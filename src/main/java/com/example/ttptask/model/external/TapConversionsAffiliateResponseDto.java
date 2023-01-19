package com.example.ttptask.model.external;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TapConversionsAffiliateResponseDto {

	private String id;
	private String firstname;
	private String lastname;
}
