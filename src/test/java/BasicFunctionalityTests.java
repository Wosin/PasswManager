import com.company.database.TextFileUserDatabase;
import com.company.model.Credentials;
import com.company.model.StorageUser;
import com.company.storage.TextFileStorage;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

import static org.junit.Assert.*;
public class BasicFunctionalityTests {
    File testFile;
    TextFileUserDatabase userDatabase;
    @Before
    public void before() throws IOException {
        BasicConfigurator.configure();
        testFile = new File("usersTest.json");
        testFile.createNewFile();
        userDatabase = new TextFileUserDatabase(testFile);
    }

    @Test
    public void testAddsUserToBase() throws GeneralSecurityException{
        assertTrue(userDatabase.registerUser("Test", "Test"));
        Optional<StorageUser> userOptional = userDatabase.getUser("Test","Test");
        assertTrue(userOptional.isPresent());
        StorageUser user = userOptional.get();
        assertEquals(user.getStoragePassword(), "Test");
        assertEquals(user.getStorageUsername(), "Test");
        assertTrue(user.getStorageKey().length > 0);
        assertFalse(user.getSalt().isEmpty());

    }

    @Test
    public void testPreventsAddingSameUserTwice() throws GeneralSecurityException {
        assertTrue(userDatabase.registerUser("Test", "Test"));
        assertFalse(userDatabase.registerUser("Test", "Test"));
    }

    @Test
    public void testCreatesUserStorage() throws GeneralSecurityException {
        assertTrue(userDatabase.registerUser("Test", "Test"));
        Optional<StorageUser> userOptional = userDatabase.getUser("Test", "Test");
        StorageUser user = userOptional.get();
        TextFileStorage testStorage = new TextFileStorage(user.getStorageUsername());
        File file = new File("Test.json");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testFailsOnImproperPassword() throws GeneralSecurityException {
        assertTrue(userDatabase.registerUser("Test", "Test"));
        Optional<StorageUser> userOptional = userDatabase.getUser("Test", "BadTest");
        assertFalse(userOptional.isPresent());
    }

    @Test
    public void testAddsCredentialsToStorage() throws GeneralSecurityException {
        assertTrue(userDatabase.registerUser("Test", "Test"));
        Optional<StorageUser> userOptional = userDatabase.getUser("Test", "Test");
        StorageUser user = userOptional.get();
        TextFileStorage testStorage = new TextFileStorage(user.getStorageUsername());
        File file = new File("Test.json");
        assertTrue(file.exists());
        Credentials credentials = new Credentials("admin", "admin");
        testStorage.storeForLoggingPoint("admin@gmail.com", credentials, user);
        Optional<Credentials> credentialsOptional = testStorage.getForLoggingPoint("admin@gmail.com", user);
        assertTrue(credentialsOptional.isPresent());
        Credentials receivedCredentials = credentialsOptional.get();
        assertEquals(receivedCredentials.getPassword(), credentials.getPassword());
        assertEquals(receivedCredentials.getUsername(), credentials.getUsername());
        file.delete();

    }

    @After
    public void cleanup() {
        testFile.delete();
    }

}
