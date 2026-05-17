package yoyo.inventory.entities;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import yoyo.inventory.enums.CustomerStatus;
import yoyo.inventory.enums.CustomerType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerCode;

    private String fullName;

    private String gender;

    private LocalDate birthDate;

    private String phone;

    private String email;

    private String address;

    private String city;

    private String country;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    @Column(precision = 25, scale = 4)
    private BigDecimal creditLimit;

    @Column(precision = 25, scale = 4)
    private BigDecimal currentDebt;

    private Integer rewardPoint;

    private String note;

    private String createdBy;

    private String updatedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}