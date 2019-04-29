package com.zica.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


@Service
public class RSACryptographyServiceImpl implements RSACryptographyService {

    private Key publicKey;
    private Key privateKey;

    public RSACryptographyServiceImpl(@Value("${rsa-public-key}") String serializedPublicKey,
                                      @Value("${rsa-private-key}") String serializedPrivateKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        this.publicKey = this.decodeBase64ToPublicKey(serializedPublicKey);
        this.privateKey = this.decodeBase64ToPrivateKey(serializedPrivateKey);
    }

    @Override
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        var kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);

        return kpg.genKeyPair();
    }

    @Override
    public String encrypt(String data) throws NoSuchPaddingException,
                NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        var encrypted = this.applyCryptography(data.getBytes(StandardCharsets.UTF_8), this.publicKey, Cipher.ENCRYPT_MODE);
        return this.encodeToBase64(encrypted);
    }

    @Override
    public String decrypt(String data) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        var decodedData = this.decodeFromBase64(data);
        var decrypted = this.applyCryptography(decodedData, this.privateKey, Cipher.DECRYPT_MODE);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private byte[] applyCryptography(byte[] data, Key key, int cipherMode) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        var cipher = Cipher.getInstance("RSA");
        cipher.init(cipherMode, key);
        return cipher.doFinal(data);
    }

    private String encodeToBase64(byte[] toBeEncoded) {
        return Base64.getEncoder().encodeToString(toBeEncoded);
    }

    private byte[] decodeFromBase64(String toBeDecoded) {
        return Base64.getDecoder().decode(toBeDecoded.getBytes(StandardCharsets.UTF_8));
    }

    private Key decodeBase64ToPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var keyBytes = this.decodeFromBase64(key);
        var keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private Key decodeBase64ToPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var keyBytes = this.decodeFromBase64(key);
        var keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }
}
