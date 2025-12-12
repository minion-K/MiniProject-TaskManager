package org.example.o_lim.service;

import jakarta.validation.Valid;
import org.example.o_lim.dto.mail.request.SendMailRequestDto;

public interface MailService {
    void sendEmail(@Valid SendMailRequestDto request);
    void verifyEmail(String token);
}
