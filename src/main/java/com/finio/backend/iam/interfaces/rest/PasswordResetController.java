package com.finio.backend.iam.interfaces.rest;

import com.finio.backend.iam.application.internal.PasswordResetServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class PasswordResetController {

    private final PasswordResetServiceImpl resetService;

    public PasswordResetController(PasswordResetServiceImpl resetService) {
        this.resetService = resetService;
    }

    @PostMapping("/request-code")
    public ResponseEntity<String> requestResetCode(@RequestParam String email) {
        try {
            resetService.generateAndSendCode(email);
            return ResponseEntity.ok("Código de verificación enviado con éxito.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno al despachar el correo electrónico: " + e.getMessage());
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isValid = resetService.validateCode(email, code);
        if (!isValid) {
            return ResponseEntity.badRequest().body("El código ingresado es incorrecto, ya fue utilizado o ha expirado.");
        }
        return ResponseEntity.ok("Código verificado de forma exitosa. Acceso concedido al cambio de credenciales.");
    }
}