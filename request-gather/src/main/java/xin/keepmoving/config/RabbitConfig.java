package xin.keepmoving.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Rabbit MQ configuration
 */
@Configuration
public class RabbitConfig {

    @Bean
    public Queue requestQueue() {
        return new Queue("RequestQueue", true, false, false);
    }

    @Bean
    public DirectExchange requestExchange() {
        return new DirectExchange("RequestExchange", true, false);
    }

    @Bean
    public Binding bindingReq2Resp() {
        return BindingBuilder.bind(requestQueue()).to(requestExchange()).with("Req-Resp");
    }
}
