package com.example.ttptask.service;

import com.example.ttptask.model.FailedCall;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FailedCallUtils {

	private final ObjectMapper objectMapper;

	@SneakyThrows
	public FailedCall createFailedCallEntry(UUID clientId, String requestType, Object payload, String reason) {
		return new FailedCall()
				.setClientId(clientId)
				.setRequestType(requestType)
				.setPayload(objectMapper.writeValueAsString(payload))
				.setReason(reason)
				.setProcessed(false);
	}
}
