package no.hiof.webframework.Security;
//Scenario 3.3
public class UserController {
    /**
     * Register a new user with the given username and password
     * @param username the username for the new user
     * @param password the password for the new user*/
    public void registerNewUser(String username, String password) {
    }

    /**
     * Generates a random salt*/
    public void generateSalt() {
    }

    /**
     * Encrypts the password with the salt
     * @param password the password to be encrypted
     * @param salt used to add an extra layer of security to the password encryption process*/
    public void encryptPassword(String password, byte[] salt) {
    }

    /**
     * Saves a new user to the database with the given username, salt, and encrypted password.
     * @param username the username for the new user
     * @param salt the salt used to encrypt the user's password
     * @param encryptedPassword the encrypted password for the new user
     */
    public void saveUserToDatabase(String username, byte[] salt, byte[] encryptedPassword) {
    }

}