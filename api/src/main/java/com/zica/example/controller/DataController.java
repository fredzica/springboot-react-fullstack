package com.zica.example.controller;

import com.zica.example.api.DataApi;
import com.zica.example.model.Data;
import com.zica.example.model.NewData;
import com.zica.example.repository.DataRepository;
import com.zica.example.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DataController implements DataApi {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private DataService dataService;

    /**
     * Retrieves all existing data
     */
    @Override
    public ResponseEntity<List<Data>> retrieveData() {
        var data = dataRepository.findAll().stream()
                .map(this::convertDbDataToApiData)
                .collect(Collectors.toList());

        return ResponseEntity.ok(data);
    }

    /**
     * Finds the data value of one specific record
     */
    @Override
    public ResponseEntity<Data> getDecryptedData(Long id) {
        // handles Optional.empty
        return ResponseEntity.of(
                this.dataService.getDecryptedData(id)
                        .map(this::convertDbDataToApiData)
        );
    }

    /**
     * Saves the new data inserted by the user
     */
    @Override
    public ResponseEntity<Data> saveData(@Valid NewData newData) {
        var dbData = this.dataService.saveData(newData);

        var toReturn = this.convertDbDataToApiData(dbData);
        return ResponseEntity.status(HttpStatus.CREATED).body(toReturn);
    }

    /**
     * Updates an existing data.
     */
    @Override
    public ResponseEntity<Data> updateData(Long id, @Valid NewData data) {
        return ResponseEntity.of(
                dataService.updateDataValue(id, data).stream()
                        .map(this::convertDbDataToApiData)
                        .findFirst()
        );
    }

    private Data convertDbDataToApiData(com.zica.example.db.Data dbData) {
        var apiData = new Data();
        apiData.setId(dbData.getId());
        apiData.setData(dbData.getData());
        return apiData;
    }
}
