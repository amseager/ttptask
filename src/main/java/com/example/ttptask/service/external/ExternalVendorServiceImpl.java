package com.example.ttptask.service.external;

import com.example.ttptask.model.external.TapClicksRequestDto;
import com.example.ttptask.model.external.TapClicksResponseDto;
import com.example.ttptask.model.external.TapConversionsRequestDto;
import com.example.ttptask.model.external.TapConversionsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.vendor.use-stubs", havingValue = "false", matchIfMissing = true)
public class ExternalVendorServiceImpl implements ExternalVendorService {

	private final RetryTemplate externalVendorRetryTemplate;
	private final RestTemplate externalVendorRestTemplate;

	@Override
	public TapClicksResponseDto callTapClicks(TapClicksRequestDto tapClicksRequestDto) {
		return externalVendorRetryTemplate.execute(context -> externalVendorRestTemplate.postForObject(
				"/tap/clicks", tapClicksRequestDto, TapClicksResponseDto.class));
	}

	@Override
	public TapConversionsResponseDto callTapConversions(TapConversionsRequestDto tapConversionsRequestDto) {
		return externalVendorRetryTemplate.execute(context -> externalVendorRestTemplate.postForObject(
				"/tap/conversions", tapConversionsRequestDto, TapConversionsResponseDto.class));
	}

}
