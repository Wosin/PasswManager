package com.company.json;


import com.company.model.CredentialsEntry;
import com.company.model.UserEntry;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.spec.IvParameterSpec;

public class JSONSerializer {

    public static String serializeUser(UserEntry userEntry) {
        JSONObject object = new JSONObject();
        String storageKeyHex = Hex.encodeHexString(userEntry.getStorageKey());
        object.put("username", userEntry.getStorageUsername());
        object.put("password", userEntry.getStoragePasswordHashed());
        object.put("storageKey", storageKeyHex);
        object.put("salt", userEntry.getSalt());

        return object.toJSONString();
    }

    public static UserEntry deserializeUserEntry(String userEntry) throws ParseException, DecoderException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(userEntry);
        String username = (String) object.get("username");
        String passwordHashed = (String) object.get("password");
        String salt = (String) object.get("salt");
        String keyHex = (String) object.get("storageKey");
        byte[] key = Hex.decodeHex(keyHex.toCharArray());

        return new UserEntry(username, passwordHashed, salt, key);
    }

    public static CredentialsEntry deserializeCredentialsEntry(String credentialsEntry) throws ParseException, DecoderException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(credentialsEntry);
        String loggingPoint = (String) object.get("loggingPoint");
        String credentialsCiphered = (String) object.get("credentials");
        String ivString = (String) object.get("iv");
        IvParameterSpec iv = new IvParameterSpec(Hex.decodeHex(ivString.toCharArray()));

        return new CredentialsEntry(credentialsCiphered, iv, loggingPoint);
    }

    public static String serializeCredentialsEntry(CredentialsEntry credentialsEntry) {
        JSONObject object = new JSONObject();
        object.put("loggingPoint", credentialsEntry.getLoggingPoint());
        IvParameterSpec iv= credentialsEntry.getIv();
        String ivString = Hex.encodeHexString(iv.getIV());
        object.put("iv",ivString);
        object.put("credentials", credentialsEntry.getCredentialsCiphered());

        return object.toJSONString();
    }
}
