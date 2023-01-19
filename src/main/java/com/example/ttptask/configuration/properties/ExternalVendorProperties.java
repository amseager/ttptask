package com.example.ttptask.configuration.properties;

import lombok.Data;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Data
@Validated
@ConfigurationProperties(prefix = "external.vendor")
@Configuration
public class ExternalVendorProperties {

	@NotNull
	private String rootUri;
	@NotNull
	private Duration connectionTimeout;
	@NotNull
	private Duration readTimeout;
	@NestedConfigurationProperty
	private PoolingHttpClientConnectionManager connectionManager;
	@NotNull
	private Integer maxAttempts;
	@NotNull
	private Duration retryTimeInterval;
	@NotNull
	private Boolean useStubs;
}
