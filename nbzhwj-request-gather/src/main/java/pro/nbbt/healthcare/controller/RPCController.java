package pro.nbbt.healthcare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@Slf4j
public class RPCController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        // 同步调用设置远程调用响应超时时间，单位：毫秒
        rabbitTemplate.setReplyTimeout(60000);
    }

    @PostMapping("/syncRPC")
    public String syncRPC() {
        Object response = rabbitTemplate.convertSendAndReceive("RPCExchange", "RPC", "RPC同步调用");
        String respMsg = response.toString();
        log.info("远程调用响应:[{}]", respMsg);
        return respMsg;
    }

}
