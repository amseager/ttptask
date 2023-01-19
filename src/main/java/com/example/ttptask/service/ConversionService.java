package com.example.ttptask.service;

import com.example.ttptask.dto.RegisterConversionRequestDto;
import com.example.ttptask.dto.RegisterConversionResponseDto;

public interface ConversionService {

	RegisterConversionResponseDto registerConversion(RegisterConversionRequestDto requestDto);
}
