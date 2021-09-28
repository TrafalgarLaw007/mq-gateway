package xin.keepmoving.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import static xin.keepmoving.constants.CommonConstants.*;

/**
 *
 */
@Configuration
@ConfigurationProperties(prefix = "remote")
@Data
public class RemoteConfig {

    private String protocol;

    private String url;

    private String host;

    private Integer port;

    private String webServiceHost;

    private Integer webServicePort;

    public String getRequestUrl() {
        if (StringUtils.hasLength(url)) {
            return url;
        }
        return protocol + PROTOCOL_SEPARATOR + host + COLON + port;
    }

    public String getWebServiceRequestUrl() {
        return protocol + PROTOCOL_SEPARATOR + webServiceHost + COLON + webServicePort;
    }
}
