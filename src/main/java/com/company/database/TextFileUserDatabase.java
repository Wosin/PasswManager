package com.company.database;

import com.company.model.StorageUser;
import com.company.model.UserEntry;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Optional;

import static com.company.crypto.CipheringService.*;
import static com.company.json.JSONSerializer.deserializeUserEntry;
import static com.company.json.JSONSerializer.serializeUser;
public class TextFileUserDatabase implements UserDatabase {

    public static Logger log = Logger.getLogger(TextFileUserDatabase.class);

    public TextFileUserDatabase(File file) {
        this.file = file;
    }

    File file;

    public boolean registerUser(String username, String password) throws  GeneralSecurityException {
        if(getUserEntry(username).isPresent()){
            log.info("User already present in file!");
            return false;
        } else {
            String hashedPsw = hashPassword(password);
            byte[] salt = generateSalt();
            SecretKey key = generateKeyFromPassword(password, salt);
            byte[] storageKeyCiphered = generateSecretKey(key);
            String saltString = Hex.encodeHexString(salt);
            UserEntry userEntry = new UserEntry(username, hashedPsw, saltString, storageKeyCiphered);
            addUserEntry(userEntry);
            return true;
        }
    }

    public Optional<StorageUser> getUser(String username, String password) {

        Optional<UserEntry> userEntryOptional = getUserEntry(username);
        if(!userEntryOptional.isPresent()){
            return Optional.empty();
        }

        UserEntry userEntry = userEntryOptional.get();
        if(!checkHash(userEntry.getStoragePasswordHashed(), password)) {
            log.debug("Provided password is not valid!");
            return Optional.empty();
        }
        StorageUser user = new StorageUser(password, userEntry);
        return Optional.of(user);
    }



    private Optional<UserEntry> getUserEntry(String username) {
        try ( BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String entry = reader.readLine();
            UserEntry userEntry;
            while (entry != null) {
                try {
                    userEntry = deserializeUserEntry(entry);
                } catch (ParseException | DecoderException e) {
                    log.debug("Failed to decode data from String!");
                    return Optional.empty();
                }
                if (userEntry.getStorageUsername().equals(username)) {
                    return Optional.of(userEntry);
                } else {
                    entry = reader.readLine();
                }
            }
            } catch (IOException ioexc) {
            log.debug("Error while reading data from file!", ioexc);
            return Optional.empty();
        }
        return Optional.empty();
    }

    private void addUserEntry(UserEntry userEntry) {

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file,true))) {
            String userEntrySerialized = serializeUser(userEntry);
            writer.write(userEntrySerialized);
            writer.newLine();
            writer.close();
            log.info("User added to database");
        } catch (IOException ioexception) {
            log.debug("Failed to add new user to textfile!", ioexception);
            throw new RuntimeException("Failed to add user to database", ioexception);
        }
    }
}
