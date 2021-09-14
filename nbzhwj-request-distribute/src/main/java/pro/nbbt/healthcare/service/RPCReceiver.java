package pro.nbbt.healthcare.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "RPCQueue")
@Slf4j
public class RPCReceiver {

    @RabbitHandler
    public String process(String message) {
        log.info("接收远程调用请求消息:[{}]", message);
        return "remote procedure call success!";
    }
}
