package com.example.ttptask.configuration;

import com.example.ttptask.configuration.properties.ExternalVendorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "external.vendor.use-stubs", havingValue = "false", matchIfMissing = true)
public class ExternalVendorConfig {

	private final RestTemplateBuilder restTemplateBuilder;
	private final ExternalVendorProperties externalVendorProperties;

	@Bean
	public RestTemplate externalVendorRestTemplate() {
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.setConnectionManager(externalVendorProperties.getConnectionManager())
				.build();

		ClientHttpRequestFactory requestFactory =
				new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

		return restTemplateBuilder
				.requestFactory(() -> requestFactory)
				.rootUri(externalVendorProperties.getRootUri())
				.setConnectTimeout(externalVendorProperties.getConnectionTimeout())
				.setReadTimeout(externalVendorProperties.getReadTimeout())
				.build();
	}

	@Bean
	public RetryTemplate externalVendorRetryTemplate() {
		return RetryTemplate.builder()
				.maxAttempts(externalVendorProperties.getMaxAttempts())
				.fixedBackoff(externalVendorProperties.getRetryTimeInterval().toMillis())
				.retryOn(RestClientException.class)
				.traversingCauses()
				.withListener(new RetryListenerSupport() {
					@Override
					public <T, E extends Throwable> void onError(final RetryContext context,
					                                             final RetryCallback<T, E> callback,
					                                             final Throwable throwable) {
						log.warn("Failed to make an external call. Retry attempt: {}, exception message: {}",
								context.getRetryCount(), throwable.getMessage());
					}
				})
				.build();
	}

}
