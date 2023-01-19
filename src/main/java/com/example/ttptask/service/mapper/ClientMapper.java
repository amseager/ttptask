package com.example.ttptask.service.mapper;

import com.example.ttptask.dto.RegisterClientRequestDto;
import com.example.ttptask.dto.RegisterClientResponseDto;
import com.example.ttptask.model.AffiliateClient;
import com.example.ttptask.model.external.TapClicksRequestDto;
import com.example.ttptask.model.external.TapClicksResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

	@Mapping(target = "referralCode", source = "referalCode")
	TapClicksRequestDto toTapClicksRequestDto(RegisterClientRequestDto registerClientRequestDto);

	@Mapping(target = "version", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "clientId", source = "registerClientRequestDto.clientId")
	@Mapping(target = "referralCode", source = "registerClientRequestDto.referalCode")
	@Mapping(target = "clickId", source = "tapClicksResponseDto.id")
	@Mapping(target = "clientIp", source = "registerClientRequestDto.ip")
	@Mapping(target = "userAgent", source = "registerClientRequestDto.userAgent")
	AffiliateClient toAffiliateClient(RegisterClientRequestDto registerClientRequestDto,
	                                  TapClicksResponseDto tapClicksResponseDto);

	@Mapping(target = "clickId", source = "id")
	RegisterClientResponseDto toRegisterClientResponseDto(TapClicksResponseDto tapClicksResponseDto);
}
