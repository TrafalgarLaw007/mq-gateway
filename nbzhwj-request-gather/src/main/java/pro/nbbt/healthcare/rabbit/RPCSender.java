package pro.nbbt.healthcare.rabbit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class RPCSender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean b, String s) {
            log.info("消息ACK结果： %s, correlationData： %s", b, correlationData.getId());
        }
    };

    public void send(Object message, Map<String, Object> properties) throws Exception {
        /*MessageHeaders messageHeaders = new MessageHeaders(properties);
        Message<Object> msg = MessageBuilder.createMessage(message, messageHeaders);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        // 指定业务唯一的ID
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {
                log.info("---> Post to do : " + message);
                return message;
            }
        };

        rabbitTemplate.convertAndSend("request-gather", "request.gather.rabbit", msg, messagePostProcessor, correlationData);*/
        final Channel channel = rabbitTemplate.getConnectionFactory().createConnection().createChannel(false);
        channel.queueDeclare("queue_rpc_request", true, false,false, null);
        channel.queueDeclare("queue_rpc_response", true, false,false, null);

        channel.queueBind("test", "queue_rpc_request", "queue_rpc_request", null);
        channel.queueBind("test", "queue_rpc_response", "queue_rpc_response", null);

        //下面是生产者配置代码
        //correlationId - 每个请求独一无二的标识
        String correlationId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                //指明请求Id
                .correlationId(correlationId)
                //指明返回消息队列
                .replyTo("queue_rpc_response")
                .build();

        //发布request消息
        channel.basicPublish("queue_rpc_request", "queue_rpc_request", props, message.toString().getBytes());
    }
}
