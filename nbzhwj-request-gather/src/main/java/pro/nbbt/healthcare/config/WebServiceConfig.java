package pro.nbbt.healthcare.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author
 * @Description
 * @Date Create in 2020/11/11
 * @Modified By:
 */
@Data
@Slf4j
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

	/**
	 * ZYWSUrl
	 */
	private  String zywsUrl;
}
