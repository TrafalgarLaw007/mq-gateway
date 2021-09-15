package pro.nbbt.healthcare.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.nbbt.healthcare.config.PropertyConfig;
import pro.nbbt.healthcare.constants.ContentTypeConstant;
import pro.nbbt.healthcare.constants.MethodType;
import pro.nbbt.healthcare.entity.HttpRequestEntity;
import pro.nbbt.healthcare.entity.HttpResponseEntity;
import pro.nbbt.healthcare.utils.ContentTypeUtil;
import pro.nbbt.healthcare.utils.OkHttp3Util;

import java.util.Map;

@Component
@RabbitListener(queues = "RequestQueue")
@Slf4j
public class RequestDistributeReceiver {

    static final Map<String, String> APPLICATION_JSON_HEADER = Maps.newHashMap();

    static final String COMMON_ERROR_RESPONSE = "{\"code\":\"1\", \"data\": null, \"msg\":\"服务器请求异常\"}";

    static {
        APPLICATION_JSON_HEADER.put(ContentTypeUtil.CONTENT_TYPE, ContentTypeConstant.APPLICATION_JSON);
    }

    @Autowired
    PropertyConfig propertyConfig;

    @RabbitHandler
    public Object receive(String in) {
        HttpRequestEntity httpRequestEntity = JSONObject.parseObject(in, HttpRequestEntity.class);
        if (propertyConfig.getLogDebug()) {
            log.info(" [x] -> 请求地址 : {}", OkHttp3Util.getEncodeRequestUrl(httpRequestEntity));
            log.info(" [x] -> Received {}", in);
        }
        HttpResponseEntity resp = new HttpResponseEntity();
        try {
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
        } catch (Exception e) {
            log.error("请求异常 : {}", e);
            resp.setResponse(COMMON_ERROR_RESPONSE)
                    .setHeaderMap(APPLICATION_JSON_HEADER);
        }

        if (propertyConfig.getLogDebug()) {
            log.info(" [x] -> Request Response {}", resp);
        }
        return resp;
    }
}
