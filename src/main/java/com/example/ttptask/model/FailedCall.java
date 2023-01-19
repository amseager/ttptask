package com.example.ttptask.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "FAILED_CALLS")
@Data
@EqualsAndHashCode(of = {"clientId", "requestType"})
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class FailedCall {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "failed_call_id_seq")
	@SequenceGenerator(name = "failed_call_id_seq", sequenceName = "failed_call_id_seq", allocationSize = 1)
	private Long id;

	private UUID clientId;
	private String requestType;

	private String payload;
	private String reason;

	@CreatedDate
	private OffsetDateTime time;

	private Boolean processed;
}
