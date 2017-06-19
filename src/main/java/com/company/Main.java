package com.company;

import com.company.database.TextFileUserDatabase;
import com.company.model.Credentials;
import com.company.model.StorageUser;
import com.company.storage.TextFileStorage;
import org.apache.commons.codec.DecoderException;
import org.apache.log4j.BasicConfigurator;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, GeneralSecurityException, DecoderException, ParseException {

        BasicConfigurator.configure();
        TextFileUserDatabase database = new TextFileUserDatabase(new File("./users.json"));
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please type REG to register new user or LOG to login using previously created username");
        String command = scanner.next();
        System.out.println("Please provide your username");
        String username = scanner.next();
        System.out.println("Please provide password for username : " + username);
        String password = scanner.next();
        if(command.toLowerCase().equals("reg")) {
          if(database.registerUser(username, password)) {
              System.out.println("User Registered Sucessfully!");
          } else {
              System.out.println("User already present with such username! Trying to log in...");
          }
        }
        Optional<StorageUser> user = database.getUser(username, password);
        if(!user.isPresent()) {
            System.out.println("Login Failed! Closing App!");
            System.exit(1);
        }
        TextFileStorage storage = new TextFileStorage(username);
        while(true) {

            System.out.println("Please type ADD to add credentials for loggingPoitn or GET to get the credentials");
            String loginCommand = scanner.next();
            if(loginCommand.toLowerCase().contains("add")) {
                System.out.println("Please provide loggingPoint address or some identification name");
                String loggingPoint = scanner.next();
                System.out.println("Please provide username for loggingPoint");
                String loginUsername = scanner.next();
                System.out.println("Please provide password for loggingPoint");
                String loginPassword = scanner.next();
                Credentials credentials = new Credentials(loginUsername, loginPassword);
                storage.storeForLoggingPoint(loggingPoint, credentials, user.get());
        } else {
                System.out.println("Please provide loggingPoint to retrieve credentials");
                String loggingPoint = scanner.next();

                Optional<Credentials> credentialsOptional = storage.getForLoggingPoint(loggingPoint, user.get());
                if(!credentialsOptional.isPresent()) {
                    System.out.println("Failed to receive data from database or entry not present in file!");
                } else {
                    System.out.println(credentialsOptional.get());
                }

            }
        }
    }
}
