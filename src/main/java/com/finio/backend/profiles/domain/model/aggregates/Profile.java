package com.finio.backend.profiles.domain.model.aggregates;

import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profile_id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Username must be less than 100 characters")
    private String name;

    @NotNull
    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(precision = 10, scale = 2)
    private BigDecimal saving_percentage;

    @Column(name = "allow_ml_analysis", unique = true)
    private Boolean allow_ml_analysis;

    @Column(name = "allow_push_notifications", unique = true)
    private Boolean allow_push_notifications;

    @Column(name = "use_biometrics", unique = true)
    private Boolean use_biometrics;

    public Profile() {}

    public Profile(String name, Long user_id, BigDecimal saving_percentage, Boolean allow_ml_analysis, Boolean allow_push_notifications, Boolean use_biometrics) {
        this.name = name;
        this.userId = user_id;
        this.saving_percentage = saving_percentage;
        this.allow_ml_analysis = allow_ml_analysis;
        this.allow_push_notifications = allow_push_notifications;
        this.use_biometrics = use_biometrics;
    }

    public Profile(String name, Long user_id) {
        this.name = name;
        this.userId = user_id;
    }
}