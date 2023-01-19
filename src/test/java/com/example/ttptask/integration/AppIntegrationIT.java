package com.example.ttptask.integration;

import com.example.ttptask.configuration.I18nConfig;
import com.example.ttptask.repository.AffiliateClientRepository;
import com.example.ttptask.repository.AffiliateTransactionRepository;
import com.example.ttptask.repository.FailedCallRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@Import(I18nConfig.class)
@SpringBootTest
public class AppIntegrationIT {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private AffiliateClientRepository affiliateClientRepository;
	@Autowired
	private AffiliateTransactionRepository affiliateTransactionRepository;
	@Autowired
	private FailedCallRepository failedCallRepository;

	@BeforeEach
	protected void setUp() {
		affiliateClientRepository.deleteAll();
		affiliateTransactionRepository.deleteAll();
		failedCallRepository.deleteAll();
	}

	@Test
	@SneakyThrows
	void testRegisterClientAndConversionSuccessfully() {

		//register a new client
		stubPostRequest("/tap/clicks",
				getJsonFromFile("__files/tap_clicks_request.json"),
				HttpStatus.OK, "tap_clicks_response.json");

		mockMvc.perform(
				MockMvcRequestBuilders.post("/register/client")
						.contentType(MediaType.APPLICATION_JSON)
						.content(getJsonFromFile("integration/RegisterClientRequest.json"))
		).andDo(print()
		).andExpect(status().is(HttpStatus.CREATED.value())
		).andExpect(content().json(getJsonFromFile("integration/RegisterClientResponse.json")));

		//register a conversion for the existing client
		stubPostRequest("/tap/conversions",
				getJsonFromFile("__files/tap_conversions_request.json"),
				HttpStatus.OK, "tap_conversions_response.json");

		mockMvc.perform(
				MockMvcRequestBuilders.post("/register/conversion")
						.contentType(MediaType.APPLICATION_JSON)
						.content(getJsonFromFile("integration/RegisterConversionRequest.json"))
		).andDo(print()
		).andExpect(status().is(HttpStatus.CREATED.value())
		).andExpect(content().json(getJsonFromFile("integration/RegisterConversionResponse.json")));

		assertEquals(1, affiliateClientRepository.count());
		assertEquals(1, affiliateTransactionRepository.count());
		assertEquals(0, failedCallRepository.count());
	}

	@Test
	@SneakyThrows
	void testRegisterClientWithFailedExternalCall() {

		//register a new client
		stubPostRequest("/tap/clicks",
				getJsonFromFile("__files/tap_clicks_request.json"),
				HttpStatus.BAD_REQUEST, "tap_error_response.json");

		mockMvc.perform(
				MockMvcRequestBuilders.post("/register/client")
						.contentType(MediaType.APPLICATION_JSON)
						.content(getJsonFromFile("integration/RegisterClientRequest.json"))
		).andDo(print()
		).andExpect(status().is(HttpStatus.FAILED_DEPENDENCY.value()));

		assertEquals(0, affiliateClientRepository.count());
		assertEquals(1, failedCallRepository.count());
	}

	@Test
	@SneakyThrows
	void testRegisterClientSuccessfullyAndConversionWithFailedExternalCall() {

		//register a new client
		stubPostRequest("/tap/clicks",
				getJsonFromFile("__files/tap_clicks_request.json"),
				HttpStatus.OK, "tap_clicks_response.json");

		mockMvc.perform(
				MockMvcRequestBuilders.post("/register/client")
						.contentType(MediaType.APPLICATION_JSON)
						.content(getJsonFromFile("integration/RegisterClientRequest.json"))
		).andDo(print()
		).andExpect(status().is(HttpStatus.CREATED.value())
		).andExpect(content().json(getJsonFromFile("integration/RegisterClientResponse.json")));

		//register a conversion for the existing client
		stubPostRequest("/tap/conversions",
				getJsonFromFile("__files/tap_conversions_request.json"),
				HttpStatus.BAD_REQUEST, "tap_error_response.json");

		mockMvc.perform(
				MockMvcRequestBuilders.post("/register/conversion")
						.contentType(MediaType.APPLICATION_JSON)
						.content(getJsonFromFile("integration/RegisterConversionRequest.json"))
		).andDo(print()
		).andExpect(status().is(HttpStatus.FAILED_DEPENDENCY.value()));

		assertEquals(1, affiliateClientRepository.count());
		assertEquals(0, affiliateTransactionRepository.count());
		assertEquals(1, failedCallRepository.count());
	}

	private void stubPostRequest(final String url,
	                             final String request,
	                             final HttpStatus stubStatus,
	                             final String stubResponse) {
		stubFor(post(urlEqualTo(url))
				.withRequestBody(equalToJson(request, true, false))
				.willReturn(aResponse()
						.withStatus(stubStatus.value())
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.withBodyFile(stubResponse)));
	}

	@SneakyThrows
	protected String getJsonFromFile(String resource) {
		return FileUtils.readFileToString(
				FileUtils.getFile("src/test/resources/" + resource), Charset.defaultCharset());
	}
}
