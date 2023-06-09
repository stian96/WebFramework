package no.hiof.webframework.security;

/**The UserInputValidatorForLoginForm class that contains methods for validating usernames and passwords*/
public class UserInputValidatorForLoginForm {

    /**Validates username by checking the length of the field
     * @param username The username that needs to be validated*/
    public boolean validateUsername(String username) {
        int usernameLength = username.length();
        return (usernameLength >= 5 && usernameLength <= 20);
    }

    /**Validates password by checking the length of the field and whether it contains only alphanumeric characters
     *@param password The password that needs to be validated*/
    public boolean validatePassword(String password) {
        int passwordLength = password.length();
        boolean containsAlphabets = password.matches(".*[a-zA-Z].*");
        boolean containsNumbers = password.matches(".*\\d.*");
        boolean isAlphaNumericOnly = password.matches("[a-zA-Z0-9]+");
        return (passwordLength >= 5 && passwordLength <= 20 && containsAlphabets && containsNumbers && isAlphaNumericOnly);
    }

}
