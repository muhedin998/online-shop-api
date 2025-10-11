package com.example.online_shop.shared.service;

import com.example.online_shop.shared.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:3000");
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@shop.com");
    }

    @Test
    void sendPasswordResetEmail_Success() {
        // Arrange
        String to = "test@example.com";
        String token = "test-token-123";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail(to, token);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("noreply@shop.com", sentMessage.getFrom());
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals("Password Reset Request - Online Shop", sentMessage.getSubject());
        assertNotNull(sentMessage.getText());
        assertTrue(sentMessage.getText().contains("http://localhost:3000/reset-password?token=" + token));
    }

    @Test
    void sendPasswordResetEmail_MailSenderFails_ThrowsException() {
        // Arrange
        String to = "test@example.com";
        String token = "test-token";
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> emailService.sendPasswordResetEmail(to, token));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_ContainsResetLink() {
        // Arrange
        String to = "user@example.com";
        String token = "abc123";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail(to, token);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        String emailBody = messageCaptor.getValue().getText();
        assertTrue(emailBody.contains("reset-password?token=abc123"));
        assertTrue(emailBody.contains("expire in 1 hour"));
    }
}
