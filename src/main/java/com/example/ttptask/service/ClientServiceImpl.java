package com.example.ttptask.service;

import com.example.ttptask.dto.RegisterClientRequestDto;
import com.example.ttptask.dto.RegisterClientResponseDto;
import com.example.ttptask.exception.AppLogicException;
import com.example.ttptask.exception.ErrorCode;
import com.example.ttptask.model.AffiliateClient;
import com.example.ttptask.model.FailedCall;
import com.example.ttptask.model.external.TapClicksRequestDto;
import com.example.ttptask.model.external.TapClicksResponseDto;
import com.example.ttptask.repository.AffiliateClientRepository;
import com.example.ttptask.repository.FailedCallRepository;
import com.example.ttptask.service.external.ExternalVendorService;
import com.example.ttptask.service.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

	private final ClientMapper clientMapper;

	private final ExternalVendorService externalVendorService;
	private final FailedCallUtils failedCallUtils;

	private final AffiliateClientRepository affiliateClientRepository;
	private final FailedCallRepository failedCallRepository;

	/**
	 * Registers a new client with calling an external vendor.
	 */
	@Override
	public RegisterClientResponseDto registerClient(RegisterClientRequestDto requestDto) {
		UUID clientId = requestDto.getClientId();
		if (affiliateClientRepository.existsById(clientId)) {
			throw new AppLogicException(ErrorCode.DUPLICATE_CLIENT, clientId);
		}

		TapClicksRequestDto tapClicksRequestDto = clientMapper.toTapClicksRequestDto(requestDto);
		TapClicksResponseDto tapClicksResponseDto;

		try {
			tapClicksResponseDto = externalVendorService.callTapClicks(tapClicksRequestDto);
		} catch (RestClientException e) {
			FailedCall failedCall = failedCallUtils.createFailedCallEntry(
					clientId, "createClick", tapClicksRequestDto, e.getMessage());
			failedCallRepository.save(failedCall);
			throw new AppLogicException(ErrorCode.EXTERNAL_SERVICE_ERROR, e, e.getMessage());
		}

		AffiliateClient affiliateClient = clientMapper.toAffiliateClient(requestDto, tapClicksResponseDto);
		affiliateClientRepository.save(affiliateClient);

		return clientMapper.toRegisterClientResponseDto(tapClicksResponseDto);
	}
}
