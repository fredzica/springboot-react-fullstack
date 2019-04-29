package com.zica.example.service;

import com.zica.example.db.Data;
import com.zica.example.model.NewData;

import java.util.Optional;

public interface DataService {
    /**
     * Finds the data record in the database and returns it with its decrypted value, if it exists
     * @param id database id to be searched
     * @return an Optional with the found decrypted data, if it exists
     */
    Optional<Data> getDecryptedData(Long id);

    /**
     * Encrypts the data value and inserts it in the database
     * @param newData The value to be inserted
     * @return The created record
     */
    Data saveData(NewData newData);

    /**
     * Updates the data value with the informed id. Encrypts the data before updating
     * @param id id of the object to be updated
     * @param data data to be encrypted and saved
     * @return The updated db value
     */
    Optional<Data> updateDataValue(Long id, NewData data);
}
