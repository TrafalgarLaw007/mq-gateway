package pro.nbbt.healthcare.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        // 同步调用设置远程调用响应超时时间，单位：毫秒
//        rabbitTemplate.setReplyTimeout(60000);
        rabbitTemplate.setReplyTimeout(10000);
    }

    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean b, String s) {
            log.info("消息ACK结果： %s, correlationData： %s", b, correlationData.getId());
        }
    };



    /*public void send(Object message, Map<String, Object> properties) throws Exception {
        MessageHeaders messageHeaders = new MessageHeaders(properties);
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

        rabbitTemplate.convertAndSend("request-gather", "request.gather.rabbit", msg, messagePostProcessor, correlationData);
    }*/

    public Object send(Object message, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 指定业务唯一的ID
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        // RPC调用
        Object resp = rabbitTemplate.convertSendAndReceive("RequestExchange", "Req-Resp", message, correlationData);
        return resp;
    }

    public Object send(Object message) {
        // 指定业务唯一的ID
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        // RPC调用
        Object resp = rabbitTemplate.convertSendAndReceive("RequestExchange", "Req-Resp", message, correlationData);
        return resp;
    }
}
