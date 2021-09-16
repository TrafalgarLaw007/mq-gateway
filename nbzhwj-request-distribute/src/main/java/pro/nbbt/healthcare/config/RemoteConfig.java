package pro.nbbt.healthcare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConfigurationProperties(prefix = "remote")
@Data
public class RemoteConfig {

    private String url;

    private String host;

    private Integer port;

    private String webServiceHost;

    private Integer webServicePort;

    public String getRequestUrl() {
        if (StringUtils.hasLength(url)) {
            return url;
        }
        return "http://" + host + ":" + port;
    }

    public String getWebServiceRequestUrl() {
        return "http://" + webServiceHost + ":" + webServicePort;
    }
}
