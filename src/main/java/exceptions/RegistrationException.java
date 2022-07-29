package exceptions;

import java.security.GeneralSecurityException;

/**
 * @author Igor Sytnik
 */
public class RegistrationException extends GeneralSecurityException {
    public RegistrationException(String message) {
        super(message);
    }
}
