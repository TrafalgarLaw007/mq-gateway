package pro.nbbt.healthcare.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.nbbt.healthcare.entity.HttpRequestEntity;
import pro.nbbt.healthcare.entity.HttpResponseEntity;
import pro.nbbt.healthcare.rabbit.RabbitSender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@Slf4j
public class ApiController {

    @Autowired
    RabbitSender rabbitSender;

    @RequestMapping(value = "/api/**", produces="application/json;charset=UTF-8")
    public Object api(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        log.info("请求地址: {}", request.getRequestURL());
        HttpResponseEntity resp = (HttpResponseEntity) rabbitSender.send(HttpRequestEntity.builderJSON(request), request, response) ;
        // 写出文件
        if (resp != null) {
             // response.getOutputStream().write(resp.getResponse().getBytes());
        }
        response.setHeader("Content-Type", resp.getHeaderMap().get("Content-Type"));
        log.info("响应内容 : {}", resp);
        return resp.getResponse();
    }
}
