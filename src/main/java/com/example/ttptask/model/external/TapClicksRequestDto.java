package com.example.ttptask.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TapClicksRequestDto {

	@JsonProperty("referral_code")
	private String referralCode;

	@JsonProperty("landing_page")
	private String landingPage;

	@JsonProperty("user_agent")
	private String userAgent;

	private String ip;
}

