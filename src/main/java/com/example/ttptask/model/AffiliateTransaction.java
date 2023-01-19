package com.example.ttptask.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "AFFILIATE_TRANSACTIONS")
@Data
@EqualsAndHashCode(of = {"conversionId"})
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class AffiliateTransaction {

	@Id
	private Integer conversionId;

	private UUID clientId;
	private String referralCode;
	private UUID orderId;
	private String currency;
	private BigDecimal orderAmount;
	private BigDecimal conversionAmount;
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@CreatedDate
	private OffsetDateTime creationDate;

	@Version
	private Integer version;
}
