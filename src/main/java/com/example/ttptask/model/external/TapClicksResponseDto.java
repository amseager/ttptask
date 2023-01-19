package com.example.ttptask.model.external;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class TapClicksResponseDto {

	private UUID id;
}

