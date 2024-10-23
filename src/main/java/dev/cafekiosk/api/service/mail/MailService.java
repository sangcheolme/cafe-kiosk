package dev.cafekiosk.api.service.mail;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MailService {

    public boolean sendMail(String fromEmail, String toEmail, String title, String content) {
        return false;
    }
}
