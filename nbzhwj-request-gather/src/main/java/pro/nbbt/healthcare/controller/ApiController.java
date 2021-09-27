package pro.nbbt.healthcare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.nbbt.healthcare.entity.HttpRequestEntity;
import pro.nbbt.healthcare.entity.HttpResponseEntity;
import pro.nbbt.healthcare.rabbit.RabbitSender;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@RestController
//@Controller
@Slf4j
public class ApiController {

    @Autowired
    RabbitSender rabbitSender;

    /**
     * 所有请求同一入口
     */
    @RequestMapping(value = "/api/**", produces="application/json;charset=UTF-8")
    public ResponseEntity api(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("请求地址: {}", request.getRequestURL());
        HttpResponseEntity resp = (HttpResponseEntity) rabbitSender.send(HttpRequestEntity.builderJSON(request), request, response) ;
        ResponseEntity responseEntity = resp.buildResponseEntity(response);
        log.info("响应状态码 : {}，实际响应码：{}，响应内容 : {}", resp.getStatusCode(), responseEntity.getStatusCodeValue(), resp.getResponse());
        return responseEntity;
    }

    @RequestMapping(value= "/404")
    public ResponseEntity unauth(HttpServletResponse response) throws IOException {

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.resolve(401));

        /*ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write("no auth".getBytes());
        outputStream.flush();

        response.setStatus(401);*/

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Hello", "world");

        return new ResponseEntity("no auth", httpHeaders, HttpStatus.resolve(401));
    }
}
