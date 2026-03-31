package com.example.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String otp) {
        String subject = "Email Verification - SkillSync";
        String content = "Welcome to SkillSync! Your verification OTP is: " + otp + "\nIt will expire in 5 minutes.";
        sendEmail(to, subject, content);
    }

    public void sendRegistrationSuccessEmail(String to, String name) {
        String subject = "Welcome to SkillSync!";
        String content = "Hello " + name + ",\nYour account has been successfully verified. Welcome to SkillSync!";
        sendEmail(to, subject, content);
    }

    public void sendLoginNotification(String to) {
        String subject = "New Login Notification";
        String content = "You have successfully logged into your SkillSync account.";
        sendEmail(to, subject, content);
    }

    public void sendOtpEmail(String to, String otp) {
        String subject = "Password Reset OTP - SkillSync";
        String content = "Your OTP for password reset is: " + otp + "\nIt will expire in 5 minutes.";
        sendEmail(to, subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
