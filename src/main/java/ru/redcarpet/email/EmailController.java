package ru.redcarpet.email;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.redcarpet.email.dto.EmailRequest;

@RestController
@RequestMapping("/email")
public class EmailController {
    
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(
                request.getTo(),
                request.getSubject(),
                request.getBody()
            );
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Failed to send email: " + e.getMessage());
        }
    }
}
