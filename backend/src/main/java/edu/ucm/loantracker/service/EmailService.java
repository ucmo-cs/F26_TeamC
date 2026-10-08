package edu.ucm.loantracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/** Sends new customers their login. With app.mail.enabled=false it just logs the email. */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final boolean enabled;
    private final String from;

    public EmailService(ObjectProvider<JavaMailSender> mailSender,
                        @Value("${app.mail.enabled:false}") boolean enabled,
                        @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
    }

    /** @return true if the email was actually sent. */
    public boolean sendAccountEmail(String to, String name, String username, String password) {
        String subject = "Your loan account is ready";
        String body = """
                Hi %s,

                An account has been created so you can view your loan and set up automatic payments.

                Username: %s
                Password: %s

                Sign in at http://localhost:5173/login
                """.formatted(name, username, password);

        JavaMailSender sender = mailSender.getIfAvailable();
        if (!enabled || sender == null) {
            log.info("Email sending is off. Would have sent to {}:\nSubject: {}\n{}", to, subject, body);
            return false;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            sender.send(message);
            return true;
        } catch (MailException e) {
            log.warn("Could not send account email to {}: {}", to, e.getMessage());
            return false;
        }
    }
}
