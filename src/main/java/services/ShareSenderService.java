package services;

import pojo.UserBase;

/**
 * Class for sending shares.
 * <p>Uses {@link MailService} to send email.
 *
 * @author Igor Sytnik
 */
public abstract class ShareSenderService {

    public abstract MailService getMailService();

    /**
     * A method for sending shares to <b>user</b>.
     *
     * @param user a user to send shares to.
     * @throws Exception
     */
    public abstract void sendShareEmail(UserBase<?> user) throws Exception;
}