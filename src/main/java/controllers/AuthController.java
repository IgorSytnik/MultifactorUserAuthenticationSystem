package controllers;

import MFA.Manager;
import MFA.SecretShare;
import exceptions.RegistrationException;
import pojo.LoginClass;
import pojo.UserBase;
import services.ShareSenderService;
import services.user.UserService;
import org.springframework.data.domain.Example;

import javax.security.auth.login.LoginException;
import javax.validation.constraints.NotEmpty;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The base for the <i>authentication controller</i> class.
 *
 * <p>Extend this method to implement it to your application.<br>
 * If you are using Spring Framework the class that extends
 * {@link AuthController} should be annotated with
 * {@link org.springframework.stereotype.Controller}.
 *
 * @param <U> user type. Must extend {@link UserBase}.
 * @author Igor Sytnik
 */
public abstract class AuthController<U extends UserBase<?>> {

    protected abstract UserService<U, ?> getUserService();
    protected abstract ShareSenderService getShareSenderService();
    private Constructor<U> userConstructorNoPar;

    {
        try {
            Class<U> userClass = (Class<U>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
            this.userConstructorNoPar = userClass.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logins the user with credentials given in <b>loginClass</b>.
     *
     * @param loginClass class that has username and password fields with
     *                   according getters and setters.
     * @return true if login was successful, otherwise false.
     * @throws LoginException if user wasn't found.
     */
    public boolean loginShares(LoginClass loginClass) throws LoginException {
        U user = getUserService().findByUsername(loginClass.getUsername());
        List<String> list = loginClass.getPasswordFields()
                .stream()
                .filter(passwordField -> !passwordField.isEmpty())
                .collect(Collectors.toList())
        ;

        Manager manager = new Manager(user.getPrime());
        SecretShare[] shares = getShares(list, Pattern.compile("(\\d+)-(\\d+)"));
        user.setPassword(manager.getAccess(shares).toString());
        return getUserService().getAccess(user);
    }

    /**
     * Get the authorised user.
     *
     * @return {@link UserService}'s user object of class {@code U}.
     * @throws GeneralSecurityException if {@link AuthController#isAuthorised()}
     * value is {@code false}.
     * @see U
     */
    public U getUser() throws GeneralSecurityException {
        if (!isAuthorised())
            throw new GeneralSecurityException("User is not authorised");
        return getUserService().getUser();
    }

    /**
     * Check if user is authorised.
     *
     * @return {@link AuthController#getUserService()}'s
     * <b>isAuthorised</b> {@code boolean} value.
     */
    public boolean isAuthorised() {
        return getUserService().isAuthorised();
    }

    /**
     * Checks if user <b>user</b> exists and then returns {@link UserBase#getSharesNeeded()}
     *
     * <p>Checks by example if the <b>user</b> exists. Looks for the user by its username.
     *
     * @param user user to make example of. {@link UserService#checkIfExists(UserBase)}
     *             will search user by example, meaning only by
     *             attributes that are not <i>null</i>.
     * @return number of passwords needed for authentication of the user <b>user</b>.
     * @throws GeneralSecurityException if user wasn't found.
     * @see org.springframework.data.jpa.repository.JpaRepository#exists(Example)
     */
    public Integer checkAndGetNumberOfPasswordsNeeded(U user) throws GeneralSecurityException {
        if (!getUserService().checkIfExists(user))
            throw new GeneralSecurityException("Couldn't find user.");
        return getUserService().findByUsername(user.getUsername()).getSharesNeeded();
    }

    /**
     * Checks if user <b>user</b> exists and then returns it.
     *
     * <p>Checks by example if the <b>user</b> exists. Looks for the user by its username.
     *
     * @param user user to make example of. {@link UserService#checkIfExists(UserBase)}
     *             will search user by example, meaning only by
     *             attributes that are not <i>null</i>.
     * @throws GeneralSecurityException if user wasn't found.
     */
    public U checkAndGetUser(U user) throws GeneralSecurityException {
        if (!getUserService().checkIfExists(user))
            throw new GeneralSecurityException("Couldn't find user.");
        return getUserService().findByUsername(user.getUsername());
    }

    /**
     * Checks if the <b>user</b> has email sending enabled by calling the
     * {@link UserBase#getEmailingEnabled()} method.
     *
     * <p>Checks by example if the <b>user</b> exists. Looks for the user by its username.
     *
     * @param user user to make example of. {@link UserService#checkIfExists(UserBase)}
     *             will search user by example, meaning only by
     *             attributes that are not <i>null</i>.
     * @return {@code true} if <b>user</b> has emailing enabled, otherwise {@code false}.
     * @throws GeneralSecurityException if user wasn't found.
     * @see org.springframework.data.jpa.repository.JpaRepository#exists(Example)
     */
    public Boolean checkEmailingEnabled(U user) throws GeneralSecurityException {
        if (!getUserService().checkIfExists(user))
            throw new GeneralSecurityException("Couldn't find user.");
        return getUserService().findByUsername(user.getUsername()).getEmailingEnabled();
    }

    /**
     * Sends saved share through email to <b>user</b>.
     *
     * <p>Checks by example if the <b>user</b> exists. Looks for the user by its username.
     *
     * @param user user to make example of. {@link UserService#checkIfExists(UserBase)}
     *             will search user by example, meaning only by
     *             attributes that are not <i>null</i>.
     * @throws Exception if user wasn't found or if there is something
     * wrong with emailing.
     * @see ShareSenderService#sendShareEmail(UserBase)
     */
    public void sendShare(U user)
            throws Exception {
        if (!getUserService().checkIfExists(user))
            throw new LoginException("Couldn't find user.");
        getShareSenderService().sendShareEmail(getUserService().findByUsername(user.getUsername()));
    }

    /**
     * Registers user with these credentials.
     *
     * @param username user's username.
     * @param password user's password.
     * @param needed user's number of shares that are needed for authentication.
     * @param available user's number of shares will be generated.
     * @return array of {@link SecretShare} objects to share to user.
     * @throws RegistrationException if the username is already taken.
     * @throws InvocationTargetException if the constructor in {@link AuthController#userConstructorNoPar}
     * throws an exception while calling {@link Constructor#newInstance(Object...)}.
     * @throws InstantiationException if the class that declares the constructor in
     * {@link AuthController#userConstructorNoPar} represents an abstract class.
     * @throws IllegalAccessException if {@link AuthController#userConstructorNoPar} object is
     * enforcing Java language access control and the underlying
     * constructor is inaccessible.
     */
    public SecretShare[] register(@NotEmpty String username, @NotEmpty BigInteger password,
                                  @NotEmpty Integer needed, @NotEmpty Integer available)
            throws RegistrationException, InvocationTargetException, InstantiationException, IllegalAccessException {
        U user = userConstructorNoPar.newInstance();
        user.setUsername(username);
        if (getUserService().checkIfExists(user))
            throw new RegistrationException("Username already taken.");
        Manager manager = new Manager(password, needed, available);
        user.setPassword(password.toString());
        user.setSharesNeeded(needed);
        user.setSharesAvailable(available);
        user.setPrime(manager.getPrime());
        user.setEmailingEnabled(false);
        getUserService().update(user);
        return manager.getShares();
    }

    /**
     * Registers user with these credentials.
     *
     * <p>The first of the generated {@link SecretShare}s is written
     * to the user and saved to database.
     *
     * @param username user's username.
     * @param password user's password.
     * @param needed user's number of shares that are needed for authentication.
     * @param available user's number of shares will be generated.
     * @param email user's email address.
     * @return array of {@link SecretShare} objects to share to user.
     * @throws RegistrationException if the username is already taken.
     * @throws InvocationTargetException if the constructor in {@link AuthController#userConstructorNoPar}
     * throws an exception while calling {@link Constructor#newInstance(Object...)}.
     * @throws InstantiationException if the class that declares the constructor in
     * {@link AuthController#userConstructorNoPar} represents an abstract class.
     * @throws IllegalAccessException if {@link AuthController#userConstructorNoPar} object is
     * enforcing Java language access control and the underlying
     * constructor is inaccessible.
     */
    public SecretShare[] registerEmailShares(@NotEmpty String username, @NotEmpty BigInteger password,
                                           @NotEmpty Integer needed, @NotEmpty Integer available,
                                           String email)
            throws RegistrationException, InvocationTargetException, InstantiationException, IllegalAccessException {
        U user = userConstructorNoPar.newInstance();
        user.setUsername(username);
        if (getUserService().checkIfExists(user))
            throw new RegistrationException("Username already taken.");
        Manager manager = new Manager(password, needed, available);
        user.setPassword(password.toString());
        user.setSharesNeeded(needed);
        user.setSharesAvailable(available);
        user.setPrime(manager.getPrime());
        user.setEmail(email);
        user.setEmailingEnabled(true);
        user.setShareForEmail(manager.getShares()[0].toString());
        getUserService().update(user);
        return Arrays.copyOfRange(manager.getShares(), 1, manager.getShares().length);
    }

    /**
     * Finds matches in <b>list</b> by pattern <b>pattern</b>
     * and returns an array of {@link SecretShare} objects,
     * that are constructed from these matches.
     *
     * @param list list of {@link String} representing entered passwords.
     * @param pattern a {@link Pattern} object to find matches by.
     * @return array of {@link SecretShare} objects.
     * @see AuthController#findMatches(Matcher)
     */
    private SecretShare[] getShares(List<String> list, Pattern pattern) {
        SecretShare[] shares = new SecretShare[list.size()];
        for (int i = 0; i < list.size(); i++) {
            shares[i] = findMatches(pattern.matcher(list.get(i)));
        }
        return shares;
    }

    /**
     * Finds matches in <b>matches</b> parameter and returns a {@link SecretShare}
     * object, constructed from these matches.<br>
     * A helper method for {@link AuthController#getShares(List, Pattern)}.
     *
     * @param matcher object of class {@link Matcher} with pattern to parse password.
     * @return a {@link SecretShare} object constructed from matched groups in <b>matcher</b>.
     * @throws NoSuchElementException if no matches were found.
     */
    private SecretShare findMatches(Matcher matcher) throws NoSuchElementException {
        if (!matcher.find()) {
            throw new NoSuchElementException("No matches found in " + matcher);
        }
        return new SecretShare(Integer.parseInt(matcher.group(1)), new BigInteger(matcher.group(2)));
    }
}
