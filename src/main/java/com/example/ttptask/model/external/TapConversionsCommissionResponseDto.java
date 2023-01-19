package com.example.ttptask.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class TapConversionsCommissionResponseDto {

	private Integer id;
	@JsonProperty("conversion_sub_amount")
	private BigDecimal conversionSubAmount;
	private BigDecimal amount;
	@JsonProperty("commission_type")
	private String commissionType;
	private Boolean approved;
	private TapConversionsAffiliateResponseDto affiliate;
	private String kind;
	private String currency;
	@JsonProperty("created_at")
	private OffsetDateTime createdAt;

}
