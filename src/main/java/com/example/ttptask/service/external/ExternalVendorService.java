package com.example.ttptask.service.external;

import com.example.ttptask.model.external.TapClicksRequestDto;
import com.example.ttptask.model.external.TapClicksResponseDto;
import com.example.ttptask.model.external.TapConversionsRequestDto;
import com.example.ttptask.model.external.TapConversionsResponseDto;

public interface ExternalVendorService {

	TapClicksResponseDto callTapClicks(TapClicksRequestDto tapClicksRequestDto);

	TapConversionsResponseDto callTapConversions(TapConversionsRequestDto tapConversionsRequestDto);
}
