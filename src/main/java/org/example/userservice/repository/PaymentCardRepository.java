package org.example.userservice.repository;

import org.example.userservice.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    List<PaymentCard> findByUserId(Long userId);

    @Query("SELECT c FROM PaymentCard c WHERE c.user.id = :userId AND c.active = true")
    List<PaymentCard> findActiveCardsByUserId(Long userId);

    @Query(value = "SELECT * FROM payment_cards WHERE user_id = :userId", nativeQuery = true)
    List<PaymentCard> findCardsNative(Long userId);

    long countByUserId(Long userId);

    @Modifying
    @Query("UPDATE PaymentCard c SET c.active = :active WHERE c.id = :id")
    void updateActive(Long id, Boolean active);
}