package dev.cafekiosk;

import jakarta.transaction.Transactional;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import dev.cafekiosk.client.MailSendClient;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public abstract class IntegrationTestSupport {

    @MockBean
    protected MailSendClient mailSendClient;

}
