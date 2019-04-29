package com.zica.example.service;

import com.zica.example.db.Data;
import com.zica.example.exception.CryptographyException;
import com.zica.example.model.NewData;
import com.zica.example.repository.DataRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class DataServiceTest {

    @Autowired
    private DataService service;

    @MockBean
    private DataRepository repository;

    @MockBean
    private RSACryptographyService cryptographyService;

    @Test
    void getDecryptedDataTest() throws Exception {
        var id = 10L;
        var encrypted = "jdodjfu9dsufuafud9sfu98";
        var decrypted = "a nice value";
        var data = new Data(id, encrypted);

        when(repository.findById(eq(id))).thenReturn(Optional.of(data));
        when(cryptographyService.decrypt(eq(encrypted))).thenReturn(decrypted);

        var returnedData = service.getDecryptedData(id);

        verify(cryptographyService).decrypt(eq(encrypted));
        verify(repository).findById(eq(id));
        assertTrue(returnedData.isPresent());
        Assertions.assertEquals(id, returnedData.get().getId());
        Assertions.assertEquals(decrypted, returnedData.get().getData());
    }

    @Test
    void getDecryptedDataUnexistentTest() throws Exception {
        var id = 10L;

        when(repository.findById(eq(id))).thenReturn(Optional.empty());

        var returnedData = service.getDecryptedData(id);

        verify(repository).findById(eq(id));
        assertTrue(returnedData.isEmpty());
    }

    @Test
    void getDecryptedDataCryptographyErrorTest() throws Exception {
        var id = 10L;
        var encrypted = "jdodjfu9dsufuafud9sfu98";
        var data = new Data(id, encrypted);

        when(repository.findById(eq(id))).thenReturn(Optional.of(data));
        when(cryptographyService.decrypt(eq(encrypted))).thenThrow(new RuntimeException("a very serious error..."));

        assertThrows(CryptographyException.class, () -> service.getDecryptedData(id));

        verify(repository).findById(eq(id));
        verify(cryptographyService).decrypt(eq(encrypted));
    }

    @Test
    void saveDataTest() throws Exception {
        var id = 2L;
        var unencrypted = "some string";
        var encrypted = "(*&S(D*&ASDSAD*(AS";
        var newData = new NewData().data(unencrypted);

        when(repository.save(any(Data.class))).thenReturn(new Data(id, encrypted));
        when(cryptographyService.encrypt(eq(unencrypted))).thenReturn(encrypted);

        var savedData = service.saveData(newData);

        verify(repository).save(any(Data.class));
        verify(cryptographyService).encrypt(eq(unencrypted));
        Assertions.assertEquals(id, savedData.getId());
        Assertions.assertEquals(encrypted, savedData.getData());
    }

    @Test
    void saveDataCryptographyErrorTest() throws Exception {
        var unencrypted = "some string";
        var newData = new NewData().data(unencrypted);

        when(cryptographyService.encrypt(eq(unencrypted))).thenThrow(new RuntimeException("a heavy exception"));

        assertThrows(CryptographyException.class, () -> service.saveData(newData));

        verify(cryptographyService).encrypt(eq(unencrypted));
    }

    @Test
    void updateDataValueTest() throws Exception{
        var id = 48L;
        var unencrypted = "Hello!!!111";
        var encrypted = "//8dsaf98sdfsad89f78asdf6s7df";
        var newData = new NewData().data(unencrypted);

        when(repository.existsById(eq(id))).thenReturn(true);
        when(repository.save(any(Data.class))).thenReturn(new Data(id, encrypted));
        when(cryptographyService.encrypt(eq(unencrypted))).thenReturn(encrypted);

        var updatedValue = service.updateDataValue(id, newData);

        verify(repository).existsById(eq(id));
        verify(repository).save(any(Data.class));
        verify(cryptographyService).encrypt(eq(unencrypted));
        assertTrue(updatedValue.isPresent());
        Assertions.assertEquals(id, updatedValue.get().getId());
        Assertions.assertEquals(encrypted, updatedValue.get().getData());

    }

    @Test
    void updateDataValueCryptographyErrorTest() throws Exception{
        var id = 48L;
        var unencrypted = "Helloooo";
        var encrypted = "//8dsaf98sdfsad89f78asdf6s7df";
        var newData = new NewData().data(unencrypted);

        when(repository.existsById(eq(id))).thenReturn(true);
        when(cryptographyService.encrypt(eq(unencrypted))).thenThrow(new RuntimeException("a weird exception"));

        assertThrows(CryptographyException.class, () -> service.updateDataValue(id, newData));

        verify(repository).existsById(eq(id));
        verify(cryptographyService).encrypt(eq(unencrypted));

    }

    @Test
    void updateDataValueNotExistsTest() {
        var id = 48L;
        var newData = new NewData();

        when(repository.existsById(eq(id))).thenReturn(false);

        var updatedValue = service.updateDataValue(id, newData);

        verify(repository).existsById(eq(id));
        assertTrue(updatedValue.isEmpty());
    }
}