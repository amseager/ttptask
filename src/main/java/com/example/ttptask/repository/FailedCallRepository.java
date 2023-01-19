package com.example.ttptask.repository;

import com.example.ttptask.model.FailedCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedCallRepository extends JpaRepository<FailedCall, Long> {
}
