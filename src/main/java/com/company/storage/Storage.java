package com.company.storage;

import com.company.model.Credentials;
import com.company.model.StorageUser;
import org.apache.commons.codec.DecoderException;
import org.json.simple.parser.ParseException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public interface Storage {

    Optional<Credentials> getForLoggingPoint(String loggingPoint, StorageUser user);
    boolean storeForLoggingPoint(String loggingPoint, Credentials credentials, StorageUser user);
}
