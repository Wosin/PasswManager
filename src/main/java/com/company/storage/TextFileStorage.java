package com.company.storage;


import com.company.model.Credentials;
import com.company.model.CredentialsEntry;
import com.company.model.StorageUser;
import org.apache.log4j.Logger;

import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.util.Optional;

import static com.company.crypto.CipheringService.*;
import static com.company.json.JSONSerializer.deserializeCredentialsEntry;
import static com.company.json.JSONSerializer.serializeCredentialsEntry;

public class TextFileStorage implements Storage {

    public static Logger log = Logger.getLogger(TextFileStorage.class);
    File file;

    public TextFileStorage(String username) {
        File file = new File(username + ".json");

        try {
            file.createNewFile();
        } catch (IOException exception) {
            log.error("Failed to create  file!");
            throw new RuntimeException("Error while creating user storage", exception);
        }
        this.file = file;
    }

    public Optional<Credentials> getForLoggingPoint(String loggingPoint, StorageUser user) {
        Optional<CredentialsEntry> entryOptional = getLoggingPointEntry(loggingPoint);
        Credentials credentials;
        if (!entryOptional.isPresent()) {
            return Optional.empty();
        } else {
            try {
                credentials = decipherCredentials(entryOptional.get(), user);
            } catch (Exception e) {
                log.debug("Data was read successfully, but it can't be deserialized! ");
                return Optional.empty();
            }

            return Optional.of(credentials);
        }
    }

    private Optional<CredentialsEntry> getLoggingPointEntry(String loggingPoint) {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String entry;
            entry = reader.readLine();
            CredentialsEntry credentialsEntry;
            while (entry != null) {
                try {
                     credentialsEntry = deserializeCredentialsEntry(entry);
                } catch (Exception ex) {
                    log.debug("Failed to decode data from file!");
                    return Optional.empty();
                }
                if(credentialsEntry.getLoggingPoint().equals(loggingPoint)){
                    return Optional.of(credentialsEntry);
                } else {
                    entry = reader.readLine();
                }
            }
        } catch (IOException ioexception) {
            log.debug("Failed to read data from database", ioexception);
        }
        return Optional.empty();
    }

    public boolean storeForLoggingPoint(String loggingPoint, Credentials credentials, StorageUser user){

        if(getLoggingPointEntry(loggingPoint).isPresent()) {
            log.debug("Entry already present in database!");
            return false;
        } else {
            IvParameterSpec iv = generateIv();
            String usernameAndPassword = credentials.getUsername() + ":::" + credentials.getPassword();
            String entryCiphered;
            try {
                entryCiphered = cipherWithUserPassword(usernameAndPassword, user, iv);
            } catch (Exception e) {
                log.error("Failure while ciphering data!", e);
                return false;
            }
            CredentialsEntry entry  = new CredentialsEntry(iv, loggingPoint, entryCiphered);
            String entrySerialized = serializeCredentialsEntry(entry);

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(entrySerialized);
                writer.newLine();
                writer.flush();
                writer.close();
                log.info("Entry added to file");
                return true;
            } catch (IOException exception){
                log.debug("Failed to write data to file", exception);
                return false;
            }
        }
    }
}
