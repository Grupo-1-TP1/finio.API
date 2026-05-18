package com.finio.backend.finance.domain.model.aggregates;

import com.finio.backend.finance.domain.model.commands.CreateCategoryCommand;
import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category extends AuditableAbstractAggregateRoot<Category> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(CreateCategoryCommand createCategoryCommand) {
        this.name = createCategoryCommand.name();
        this.description = createCategoryCommand.description();
    }
}
