package com.company.crypto;

import com.company.model.Credentials;
import com.company.model.CredentialsEntry;
import com.company.model.StorageUser;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class CipheringService {

    private static byte[] cipherData(byte[] input, SecretKey key, IvParameterSpec iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("Blowfish/CFB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        return cipher.doFinal(input);
    }

    private static byte[] cipherData(byte[] input, SecretKey key) throws GeneralSecurityException {
       return cipherData(input, key, new IvParameterSpec(new byte[8]));
    }

    private static byte[] decipherData(byte[] input, SecretKey key, byte[] iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("Blowfish/CFB/PKCS5Padding");
        IvParameterSpec ivec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivec);

        return cipher.doFinal(input);
    }

    public static Credentials decipherCredentials(CredentialsEntry credentialsEntry, StorageUser user)
            throws GeneralSecurityException, DecoderException {
        String credentials = decipherWithUserPassword(credentialsEntry.getCredentialsCiphered(),user, credentialsEntry.getIv());
        String[] array = credentials.split(":::");

        return  new Credentials(array);
    }
    public static byte[] generateSecretKey(SecretKey key) throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("Blowfish");
        keygen.init(128);
        Key cipheringKey =keygen.generateKey();
        return cipherData(cipheringKey.getEncoded(), key);
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        return random.generateSeed(16);
    }

    public static boolean checkHash(String hash, String password) {
       return BCrypt.checkpw(password, hash);
    }

    public static IvParameterSpec generateIv(){
        SecureRandom random = new SecureRandom();
        return new IvParameterSpec(random.generateSeed(8));
    }

    private static String decipherWithUserPassword(String input, StorageUser user, IvParameterSpec iv)
            throws  GeneralSecurityException, DecoderException {
        String output;

        byte[] storageKeyBytes = decipherKey(user);
        SecretKey key = new SecretKeySpec(storageKeyBytes, "Blowfish");
        byte[] data = decipherData(Hex.decodeHex(input.toCharArray()), key, iv.getIV());
        output = new String(data);

        return output;
    }

    public static String cipherWithUserPassword(String input, StorageUser user, IvParameterSpec iv)
            throws GeneralSecurityException, DecoderException {
        String output;

            byte[] storageKeyBytes = decipherKey(user);
            SecretKey key = new SecretKeySpec(storageKeyBytes, "Blowfish");
            byte[] data = cipherData(input.getBytes(), key, iv);
            output = Hex.encodeHexString(data);
        return output;
    }

    private static byte[] decipherKey(StorageUser user) throws GeneralSecurityException, DecoderException {
        String password = user.getStoragePassword();
        byte[] salt = Hex.decodeHex(user.getSalt().toCharArray());
        byte[] cipheredKey = user.getStorageKey();
        SecretKey key = generateKeyFromPassword(password, salt);

        return decipherData(cipheredKey, key, new byte[8]);
    }

    public static SecretKey generateKeyFromPassword(String password, byte[] salt) throws GeneralSecurityException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);

        return new SecretKeySpec(tmp.getEncoded(), "Blowfish");
    }

    public static String hashPassword(String password) throws GeneralSecurityException {
       return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
