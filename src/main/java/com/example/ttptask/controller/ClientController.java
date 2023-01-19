package com.example.ttptask.controller;

import com.example.ttptask.dto.RegisterClientRequestDto;
import com.example.ttptask.dto.RegisterClientResponseDto;
import com.example.ttptask.exception.AppLogicException;
import com.example.ttptask.exception.ErrorCode;
import com.example.ttptask.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClientController implements ClientApi {

	private final ClientService clientService;

	@Override
	public ResponseEntity<RegisterClientResponseDto> registerClient(RegisterClientRequestDto requestDto) {
		validateCustomTypes(requestDto);
		RegisterClientResponseDto responseDto = clientService.registerClient(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	private void validateCustomTypes(RegisterClientRequestDto requestDto) {
		if (!UrlValidator.getInstance().isValid(requestDto.getLandingPage())) {
			throw new AppLogicException(ErrorCode.INVALID_REQUEST, "invalid landing page URL");
		}
		if (!InetAddressValidator.getInstance().isValid(requestDto.getIp())) {
			throw new AppLogicException(ErrorCode.INVALID_REQUEST, "invalid IP address");
		}
	}
}
