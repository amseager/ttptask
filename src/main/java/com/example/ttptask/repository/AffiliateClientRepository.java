package com.example.ttptask.repository;

import com.example.ttptask.model.AffiliateClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AffiliateClientRepository extends JpaRepository<AffiliateClient, UUID> {
}
