package com.zica.example.cryptography;

import com.zica.example.service.RSACryptographyServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class RSACryptographyServiceTest {

    @ParameterizedTest(name = "encrypting and decrypting string \"{2}\"")
    @CsvFileSource(resources = "/encrypt-test.csv")
    void encryptTest(String publicKey, String privateKey, String toEncrypt) throws Exception {
       var cryptographyService = new RSACryptographyServiceImpl(publicKey, privateKey);

       var encrypted = cryptographyService.encrypt(toEncrypt);
       // I had to call decrypt in other to test encrypt
       var decrypted = cryptographyService.decrypt(encrypted);

       Assert.assertEquals(toEncrypt, decrypted);
    }

    @Test
    void invalidKeysTest() {
        Assertions.assertThrows(Exception.class, () ->
                new RSACryptographyServiceImpl("something", "nothing"));
    }


    @ParameterizedTest(name = "decrypting -> should yield \"{3}\"")
    @CsvFileSource(resources = "/decrypt-test.csv")
    void decryptTest(String publicKey, String privateKey, String encrypted, String shouldBe) throws Exception {
       var cryptographyService = new RSACryptographyServiceImpl(publicKey, privateKey);
       var decrypted = cryptographyService.decrypt(encrypted);

       Assert.assertEquals(shouldBe, decrypted);
    }

    @ParameterizedTest(name = "decrypting invalid strings -> should raise exceptions")
    @CsvFileSource(resources = "/decrypt-test-invalid-strings.csv")
    void decryptInvalidStringTest(String publicKey, String privateKey, String encrypted) throws Exception {
       var cryptographyService = new RSACryptographyServiceImpl(publicKey, privateKey);
       Assertions.assertThrows(Exception.class, () -> cryptographyService.decrypt(encrypted));
    }
}