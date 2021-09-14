package pro.nbbt.healthcare.entity;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;

@Data
@Accessors(chain = true)
@ToString
public class HttpRequestEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求地址
     */
    public String requestUrl;
    /**
     * 请求地址
     */
    public String requestUri;
    /**
     * 请求类型
     */
    public String method;
    /**
     * Cookie
     */
    public Cookie[] cookies;
    /**
     * 请求头名称
     */
//    public Enumeration<String> headerNames;
    /**
     * 请求头键值对
     */
    public Map<String, String> headerMap;
    /**
     * 请求参数
     */
    public Map<String, String[]> parameterMap;
    /**
     * body数据
     */
    public String bodyData;
    /**
     * Session ID
     */
    public String sessionId;

    /**
     * 构建Http请求实体JSON字符串
     * @param request
     * @return
     */
    public static String builderJSON(HttpServletRequest request) {
        return JSONObject.toJSONString(builder(request));
    }

    /**
     * 构建Http请求实体
     * @param request
     * @return
     */
    public static HttpRequestEntity builder(HttpServletRequest request) {
        HttpRequestEntity httpRequestEntity = new HttpRequestEntity();
        httpRequestEntity.setRequestUrl(request.getRequestURL().toString())
                .setRequestUri(request.getRequestURI())
            .setMethod(request.getMethod())
            .setSessionId(request.getSession().getId());

        assembleCookies(request, httpRequestEntity);
        assembleHeaders(request, httpRequestEntity);
        assembleParameterMap(request, httpRequestEntity);
        assembleBodyData(request, httpRequestEntity);
        // TODO 文件上传
        // TODO WebService支持
        return httpRequestEntity;
    }

    /**
     * 设置Cookie信息
     * @param request
     * @param httpRequestEntity
     */
    private static void assembleCookies(HttpServletRequest request, HttpRequestEntity httpRequestEntity) {
        httpRequestEntity.setCookies(request.getCookies());
    }

    /**
     * 设置请求头信息
     * @param request
     * @param httpRequestEntity
     */
    private static void assembleHeaders(HttpServletRequest request, HttpRequestEntity httpRequestEntity) {
        Enumeration<String> headerNames = request.getHeaderNames();
        String headerName = null;
        Map<String, String> headerMap = Maps.newHashMap();
        while (headerNames.hasMoreElements()) {
            headerName = headerNames.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        httpRequestEntity
//                .setHeaderNames(headerNames)
                .setHeaderMap(headerMap);
    }

    /**
     * 设置请求参数Map
     * @param request
     * @param httpRequestEntity
     */
    private static void assembleParameterMap(HttpServletRequest request, HttpRequestEntity httpRequestEntity) {
        httpRequestEntity.setParameterMap(request.getParameterMap());
    }

    /**
     * 设置请求体数据
     * @param request
     * @param httpRequestEntity
     */
    private static void assembleBodyData(HttpServletRequest request, HttpRequestEntity httpRequestEntity) {
        try {
            String bodyData = getPostData(request);
            httpRequestEntity.setBodyData(bodyData);
        } catch (Exception e) {

        }
    }

    private static String getPostData(HttpServletRequest request) {
        StringBuffer data = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            while (null != (line = reader.readLine()))
                data.append(line);
        } catch (IOException e) {
        } finally {
        }
        return data.toString();
    }

}
