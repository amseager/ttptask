package com.example.ttptask.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class TapConversionsResponseDto {

	private Integer id;
	@JsonProperty("external_id")
	private UUID externalId;
	private BigDecimal amount;
	private UUID click;
	private List<TapConversionsCommissionResponseDto> commissions;
	private TapConversionsProgramResponseDto program;
	private TapConversionsAffiliateResponseDto affiliate;
	@JsonProperty("created_at")
	private OffsetDateTime createdAt;
	private String warnings;
}

