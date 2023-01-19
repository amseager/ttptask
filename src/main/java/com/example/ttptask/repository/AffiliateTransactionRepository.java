package com.example.ttptask.repository;

import com.example.ttptask.model.AffiliateTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AffiliateTransactionRepository extends JpaRepository<AffiliateTransaction, Integer> {
}
