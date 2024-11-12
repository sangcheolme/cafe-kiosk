package dev.cafekiosk.api.service.mail;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import dev.cafekiosk.client.MailSendClient;
import dev.cafekiosk.domain.history.mail.MailSendHistory;
import dev.cafekiosk.domain.history.mail.MailSendHistoryRepository;

@RequiredArgsConstructor
@Service
public class MailService {

    private final MailSendClient mailSendClient;
    private final MailSendHistoryRepository mailSendHistoryRepository;

    public boolean sendMail(String fromEmail, String toEmail, String subject, String content) {
        boolean result = mailSendClient.sendEmail(fromEmail, toEmail, subject, content);
        if (result) {
            mailSendHistoryRepository.save(MailSendHistory.builder()
                    .fromEmail(fromEmail)
                    .toEmail(toEmail)
                    .subject(subject)
                    .content(content)
                    .build()
            );

            mailSendClient.testA();
            mailSendClient.testB();
            mailSendClient.testC();

            return true;
        }
        return false;
    }
}
