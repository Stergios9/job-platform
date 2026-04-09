package com.example.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationRequest(String toEmail, String workerName, Long applicationId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Νέα Αίτηση - Έλεγχος Δικαιολογητικών");
        message.setText("Ο χρήστης " + workerName + " υπέβαλε αίτηση. " +
                "Παρακαλώ συνδεθείτε στο panel για να ελέγξετε την ταυτότητα και το πιστοποιητικό υγείας. " +
                "ID Αίτησης: " + applicationId);
        mailSender.send(message);
    }
}