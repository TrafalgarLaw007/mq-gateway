package xin.keepmoving.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * WebService Configuration
 */
@Data
@Component
@ConfigurationProperties(prefix = "web.service")
public class WebServiceConfig {
	/**
	 * url
	 */
	private  String url;

	/**
	 * ip
	 */
	private  String publishIp;

	/**
	 * port
	 */
	private  Integer publishPort;
}
