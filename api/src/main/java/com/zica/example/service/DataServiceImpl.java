package com.zica.example.service;

import com.zica.example.db.Data;
import com.zica.example.exception.CryptographyException;
import com.zica.example.model.NewData;
import com.zica.example.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private RSACryptographyService cryptographyService;

    @Override
    public Optional<Data> getDecryptedData(Long id) {
        return this.dataRepository.findById(id)
                .map(data -> {
                    try {
                        var decryptedData = this.cryptographyService.decrypt(data.getData());
                        return new Data(id, decryptedData);
                    } catch (Exception e) {
                        throw new CryptographyException(e);
                    }
                });
    }

    @Override
    public Data saveData(NewData newData) {
        String encryptedData;
        try {
            encryptedData = cryptographyService.encrypt(newData.getData());
        } catch (Exception e) {
            throw new CryptographyException(e);
        }

        var dbData = new Data();
        dbData.setData(encryptedData);
        dbData = dataRepository.save(dbData);
        return dbData;
    }

    @Override
    public Optional<Data> updateDataValue(Long id, NewData data) {
        if (!dataRepository.existsById(id)) {
            return Optional.empty();
        }

        String encryptedData;
        try {
            encryptedData = cryptographyService.encrypt(data.getData());
        } catch (Exception e) {
            throw new CryptographyException(e);
        }

        var dbData = new Data(id, encryptedData);
        return Optional.of(dataRepository.save(dbData));
    }
}
