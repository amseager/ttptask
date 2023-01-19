package com.example.ttptask.service.mapper;

import com.example.ttptask.dto.RegisterConversionRequestDto;
import com.example.ttptask.dto.RegisterConversionResponseDto;
import com.example.ttptask.model.AffiliateClient;
import com.example.ttptask.model.AffiliateTransaction;
import com.example.ttptask.model.external.TapConversionsRequestDto;
import com.example.ttptask.model.external.TapConversionsResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ConversionMapper {

	@Mapping(target = "clickId", source = "affiliateClient.clickId")
	@Mapping(target = "externalId", source = "registerConversionRequestDto.orderId")
	@Mapping(target = "amount", source = "registerConversionRequestDto.totalPrice")
	@Mapping(target = "currency", source = "currency")
	@Mapping(target = "customerId", source = "registerConversionRequestDto.clientId")
	@Mapping(target = "userAgent", source = "affiliateClient.userAgent")
	@Mapping(target = "ip", source = "affiliateClient.clientIp")
	TapConversionsRequestDto toTapConversionsRequestDto(RegisterConversionRequestDto registerConversionRequestDto,
	                                                    AffiliateClient affiliateClient,
	                                                    String currency);

	@Mapping(target = "version", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "conversionId", source = "tapConversionsResponseDto.id")
	@Mapping(target = "clientId", source = "registerConversionRequestDto.clientId")
	@Mapping(target = "referralCode", source = "affiliateClient.referralCode")
	@Mapping(target = "orderId", source = "registerConversionRequestDto.orderId")
	@Mapping(target = "currency", source = "currency")
	@Mapping(target = "orderAmount", source = "tapConversionsResponseDto.amount")
	@Mapping(target = "conversionAmount", source = "conversionAmount")
	@Mapping(target = "transactionType", source = "registerConversionRequestDto.transactionType")
	AffiliateTransaction toAffiliateTransaction(RegisterConversionRequestDto registerConversionRequestDto,
	                                            AffiliateClient affiliateClient,
	                                            TapConversionsResponseDto tapConversionsResponseDto,
	                                            String currency,
	                                            BigDecimal conversionAmount);

	@Mapping(target = "conversionId", source = "id")
	RegisterConversionResponseDto toRegisterConversionResponseDto(TapConversionsResponseDto tapConversionsResponseDto);
}
