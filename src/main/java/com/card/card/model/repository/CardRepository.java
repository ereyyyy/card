package com.card.card.model.repository;

import com.card.card.model.entitiy.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<CardEntity, Long> {
    CardEntity findByOwner(String owner);
}
