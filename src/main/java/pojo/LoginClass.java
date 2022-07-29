package pojo;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * A POJO class for logging in a user by properties in this class.
 *
 * @see controllers.AuthController#loginShares(LoginClass) it is used here.
 * @author Igor Sytnik
 */
public class LoginClass {
    @NotEmpty
    private String username;
    @NotEmpty
    private List<String> passwordFields;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getPasswordFields() {
        return passwordFields;
    }

    public void setPasswordFields(List<String> passwordFields) {
        this.passwordFields = passwordFields;
    }
}
