package pro.nbbt.healthcare.service;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "request-gather-queue", durable = "true"),
        exchange = @Exchange(name = "request-gather", durable = "true", type = "direct"),
        key = "request.gather.rabbit"
))
public class RequestDistributeService {

    /*@RabbitHandler
    public void receive(String in) {
        System.out.println(" [x] -> Received '" + in + "'");
    }*/

    @RabbitHandler
    public void receive(String in) {
        System.out.println(" [x] -> Received '" + in + "'");

        // TODO 处理请求
    }
}
