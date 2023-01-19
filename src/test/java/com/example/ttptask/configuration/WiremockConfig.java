package com.example.ttptask.configuration;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class WiremockConfig {

	@Bean
	public WireMockConfigurationCustomizer noKeepAliveTransformerCustomizer(
			final ResponseDefinitionTransformer noKeepAliveTransformer) {
		return config -> config.extensions(noKeepAliveTransformer);
	}

	@Bean
	public ResponseDefinitionTransformer noKeepAliveTransformer() {
		return new ResponseDefinitionTransformer() {

			@Override
			public ResponseDefinition transform(final Request request,
			                                    final ResponseDefinition responseDefinition,
			                                    final FileSource files,
			                                    final Parameters parameters) {
				return ResponseDefinitionBuilder
						.like(responseDefinition)
						.withHeader(HttpHeaders.CONNECTION, "close")
						.build();
			}

			@Override
			public String getName() {
				return "keep-alive-disabler";
			}
		};
	}

}
