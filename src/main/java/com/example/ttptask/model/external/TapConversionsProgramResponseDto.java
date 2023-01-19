package com.example.ttptask.model.external;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TapConversionsProgramResponseDto {

	private String id;
	private String title;
	private String currency;
}
