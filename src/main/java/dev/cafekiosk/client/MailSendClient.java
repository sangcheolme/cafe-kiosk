package dev.cafekiosk.client;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MailSendClient {

    public boolean sendEmail(String fromEmail, String toEmail, String title, String content) {
        // 메일 전송
        log.info("메일 전송 => fromEmail: {}, toEmail: {}, title: {}, content: {}", fromEmail, toEmail, title, content);
        return true;
    }

    public void testA() {
        log.info("testA");
    }

    public void testB() {
        log.info("testB");
    }

    public void testC() {
        log.info("testC");
    }

}
