package com.finio.backend.iam.application.internal;

import com.finio.backend.iam.domain.model.aggregates.PasswordResetToken;
import com.finio.backend.iam.domain.model.aggregates.User;
import com.finio.backend.iam.infrastructure.persistence.jpa.repositories.PasswordResetTokenRepository;
import com.finio.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetServiceImpl {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;

    public PasswordResetServiceImpl(UserRepository userRepository,
                                    PasswordResetTokenRepository tokenRepository,
                                    JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public void generateAndSendCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("El correo ingresado no corresponde a ningún usuario registrado."));

        tokenRepository.deleteByUserId(user.getId());

        String code = String.format("%06d", new Random().nextInt(999999));

        PasswordResetToken resetToken = new PasswordResetToken(code, user.getId(), 10);
        tokenRepository.save(resetToken);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Código de Verificación de Seguridad - Finio");
        message.setText("Hola,\n\nHas solicitado el restablecimiento de tu contraseña en Finio.\n" +
                "Tu código de verificación de un solo uso (OTP) es:\n\n" +
                "👉 " + code + " 👈\n\n" +
                "Este código tiene una vigencia estricta de 10 minutos por motivos de seguridad. " +
                "Si no solicitaste este cambio, puedes ignorar este mensaje.\n\n" +
                "Atentamente,\nEl equipo de Finio.");

        mailSender.send(message);
    }

    @Transactional
    public boolean validateCode(String email, String code) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenCodeAndUserId(code, user.getId());

        if (tokenOpt.isPresent()) {
            PasswordResetToken token = tokenOpt.get();
            if (!token.isExpired()) {
                tokenRepository.delete(token);
                return true;
            }
        }
        return false;
    }
}