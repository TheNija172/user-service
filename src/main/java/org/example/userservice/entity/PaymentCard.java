package org.example.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_cards")
@EntityListeners(AuditingEntityListener.class)
public class PaymentCard extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;
    private String holder;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}