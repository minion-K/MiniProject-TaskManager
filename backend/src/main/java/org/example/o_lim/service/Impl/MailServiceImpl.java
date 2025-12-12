package org.example.o_lim.service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.dto.mail.request.SendMailRequestDto;
import org.example.o_lim.provider.JwtProvider;
import org.example.o_lim.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final JwtProvider jwtProvider;
    @Value("${spring.mail.username}")
    private String senderEmail;

    private MimeMessage createEmail(String email, String token) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, email);

        message.setSubject("=== [Task-Manager] 이메일 인증 링크 발송 ===");
        String body = """
                    <h3>이메일 인증 링크</h3>
                    <a href="http://localhost:8080/api/v1/auth/verify?token=%s">여기를 클릭하여 인증을 완료</a>
                """.formatted(token);

        message.setText(body, "UTF-8", "html");

        return message;
    }

//    이메일 전송
    @Override
    public void sendEmail(SendMailRequestDto request) {
        try {
            String token = jwtProvider.generateEmailJwtToken(request.email());
            MimeMessage message = createEmail(request.email(), token);

            javaMailSender.send(message);

            System.out.println("인증 이메일이 전송되었습니다.");
        } catch (MessagingException | MailException e) {
            System.err.println("이메일 전송 실패" + e.getMessage());
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    @Override
    public void verifyEmail(String token) {
        String email = jwtProvider.getEmailFromJwt(token);

        System.out.println("이메일 인증이 완료되었습니다. 이메일: " + email);
    }
}
