package com.example.ttptask.service.external;

import com.example.ttptask.model.external.TapClicksRequestDto;
import com.example.ttptask.model.external.TapClicksResponseDto;
import com.example.ttptask.model.external.TapConversionsRequestDto;
import com.example.ttptask.model.external.TapConversionsResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.vendor.use-stubs", havingValue = "true")
public class StubExternalVendorServiceImpl implements ExternalVendorService {

	private final ObjectMapper objectMapper;

	private TapClicksResponseDto stubTapClicksResponseDto;
	private TapConversionsResponseDto stubTapConversionsResponseDto;

	@PostConstruct
	private void init() throws IOException {
		stubTapClicksResponseDto = initStub("register_client_response.json", TapClicksResponseDto.class);
		stubTapConversionsResponseDto = initStub("register_conversion_response.json", TapConversionsResponseDto.class);
	}

	private <T> T initStub(String fileName, Class<T> stubClass) throws IOException {
		File file = ResourceUtils.getFile("classpath:stubs/" + fileName);
		return objectMapper.readValue(file, stubClass);
	}

	@Override
	public TapClicksResponseDto callTapClicks(TapClicksRequestDto tapClicksRequestDto) {
		return stubTapClicksResponseDto;
	}

	@Override
	public TapConversionsResponseDto callTapConversions(TapConversionsRequestDto tapConversionsRequestDto) {
		return stubTapConversionsResponseDto;
	}

}
