package services.user;

import pojo.UserBase;
import org.springframework.data.domain.Example;

import javax.security.auth.login.LoginException;

/**
 * The base for the <i>user service</i> class.
 *
 * <p>Extend this method to implement it to your application.<br>
 * If you are using Spring Framework the class that extends
 * {@link UserService} should be annotated with
 * {@link org.springframework.stereotype.Service}.
 *
 * @param <U> user type. Must extend UserBase.
 * @param <ID> user id type that <b>U</b> has.
 * @author Igor Sytnik
 */
public abstract class UserService<U extends UserBase<ID>, ID> extends Common<U, ID> {
    /**
     * By design, should be an authorised user.
     */
    protected U user;
    /**
     * By design, should be an indication if user is authorised.
     */
    protected boolean authorised;

    /**
     * Check if the user <b>user</b> exists in the database.
     *
     * <p>Will search user by example, meaning only by
     * attributes that are not <i>null</i>.
     *
     * @param user user to make example of.
     * @return {@code true} if user exists, {@code false}, otherwise.
     */
    public boolean checkIfExists(U user) {
        return getRepository().exists(Example.of(user));
    }

    /**
     * A method that should be called when logging in.
     *
     * @param user user that trying to get access.
     * @return {@code true} if user authentication is successful,
     * {@code false}, otherwise.
     * @throws LoginException in case of authentication/authorization problems.
     */
    public abstract boolean getAccess(U user) throws LoginException;

    /**
     * A getter for {@link UserService#authorised}.
     *
     * @return {@link UserService#authorised}.
     */
    public boolean isAuthorised() {return this.authorised;}

    /**
     * A getter for {@link UserService#user}.
     *
     * @return {@link UserService#user}.
     */
    public U getUser() {return this.user;}

    /**
     * Searches for a user by username.
     *
     * @param username to search by.
     * @return a found user.
     */
    public abstract U findByUsername(String username);
}
