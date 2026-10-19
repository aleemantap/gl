package com.project.gl.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "component",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_component_code",
            columnNames = "component_code"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Component extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "component_code",
        nullable = false,
        unique = true,
        length = 50
    )
    private String componentCode;

    @Column(
        name = "component_name",
        nullable = false,
        length = 100
    )
    private String componentName;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}