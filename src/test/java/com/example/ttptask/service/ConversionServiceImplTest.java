package com.example.ttptask.service;

import com.example.ttptask.configuration.properties.AppConfigProperties;
import com.example.ttptask.dto.RegisterConversionRequestDto;
import com.example.ttptask.exception.AppLogicException;
import com.example.ttptask.exception.ErrorCode;
import com.example.ttptask.model.AffiliateClient;
import com.example.ttptask.model.FailedCall;
import com.example.ttptask.model.external.TapConversionsCommissionResponseDto;
import com.example.ttptask.model.external.TapConversionsRequestDto;
import com.example.ttptask.model.external.TapConversionsResponseDto;
import com.example.ttptask.repository.AffiliateClientRepository;
import com.example.ttptask.repository.AffiliateTransactionRepository;
import com.example.ttptask.repository.FailedCallRepository;
import com.example.ttptask.service.external.ExternalVendorService;
import com.example.ttptask.service.mapper.ConversionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionServiceImplTest {

	private static final UUID SAMPLE_UUID = UUID.fromString("ab885a1c-8ad9-11ea-bc55-0242ac130003");

	@InjectMocks
	private ConversionServiceImpl conversionService;
	@Mock
	private AppConfigProperties appConfigProperties;
	@Mock
	private ConversionMapper conversionMapper;
	@Mock
	private ExternalVendorService externalVendorService;
	@Mock
	private FailedCallUtils failedCallUtils;
	@Mock
	private AffiliateClientRepository affiliateClientRepository;
	@Mock
	private AffiliateTransactionRepository affiliateTransactionRepository;
	@Mock
	private FailedCallRepository failedCallRepository;

	@Test
	void registerConversionWhenClientNotFound() {
		RegisterConversionRequestDto requestDto = new RegisterConversionRequestDto().clientId(SAMPLE_UUID);
		when(affiliateClientRepository.findById(SAMPLE_UUID)).thenReturn(Optional.empty());

		AppLogicException ex = assertThrows(AppLogicException.class, () -> conversionService.registerConversion(requestDto));

		assertEquals(ErrorCode.CLIENT_NOT_FOUND, ex.getErrorCode());
	}

	@Test
	void registerConversionWhenExternalCallWasNotSuccessful() {
		RegisterConversionRequestDto requestDto = new RegisterConversionRequestDto().clientId(SAMPLE_UUID);
		AffiliateClient affiliateClient = new AffiliateClient().setClientId(SAMPLE_UUID);
		when(affiliateClientRepository.findById(SAMPLE_UUID)).thenReturn(Optional.of(affiliateClient));
		when(appConfigProperties.getCurrency()).thenReturn("EUR");
		TapConversionsRequestDto tapConversionsRequestDto = new TapConversionsRequestDto().setCustomerId(SAMPLE_UUID);
		when(conversionMapper.toTapConversionsRequestDto(requestDto, affiliateClient, "EUR"))
				.thenReturn(tapConversionsRequestDto);
		when(externalVendorService.callTapConversions(tapConversionsRequestDto)).thenThrow(new RestClientException("fakemsg"));
		FailedCall failedCall = new FailedCall().setClientId(SAMPLE_UUID);
		when(failedCallUtils.createFailedCallEntry(SAMPLE_UUID, "createConversion", tapConversionsRequestDto, "fakemsg"))
				.thenReturn(failedCall);

		AppLogicException ex = assertThrows(AppLogicException.class, () -> conversionService.registerConversion(requestDto));

		assertEquals(ErrorCode.EXTERNAL_SERVICE_ERROR, ex.getErrorCode());
		verify(failedCallRepository).save(failedCall);
	}

	@Test
	void registerConversionWhenConversionIdAlreadyExists() {
		RegisterConversionRequestDto requestDto = new RegisterConversionRequestDto().clientId(SAMPLE_UUID);
		AffiliateClient affiliateClient = new AffiliateClient().setClientId(SAMPLE_UUID);
		when(affiliateClientRepository.findById(SAMPLE_UUID)).thenReturn(Optional.of(affiliateClient));
		when(appConfigProperties.getCurrency()).thenReturn("EUR");
		TapConversionsRequestDto tapConversionsRequestDto = new TapConversionsRequestDto().setCustomerId(SAMPLE_UUID);
		when(conversionMapper.toTapConversionsRequestDto(requestDto, affiliateClient, "EUR"))
				.thenReturn(tapConversionsRequestDto);
		when(externalVendorService.callTapConversions(tapConversionsRequestDto))
				.thenReturn(new TapConversionsResponseDto().setId(1));
		when(affiliateTransactionRepository.existsById(1)).thenReturn(true);

		AppLogicException ex = assertThrows(AppLogicException.class, () -> conversionService.registerConversion(requestDto));

		assertEquals(ErrorCode.DUPLICATE_CONVERSION, ex.getErrorCode());
	}

	@Test
	void registerConversionWhenNoAppropriateCommission() {
		RegisterConversionRequestDto requestDto = new RegisterConversionRequestDto().clientId(SAMPLE_UUID);
		AffiliateClient affiliateClient = new AffiliateClient().setClientId(SAMPLE_UUID);
		when(affiliateClientRepository.findById(SAMPLE_UUID)).thenReturn(Optional.of(affiliateClient));
		when(appConfigProperties.getCurrency()).thenReturn("EUR");
		TapConversionsRequestDto tapConversionsRequestDto = new TapConversionsRequestDto().setCustomerId(SAMPLE_UUID);
		when(conversionMapper.toTapConversionsRequestDto(requestDto, affiliateClient, "EUR"))
				.thenReturn(tapConversionsRequestDto);
		when(externalVendorService.callTapConversions(tapConversionsRequestDto))
				.thenReturn(new TapConversionsResponseDto().setId(1).setCommissions(List.of(
						new TapConversionsCommissionResponseDto().setCurrency("USD").setAmount(BigDecimal.TEN))));
		when(affiliateTransactionRepository.existsById(1)).thenReturn(false);

		AppLogicException ex = assertThrows(AppLogicException.class, () -> conversionService.registerConversion(requestDto));

		assertEquals(ErrorCode.CONVERSION_AMOUNT_NOT_FOUND, ex.getErrorCode());
	}
}