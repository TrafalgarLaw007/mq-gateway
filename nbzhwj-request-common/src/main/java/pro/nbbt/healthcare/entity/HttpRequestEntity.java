package pro.nbbt.healthcare.entity;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import pro.nbbt.healthcare.constants.ContentTypeConstant;
import pro.nbbt.healthcare.constants.MethodType;
import pro.nbbt.healthcare.utils.ContentTypeUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
@ToString
@Slf4j
public class HttpRequestEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求地址
     */
    private String requestUrl;
    /**
     * 请求地址
     */
    private String requestUri;
    /**
     * 请求类型
     */
    private String method;
    /**
     * Cookie
     */
    private Cookie[] cookies;
    /**
     * 请求头键值对
     */
    private Map<String, String> headerMap;
    /**
     * 请求参数
     */
    private Map<String, String[]> parameterMap;
    /**
     * body数据
     */
    private String bodyData;
    /**
     * Session ID
     */
    private String sessionId;

    /**
     * 多媒体
     */
//    MultiValueMap<String, MultipartFile> multiFileMap;
    Map<String, MultipartFileEntity> multiFileMap;

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
                .setRequestUri(request.getRequestURI().substring(4))
            .setMethod(request.getMethod().toUpperCase())
            .setSessionId(request.getSession().getId());

        assembleCookies(request, httpRequestEntity);
        assembleHeaders(request, httpRequestEntity);
        assembleParameterMap(request, httpRequestEntity);
        if (!MethodType.GET.equalsIgnoreCase(httpRequestEntity.getMethod())) {
            assembleBodyData(request, httpRequestEntity);
            if (httpRequestEntity.getHeaderMap().containsKey(ContentTypeUtil.CONTENT_TYPE.toLowerCase())
                    && httpRequestEntity.getHeaderMap().get(ContentTypeUtil.CONTENT_TYPE.toLowerCase()).contains(ContentTypeConstant.MULTIPART_FORM_DATA)) {
                // 文件上传
                assembleMultipartFile(request, httpRequestEntity);
            }
        }
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
        Map<String, String[]> parameterMap = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            try {
                String[] newValues = new String[v.length];
                for (int i = 0; i < v.length; i++) {
                    newValues[i] = URLEncoder.encode(v[i], "UTF-8");
                }
                parameterMap.put(k, newValues);
                // log.info("请求参数 : {} - {} -> {}", k, v, newValues);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        httpRequestEntity.setParameterMap(parameterMap);
    }

    /**
     * 设置请求体数据
     * @param request
     * @param httpRequestEntity
     */
    private static void assembleBodyData(HttpServletRequest request, HttpRequestEntity httpRequestEntity) {
        try {
            String bodyData = getPostData(request);
            log.info("请求体内容 : {}", bodyData);
            httpRequestEntity.setBodyData(bodyData);
        } catch (Exception e) {
            log.info("获取请求体内容异常 ： {}", e);
        }
    }

    /**
     * 设置上传文件数据
     * @param request
     * @param httpRequestEntity
     */
    private static void assembleMultipartFile(HttpServletRequest request, HttpRequestEntity httpRequestEntity) {
        MultiValueMap<String, MultipartFile> multiFileMap = ((StandardMultipartHttpServletRequest) request).getMultiFileMap();
        Map<String, MultipartFileEntity> multiFileMapData = Maps.newHashMap();
        multiFileMap.forEach((k, v) -> {
            log.info("多媒体 -> {} - {}", k, v.get(0).getOriginalFilename());
            multiFileMapData.put(k, assembleMultipartFileEntity(v.get(0)));
        });
        httpRequestEntity.setMultiFileMap(multiFileMapData);
    }

    private static MultipartFileEntity assembleMultipartFileEntity(MultipartFile multipartFile) {
        MultipartFileEntity multipartFileEntity = new MultipartFileEntity();

        try {
            multipartFileEntity.setBytes(multipartFile.getBytes())
                    .setContentType(multipartFile.getContentType())
                    .setOriginalFilename(multipartFile.getOriginalFilename())
                    .setName(multipartFile.getName())
                    .setSize(multipartFile.getSize());
        } catch (Exception e) {
            log.warn("媒体文件解析失败: {}", e.getMessage());
        }
        return multipartFileEntity;
    }

    /**
     * 获取请求体参数
     * @param request
     * @return
     */
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
