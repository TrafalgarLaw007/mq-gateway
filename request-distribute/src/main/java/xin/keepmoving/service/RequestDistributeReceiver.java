package xin.keepmoving.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xin.keepmoving.config.PropertyConfig;
import xin.keepmoving.constants.ContentTypeConstant;
import xin.keepmoving.constants.MethodType;
import xin.keepmoving.entity.HttpRequestEntity;
import xin.keepmoving.entity.HttpResponseEntity;
import xin.keepmoving.entity.MultipartFileEntity;
import xin.keepmoving.utils.ContentTypeUtil;
import xin.keepmoving.utils.OkHttp3Util;

import java.util.Map;

@Component
@RabbitListener(queues = "RequestQueue")
@Slf4j
public class RequestDistributeReceiver {

    static final Map<String, String> APPLICATION_JSON_HEADER = Maps.newHashMap();

    static final String COMMON_ERROR_RESPONSE = "{\"code\":\"1\", \"data\": null, \"msg\":\"服务器请求异常\"}";

    static final String WEBSERVICE_COMMON_ERROR_RESPONSE = "0";

    static {
        APPLICATION_JSON_HEADER.put(ContentTypeUtil.CONTENT_TYPE, ContentTypeConstant.APPLICATION_JSON);
    }

    @Autowired
    PropertyConfig propertyConfig;

    @RabbitHandler
    public Object receive(String in) {
        HttpRequestEntity httpRequestEntity = JSONObject.parseObject(in, HttpRequestEntity.class);
        if (propertyConfig.getLogDebug()) {
            log.info(" [x] -> 发起请求IP : {}", httpRequestEntity.getRequestIp());
            log.info(" [x] -> 请求地址 : {}", OkHttp3Util.getEncodeRequestUrl(httpRequestEntity));
            log.info(" [x] -> Received {}", in);
        }
        HttpResponseEntity resp = new HttpResponseEntity();
        boolean ws = false;
        try {
            Map<String, String> headerMap = httpRequestEntity.getHeaderMap();
            switch (httpRequestEntity.getMethod()) {
                case MethodType.GET:
                    resp = OkHttp3Util.sendByGetUrl(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), headerMap);
                    break;
                case MethodType.POST:
                    // 区分请求类型
                    if (ContentTypeUtil.isWebService(httpRequestEntity.getHeaderMap())) {
                        ws = true;
                        resp = OkHttp3Util.sendByPostXml(OkHttp3Util.getWebServiceUrl(httpRequestEntity), httpRequestEntity.getBodyData(), headerMap);
                    } else if (!ContentTypeUtil.isMultipartFormData(httpRequestEntity.getHeaderMap())) {
                        resp = OkHttp3Util.sendByPostJson(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), httpRequestEntity.getBodyData(), headerMap);
                    } else {
                        Map<String, MultipartFileEntity> multiFileMap = httpRequestEntity.getMultiFileMap();
                        resp = OkHttp3Util.sendByPostMultipart(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), httpRequestEntity.getBodyData(), headerMap, multiFileMap);
                    }
                    break;
                case MethodType.PUT:
                    // 区分请求类型
                    resp = OkHttp3Util.sendByPutJson(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), httpRequestEntity.getBodyData(), headerMap);
                    break;
                case MethodType.DELETE:
                    // 区分请求类型
                    resp = OkHttp3Util.sendByDeleteJson(OkHttp3Util.getEncodeRequestUrl(httpRequestEntity), httpRequestEntity.getBodyData(), headerMap);
                    break;
            }
        } catch (Exception e) {
            log.error("请求异常 : {}", e);
            if (!ws) {
                resp.setResponse(COMMON_ERROR_RESPONSE)
                    .setHeaderMap(APPLICATION_JSON_HEADER);
            } else {
                resp.setResponse(WEBSERVICE_COMMON_ERROR_RESPONSE);
            }
        }

        if (propertyConfig.getLogDebug()) {
            log.info(" [x] -> Request Response {}", resp);
        }
        return resp;
    }
}
