package pro.nbbt.healthcare.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.nbbt.healthcare.common.MethodType;
import pro.nbbt.healthcare.config.PropertyConfig;
import pro.nbbt.healthcare.entity.HttpRequestEntity;
import pro.nbbt.healthcare.utils.OkHttp3Util;

import java.io.IOException;

@Component
@RabbitListener(queues = "RequestQueue")
@Slf4j
public class RequestDistributeReceiver {

    @Autowired
    PropertyConfig propertyConfig;

    @RabbitHandler
    public Object receive(String in) throws IOException {
        HttpRequestEntity httpRequestEntity = JSONObject.parseObject(in, HttpRequestEntity.class);
        if (propertyConfig.getLogDebug()) {
            log.info(" [x] -> 请求地址 : {}", OkHttp3Util.getEncodeRequestUrl(httpRequestEntity));
            log.info(" [x] -> Received {}", in);
        }
        Object resp = "";

        switch (httpRequestEntity.getMethod()) {
            case MethodType.GET:
                resp = OkHttp3Util.sendByGetUrl2(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), httpRequestEntity.headerMap);
                break;
            case MethodType.POST:
                // 区分请求类型
                resp = OkHttp3Util.sendByPostJson2(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), httpRequestEntity.getBodyData(), httpRequestEntity.headerMap);
                break;
            case MethodType.PUT:
                // 区分请求类型
                resp = OkHttp3Util.sendByPutJson2(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), httpRequestEntity.getBodyData(), httpRequestEntity.headerMap);
                break;
            case MethodType.DELETE:
                // 区分请求类型
                resp = OkHttp3Util.sendByDeleteJson2(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), httpRequestEntity.getBodyData(), httpRequestEntity.headerMap);
                break;
        }
        if (propertyConfig.getLogDebug()) {
            log.info(" [x] -> Request Response {}", resp);
        }
        return resp;
    }
}
