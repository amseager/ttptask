package com.example.ttptask.service;

import com.example.ttptask.dto.RegisterClientRequestDto;
import com.example.ttptask.dto.RegisterClientResponseDto;

public interface ClientService {

	RegisterClientResponseDto registerClient(RegisterClientRequestDto requestDto);
}
