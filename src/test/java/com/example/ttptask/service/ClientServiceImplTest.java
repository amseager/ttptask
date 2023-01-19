package com.example.ttptask.service;

import com.example.ttptask.dto.RegisterClientRequestDto;
import com.example.ttptask.exception.AppLogicException;
import com.example.ttptask.exception.ErrorCode;
import com.example.ttptask.model.FailedCall;
import com.example.ttptask.model.external.TapClicksRequestDto;
import com.example.ttptask.repository.AffiliateClientRepository;
import com.example.ttptask.repository.FailedCallRepository;
import com.example.ttptask.service.external.ExternalVendorService;
import com.example.ttptask.service.mapper.ClientMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

	private static final UUID SAMPLE_UUID = UUID.fromString("ab885a1c-8ad9-11ea-bc55-0242ac130003");

	@InjectMocks
	private ClientServiceImpl clientService;
	@Mock
	private ClientMapper clientMapper;
	@Mock
	private ExternalVendorService externalVendorService;
	@Mock
	private FailedCallUtils failedCallUtils;
	@Mock
	private AffiliateClientRepository affiliateClientRepository;
	@Mock
	private FailedCallRepository failedCallRepository;

	@Test
	void registerClientWhenClientIdAlreadyExists() {
		RegisterClientRequestDto requestDto = new RegisterClientRequestDto().clientId(SAMPLE_UUID);
		when(affiliateClientRepository.existsById(SAMPLE_UUID)).thenReturn(true);

		AppLogicException ex = assertThrows(AppLogicException.class, () -> clientService.registerClient(requestDto));

		assertEquals(ErrorCode.DUPLICATE_CLIENT, ex.getErrorCode());
		verifyNoMoreInteractions(affiliateClientRepository);
	}

	@Test
	void registerClientWhenExternalCallWasNotSuccessful() {
		RegisterClientRequestDto requestDto = new RegisterClientRequestDto().clientId(SAMPLE_UUID);
		when(affiliateClientRepository.existsById(SAMPLE_UUID)).thenReturn(false);
		TapClicksRequestDto tapClicksRequestDto = new TapClicksRequestDto().setLandingPage("landingPage");
		when(clientMapper.toTapClicksRequestDto(requestDto)).thenReturn(tapClicksRequestDto);
		when(externalVendorService.callTapClicks(tapClicksRequestDto)).thenThrow(new RestClientException("fakemsg"));
		FailedCall failedCall = new FailedCall().setClientId(SAMPLE_UUID);
		when(failedCallUtils.createFailedCallEntry(SAMPLE_UUID, "createClick", tapClicksRequestDto, "fakemsg"))
				.thenReturn(failedCall);

		AppLogicException ex = assertThrows(AppLogicException.class, () -> clientService.registerClient(requestDto));

		assertEquals(ErrorCode.EXTERNAL_SERVICE_ERROR, ex.getErrorCode());
		verify(failedCallRepository).save(failedCall);
	}
}