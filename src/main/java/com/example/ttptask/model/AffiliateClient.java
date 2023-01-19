package com.example.ttptask.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "AFFILIATE_CLIENT_MAP")
@Data
@EqualsAndHashCode(of = {"clientId"})
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class AffiliateClient {

	@Id
	private UUID clientId;

	private String referralCode;
	private UUID clickId;
	private String clientIp;
	private String userAgent;

	@CreatedDate
	private OffsetDateTime creationDate;

	@Version
	private Integer version;
}
