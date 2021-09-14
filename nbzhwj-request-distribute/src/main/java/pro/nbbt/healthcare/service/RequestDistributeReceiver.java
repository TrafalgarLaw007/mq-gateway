package pro.nbbt.healthcare.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pro.nbbt.healthcare.entity.HttpRequestEntity;
import pro.nbbt.healthcare.utils.OkHttp3Util;

import java.io.IOException;

@Component
@RabbitListener(queues = "RequestQueue")
@Slf4j
public class RequestDistributeReceiver {

    @RabbitHandler
    public Object receive(String in) throws IOException {
        System.out.println(" [x] -> Received '" + in + "'");

        HttpRequestEntity httpRequestEntity = JSONObject.parseObject(in, HttpRequestEntity.class);
        log.info(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity));

        Object resp = "";

        switch (httpRequestEntity.getMethod()) {
            case "GET":
                resp = OkHttp3Util.sendByGetUrl2(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), httpRequestEntity.headerMap);
                break;
            case "POST":
                // 区分请求类型
                resp = OkHttp3Util.sendByPostJson2(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), httpRequestEntity.getBodyData(), httpRequestEntity.headerMap);
                break;
        }
        return resp;
    }
}
