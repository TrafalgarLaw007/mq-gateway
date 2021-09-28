package xin.keepmoving.webservice.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import xin.keepmoving.constants.ContentTypeConstant;
import xin.keepmoving.constants.MethodType;
import xin.keepmoving.entity.HttpRequestEntity;
import xin.keepmoving.entity.HttpResponseEntity;
import xin.keepmoving.rabbit.RabbitSender;
import xin.keepmoving.utils.ContentTypeUtil;
import xin.keepmoving.utils.SpringContextHolder;

import java.util.Map;

@Slf4j
public class BaseWebService {

    private static final String WS_CONTENT_TEMPLATE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.keepmoving.xin/\">\n" +
            "   <soapenv:Header/>\n" +
            "   <soapenv:Body>\n" +
            "      <ser:sendValues>\n" +
            "         <!--Optional:-->\n" +
            "         <arg0>\n" +
            "\n" +
            "         \t<![CDATA[\n" +
            "%s" + "        \n" +
            "         \t]]>\n" +
            "         </arg0>\n" +
            "      </ser:sendValues>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

    public String webServiceSender(String publishUrl, String valuesXml) {

        log.info("WebService发布地址 : {}", publishUrl);

        RabbitSender rabbitSender = SpringContextHolder.getBean(RabbitSender.class);
        HttpRequestEntity httpRequestEntity = new HttpRequestEntity();

        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put(ContentTypeUtil.CONTENT_TYPE, ContentTypeConstant.APPLICATION_XML);
        headerMap.put(ContentTypeUtil.CUSTOM_HEADER_WEB_SERVICE, "1");

        httpRequestEntity.setMethod(MethodType.POST)
                .setRequestUri("/wj" + publishUrl.substring(publishUrl.lastIndexOf("/")))
                .setHeaderMap(headerMap)
                .setBodyData(String.format(WS_CONTENT_TEMPLATE, valuesXml));

        HttpResponseEntity response = null;
        try {
            response = (HttpResponseEntity) rabbitSender.send(JSONObject.toJSONString(httpRequestEntity));
        } catch (Exception e) {
            log.error("WebService请求异常", e);
        }
        if (response == null) {
            response = new HttpResponseEntity();
            response.setResponse("0");
        }
        return response.getResponse();
    }

}
