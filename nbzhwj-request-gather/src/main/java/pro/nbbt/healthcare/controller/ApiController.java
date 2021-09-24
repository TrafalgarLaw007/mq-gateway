package pro.nbbt.healthcare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.nbbt.healthcare.entity.HttpRequestEntity;
import pro.nbbt.healthcare.entity.HttpResponseEntity;
import pro.nbbt.healthcare.rabbit.RabbitSender;
import pro.nbbt.healthcare.utils.ContentTypeUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static pro.nbbt.healthcare.utils.ContentTypeUtil.CONTENT_TYPE;

@RestController
@Slf4j
public class ApiController {

    @Autowired
    RabbitSender rabbitSender;

    /**
     * 所有请求同一入口
     */
    @RequestMapping(value = "/api/**", produces="application/json;charset=UTF-8")
    public Object api(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("请求地址: {}", request.getRequestURL());
        HttpResponseEntity resp = (HttpResponseEntity) rabbitSender.send(HttpRequestEntity.builderJSON(request), request, response) ;
        // 写出文件
        if (resp != null && ContentTypeUtil.isFileResponse(resp.getHeaderMap())) {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(resp.getBytes());
            outputStream.flush();
        }
        if (resp.getHeaderMap() != null) {
            response.setHeader(CONTENT_TYPE, resp.getHeaderMap().get(CONTENT_TYPE));
        }
        log.info("响应内容 : {}", resp.getResponse());
        return resp.getResponse();
    }

    /*@RequestMapping(value = "/wj/**")
    public Object webservice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("请求地址: {}", request.getRequestURL());

        return request.getRequestURL();
    }*/

    /**
     * 所有请求同一入口
     */
    /*@RequestMapping(value = "/wj/**", produces="application/json;charset=UTF-8")
    public Object webservice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("请求地址: {}", request.getRequestURL());
        HttpResponseEntity resp = (HttpResponseEntity) rabbitSender.send(HttpRequestEntity.builderJSON(request), request, response) ;
        // 写出文件
        if (resp != null && ContentTypeUtil.isFileResponse(resp.getHeaderMap())) {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(resp.getBytes());
            outputStream.flush();
        }
        response.setHeader(CONTENT_TYPE, resp.getHeaderMap().get(CONTENT_TYPE));
        log.info("响应内容 : {}", resp.getResponse());
        return resp.getResponse();
    }*/
}
