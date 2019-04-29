package com.zica.example.controller;

import com.zica.example.db.Data;
import com.zica.example.model.NewData;
import com.zica.example.repository.DataRepository;
import com.zica.example.service.DataService;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DataController.class)
class DataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataService dataService;

    @MockBean
    private DataRepository dataRepository;

    @Test
    void retrieveDataEmptyTest() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/data")).andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());

        List<Data> dataList = TestJsonConverter.jsonToList(result.getResponse().getContentAsString(), Data.class);

        verify(dataRepository).findAll();
        assertTrue(dataList.isEmpty());
    }

    @Test
    void retrieveDataTest() throws Exception {
        List<Data> toMock = List.of(new Data(1, "test"),
                new Data(2, "yet another test"), new Data(3, "some string"));
        when(dataRepository.findAll()).thenReturn(toMock);

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/data")).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        List<Data> dataList = TestJsonConverter.jsonToList(result.getResponse().getContentAsString(), Data.class);

        verify(dataRepository).findAll();
        assertEquals(toMock.get(1).getData(), dataList.get(1).getData());
    }

    @Test
    void getDecryptedEmptyDataTest() throws Exception {
        long id = 1;

        when(dataService.getDecryptedData(eq(id))).thenReturn(Optional.empty());

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/data/1/decrypted")).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());

        var json = result.getResponse().getContentAsString();

        verify(dataService).getDecryptedData(id);
        assertEquals("", json);
    }

    @Test
    void getDecryptedDataTest() throws Exception {
        long id = 2;
        String value = "some data";

        when(dataService.getDecryptedData(eq(id))).thenReturn(Optional.of(new Data(id, value)));

        var result = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/data/%d/decrypted", id))).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        var data = TestJsonConverter.jsonToObject(result.getResponse().getContentAsString(), Data.class);

        verify(dataService).getDecryptedData(id);
        assertEquals(id, data.getId());
        assertEquals(value, data.getData());
    }

    @Test
    void saveDataTest() throws Exception {
        var value = "unencrypted test value";
        var newData = new NewData();
        newData.setData(value);

        long id = 3;
        var savedData = new Data(id, value);

        when(dataService.saveData(eq(newData))).thenReturn(savedData);

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/data")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestJsonConverter.objectToJson(newData))
        ).andReturn();

        var statusCode = result.getResponse().getStatus();
        assertEquals(HttpStatus.CREATED.value(), statusCode);

        var data = TestJsonConverter.jsonToObject(result.getResponse().getContentAsString(), Data.class);

        verify(dataService).saveData(eq(newData));
        assertEquals(id, data.getId());
        assertEquals(value, data.getData());
    }

    @Test
    void saveInvalidDataTestEmptyValue() throws Exception {
        var newData = new NewData();

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/data")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestJsonConverter.objectToJson(newData))
        ).andReturn();

        var statusCode = result.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
    }

    @Test
    void saveInvalidDataTestTooLongValue() throws Exception {
        var newData = new NewData();
        // this is a limitation related to the RSA. if all the characters utilized are of 1 byte, 245 is the
        // maximum amount
        newData.setData(RandomString.make(246));

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/data")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestJsonConverter.objectToJson(newData))
        ).andReturn();

        var statusCode = result.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
    }

    @Test
    void updateDataTest() throws Exception {
        var id = 6L;
        var newValue = "a good value";
        var newData = new NewData().data(newValue);

        var data = new Data(id, newValue);

        when(dataService.updateDataValue(id, newData)).thenReturn(Optional.of(data));

        var result = mockMvc.perform(MockMvcRequestBuilders.put("/data/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestJsonConverter.objectToJson(newData))
        ).andReturn();

        var statusCode = result.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), statusCode);

        var returnedData = TestJsonConverter.jsonToObject(result.getResponse().getContentAsString(), Data.class);

        assertEquals(id, returnedData.getId());
        assertEquals(newValue, returnedData.getData());
    }

    @Test
    void updateUnexistentDataTest() throws Exception {
        var id = 6L;
        var newValue = "a good value";
        var newData = new NewData().data(newValue);

        when(dataService.updateDataValue(id, newData)).thenReturn(Optional.empty());

        var result = mockMvc.perform(MockMvcRequestBuilders.put("/data/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestJsonConverter.objectToJson(newData))
        ).andReturn();

        var statusCode = result.getResponse().getStatus();
        assertEquals(HttpStatus.NOT_FOUND.value(), statusCode);
    }

    @Test
    void updateInvalidDataTest() throws Exception {
        var id = 6L;
        var newData = new NewData();

        var result = mockMvc.perform(MockMvcRequestBuilders.put("/data/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestJsonConverter.objectToJson(newData))
        ).andReturn();

        var statusCode = result.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
    }
}