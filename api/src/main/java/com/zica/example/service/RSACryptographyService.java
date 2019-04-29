package com.zica.example.service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

/**
 * The aim of this class is to provide a clear interface for those in need to
 * encrypt/decrypt a string with RSA. It tries to hide all the unnecessary
 * cryptography specifics from the caller.
 */
public interface RSACryptographyService {
    KeyPair generateKeyPair() throws NoSuchAlgorithmException;

    /**
     * Encrypts a String
     * @param data The String to be encrypted
     * @return the value encrypted with RSA in a Base64 encoded String
     */
    String encrypt(String data) throws NoSuchPaddingException,
                NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException;

    /**
     * Decrypts a string
     * @param data A value encrypted with RSA represented as a Base64 encoded string
     * @return The decrypted string
     */
    String decrypt(String data) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException;
}
