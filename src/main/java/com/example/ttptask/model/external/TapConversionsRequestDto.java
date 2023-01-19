package com.example.ttptask.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class TapConversionsRequestDto {

	@JsonProperty("click_id")
	private UUID clickId;

	@JsonProperty("external_id")
	private UUID externalId;

	private BigDecimal amount;

	private String currency;

	@JsonProperty("customer_id")
	private UUID customerId;

	@JsonProperty("user_agent")
	private String userAgent;

	private String ip;
}

