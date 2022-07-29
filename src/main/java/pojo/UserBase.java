package pojo;

import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.math.BigInteger;
import java.util.Objects;

/**
 * The parent class for a user entity in the system.<br>
 * Transfers all its properties to deriving classes that are
 * annotated with {@link Entity}.
 *
 * @param <ID> user's id class.
 * @author Igor Sytnik
 */
@MappedSuperclass
abstract public class UserBase<ID> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected ID id;
    @Column(name = "username", nullable = false)
    protected String username;
    @Column(name = "password", nullable = false)
    protected String password;
    @Email
    @Column(name="email")
    protected String email;
    @Column(name = "prime", nullable = false)
    protected BigInteger prime;
    @Column(name = "shares_needed", nullable = false)
    protected Integer sharesNeeded;
    @Column(name = "shares_available", nullable = false)
    protected Integer sharesAvailable;
    @Column(name = "share_for_email")
    protected String shareForEmail;
    @Column(name = "emailing_enabled", nullable = false)
    protected Boolean emailingEnabled;

    public UserBase(String username, String password, Integer sharesNeeded) {
        setUsername(username);
        setSharesNeeded(sharesNeeded);
        setPassword(password);
    }

    public UserBase() {

    }

    public boolean checkPassword(String password) {
        return DigestUtils.sha1Hex(password).equals(this.password);
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = DigestUtils.sha1Hex(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigInteger getPrime() {
        return prime;
    }

    public void setPrime(BigInteger prime) {
        this.prime = prime;
    }

    public Integer getSharesNeeded() {
        return sharesNeeded;
    }

    public void setSharesNeeded(Integer sharesNeeded) {
        this.sharesNeeded = sharesNeeded;
    }

    public Integer getSharesAvailable() {
        return sharesAvailable;
    }

    public void setSharesAvailable(Integer sharesAvailable) {
        this.sharesAvailable = sharesAvailable;
    }

    public String getShareForEmail() {
        return shareForEmail;
    }

    public void setShareForEmail(String shareForEmail) {
        this.shareForEmail = shareForEmail;
    }

    public Boolean getEmailingEnabled() {
        return emailingEnabled;
    }

    public void setEmailingEnabled(Boolean emailingEnabled) {
        this.emailingEnabled = emailingEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserBase)) return false;
        UserBase<?> userBase = (UserBase<?>) o;
        return getUsername().equals(userBase.getUsername()) &&
                getPassword().equals(userBase.getPassword()) &&
                Objects.equals(getEmail(), userBase.getEmail()) &&
                getPrime().equals(userBase.getPrime()) &&
                getSharesNeeded().equals(userBase.getSharesNeeded()) &&
                getSharesAvailable().equals(userBase.getSharesAvailable()) &&
                Objects.equals(getShareForEmail(), userBase.getShareForEmail()) &&
                getEmailingEnabled().equals(userBase.getEmailingEnabled());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getUsername(),
                getPassword(),
                getEmail(),
                getPrime(),
                getSharesNeeded(),
                getSharesAvailable(),
                getShareForEmail(),
                getEmailingEnabled());
    }
}
