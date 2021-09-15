package pro.nbbt.healthcare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "data")
@Data
public class PropertyConfig {

    private Boolean logDebug;

}