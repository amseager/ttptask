package com.example.ttptask.controller;

import com.example.ttptask.dto.RegisterConversionRequestDto;
import com.example.ttptask.dto.RegisterConversionResponseDto;
import com.example.ttptask.service.ConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConversionController implements ConversionApi {

	private final ConversionService conversionService;

	@Override
	public ResponseEntity<RegisterConversionResponseDto> registerConversion(RegisterConversionRequestDto requestDto) {
		RegisterConversionResponseDto responseDto = conversionService.registerConversion(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}
}
