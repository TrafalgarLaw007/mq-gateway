package pro.nbbt.healthcare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.nbbt.healthcare.entity.HttpRequestEntity;
import pro.nbbt.healthcare.entity.HttpResponseEntity;
import pro.nbbt.healthcare.rabbit.RabbitSender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
//@Controller
@Slf4j
public class ApiController {

    @Autowired
    RabbitSender rabbitSender;

    /**
     * 所有请求统一入口
     */
    @RequestMapping(value = "/api/**", produces="application/json;charset=UTF-8")
    public ResponseEntity api(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("请求地址: {}", request.getRequestURL());
        HttpResponseEntity resp = (HttpResponseEntity) rabbitSender.send(HttpRequestEntity.builderJSON(request), request, response) ;
        ResponseEntity responseEntity = resp.buildResponseEntity(response);
        log.info("响应状态码 : {}，实际响应码：{}，响应内容 : {}", resp.getStatusCode(), responseEntity.getStatusCodeValue(), resp.getResponse());
        return responseEntity;
    }
}
