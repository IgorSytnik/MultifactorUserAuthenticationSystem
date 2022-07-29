package services;

import pojo.UserBase;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

/**
 * Class responsible for email sending services.
 *
 * @author Igor Sytnik
 */
public abstract class MailService {

    public abstract JavaMailSender getMailSender();

    /**
     * Sends an email to the user.
     *
     * @param user a {@link UserBase} object to email.
     * @param content content of the email.
     *                Content type {@code "text/html"} is applied.
     * @param subject subject of the email.
     * @throws MessagingException in case of errors while calling
     * {@link MimeMessageHelper#setTo(String)},
     * {@link MimeMessageHelper#setSubject(String)} or
     * {@link MimeMessageHelper#setText(String, boolean)} methods.
     * @throws NullPointerException if <b>user</b>'s email is <i>null</i>.
     */
    public void sendEmailToUser(UserBase<?> user, String content, String subject)
            throws MessagingException, NullPointerException {
        if (Objects.isNull(user.getEmail())) {
            throw new NullPointerException("User has no email address");
        }
        MimeMessage message = getMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(content, true);

        getMailSender().send(message);
    }
}