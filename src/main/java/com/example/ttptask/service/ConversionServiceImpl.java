package com.example.ttptask.service;

import com.example.ttptask.configuration.properties.AppConfigProperties;
import com.example.ttptask.dto.RegisterConversionRequestDto;
import com.example.ttptask.dto.RegisterConversionResponseDto;
import com.example.ttptask.exception.AppLogicException;
import com.example.ttptask.exception.ErrorCode;
import com.example.ttptask.model.AffiliateClient;
import com.example.ttptask.model.AffiliateTransaction;
import com.example.ttptask.model.FailedCall;
import com.example.ttptask.model.external.TapConversionsCommissionResponseDto;
import com.example.ttptask.model.external.TapConversionsRequestDto;
import com.example.ttptask.model.external.TapConversionsResponseDto;
import com.example.ttptask.repository.AffiliateClientRepository;
import com.example.ttptask.repository.AffiliateTransactionRepository;
import com.example.ttptask.repository.FailedCallRepository;
import com.example.ttptask.service.external.ExternalVendorService;
import com.example.ttptask.service.mapper.ConversionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversionServiceImpl implements ConversionService {

	private final AppConfigProperties appConfigProperties;
	private final ConversionMapper conversionMapper;

	private final ExternalVendorService externalVendorService;
	private final FailedCallUtils failedCallUtils;

	private final AffiliateClientRepository affiliateClientRepository;
	private final AffiliateTransactionRepository affiliateTransactionRepository;
	private final FailedCallRepository failedCallRepository;

	/**
	 * Registers a conversion with calling an external vendor.
	 * It's assumed that the corresponding client already exists in the system, otherwise an error will be thrown.
	 */
	@Override
	public RegisterConversionResponseDto registerConversion(RegisterConversionRequestDto requestDto) {
		UUID clientId = requestDto.getClientId();
		AffiliateClient affiliateClient = affiliateClientRepository.findById(clientId)
				.orElseThrow(() -> new AppLogicException(ErrorCode.CLIENT_NOT_FOUND, clientId));

		String currency = appConfigProperties.getCurrency();
		TapConversionsRequestDto tapConversionsRequestDto = conversionMapper
				.toTapConversionsRequestDto(requestDto, affiliateClient, currency);
		TapConversionsResponseDto tapConversionsResponseDto;

		try {
			tapConversionsResponseDto = externalVendorService.callTapConversions(tapConversionsRequestDto);
		} catch (RestClientException e) {
			FailedCall failedCall = failedCallUtils.createFailedCallEntry(
					clientId, "createConversion", tapConversionsRequestDto, e.getMessage());
			failedCallRepository.save(failedCall);
			throw new AppLogicException(ErrorCode.EXTERNAL_SERVICE_ERROR, e, e.getMessage());
		}

		Integer conversionId = tapConversionsResponseDto.getId();
		if (affiliateTransactionRepository.existsById(conversionId)) {
			throw new AppLogicException(ErrorCode.DUPLICATE_CONVERSION, conversionId);
		}

		//trying to find the corresponding conversion amount
		BigDecimal conversionAmount = Optional.ofNullable(tapConversionsResponseDto.getCommissions()).orElse(List.of())
				.stream()
				.filter(commission -> currency.equals(commission.getCurrency()))
				.findFirst()
				.map(TapConversionsCommissionResponseDto::getAmount)
				.orElseThrow(() -> new AppLogicException(ErrorCode.CONVERSION_AMOUNT_NOT_FOUND, currency));

		AffiliateTransaction affiliateTransaction = conversionMapper.toAffiliateTransaction(
				requestDto, affiliateClient, tapConversionsResponseDto, currency, conversionAmount);
		affiliateTransactionRepository.save(affiliateTransaction);

		return conversionMapper.toRegisterConversionResponseDto(tapConversionsResponseDto);
	}
}
