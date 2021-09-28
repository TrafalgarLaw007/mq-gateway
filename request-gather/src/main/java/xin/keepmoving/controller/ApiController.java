package xin.keepmoving.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xin.keepmoving.entity.HttpRequestEntity;
import xin.keepmoving.entity.HttpResponseEntity;
import xin.keepmoving.rabbit.RabbitSender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * All request entrance
 */
@RestController
@Slf4j
public class ApiController {

    @Autowired
    RabbitSender rabbitSender;

    /**
     * 所有请求统一入口
     */
    @RequestMapping(value = "/api/**", produces="application/json;charset=UTF-8")
    public ResponseEntity api(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpResponseEntity resp = (HttpResponseEntity) rabbitSender.send(HttpRequestEntity.builderJSON(request), request, response) ;
        ResponseEntity responseEntity = resp.buildResponseEntity(response);
        log.info("请求地址: {}，响应状态码 : {}，响应内容 : {}", request.getRequestURL(), resp.getStatusCode(), resp.getResponse());
        return responseEntity;
    }
}
