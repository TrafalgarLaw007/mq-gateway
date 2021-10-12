package pro.nbbt.healthcare.utils;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.CollectionUtils;
import pro.nbbt.healthcare.config.PropertyConfig;
import pro.nbbt.healthcare.config.RemoteConfig;
import pro.nbbt.healthcare.constants.ContentTypeConstant;
import pro.nbbt.healthcare.constants.MethodType;
import pro.nbbt.healthcare.entity.HttpRequestEntity;
import pro.nbbt.healthcare.entity.HttpResponseEntity;
import pro.nbbt.healthcare.entity.MultipartFileEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttp3Util {

    //MEDIA_TYPE <==> Content-Type
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    //MEDIA_TYPE_TEXT post请求不是application/x-www-form-urlencoded的，全部直接返回，不作处理，即不会解析表单数据来放到request parameter map中。所以通过request.getParameter(name)是获取不到的。只能使用最原始的方式，读取输入流来获取。
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    //
    private static final MediaType MEDIA_TYPE_MULTIPART = MediaType.parse("multipart/form-data");
    private static final MediaType MEDIA_TYPE_TEXT_XML = MediaType.parse("text/xml");
    private static final MediaType MEDIA_TYPE_APPLICATION_XML = MediaType.parse("application/xml");

    private static final String WEB_SERVICE_RETURN_PREFIX = "<return>";
    private static final String WEB_SERVICE_RETURN_SUFFIX = "</return>";

    public static HttpResponseEntity sendByGetUrl(String url, Map<String, String> header) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder().url(url);
        if (!CollectionUtils.isEmpty(header)) {
            header.forEach((k, val) -> {
                builder.addHeader(k, val);
            });
        }
        Request request = builder .build();

        Response response;
        InputStream in = null;
        try {
            response = client.newCall(request).execute();
            assert response.body() != null;
            // 解析响应
            return assembleResponse(response, httpResponseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param url , json
     * @return java.lang.String
     * @author xiaobu
     * @date 2019/3/4 11:13
     * @descprition
     * @version 1.0 post+json方式
     */
    public static HttpResponseEntity sendByPostJson(String url, String json, Map<String, String> header) throws IOException {

        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 , TimeUnit.SECONDS)
                .writeTimeout(10 ,TimeUnit.SECONDS)
                .readTimeout(10 ,TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);

        header.forEach((k, val) -> {
            builder.addHeader(k, val);
        });

        Request request = builder.build();
        Response response;

        response = client.newCall(request).execute();
        assert response.body() != null;

        // 解析响应
        return assembleResponse(response, httpResponseEntity);
    }

    /**
     * @param url , json
     * @param multiFileMap
     * @return java.lang.String
     * @author xiaobu
     * @date 2019/3/4 11:13
     * @descprition
     * @version 1.0 post+multipart/form-data方式
     */
    public static HttpResponseEntity sendByPostMultipart(String url, String json, Map<String, String> header, Map<String, MultipartFileEntity> multiFileMap) throws IOException {

        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 , TimeUnit.SECONDS)
                .writeTimeout(10 ,TimeUnit.SECONDS)
                .readTimeout(10 ,TimeUnit.SECONDS)
                .build();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MEDIA_TYPE_MULTIPART);

        multiFileMap.forEach((k, v) -> {
            builder.addFormDataPart(k, v.getName(), RequestBody.create(MEDIA_TYPE_MULTIPART, v.getBytes()));
        });

        MultipartBody body = builder.build();
        Headers headers = Headers.of(header);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(body);

        Request request = requestBuilder.build();
        Response response;

        response = client.newCall(request).execute();
        assert response.body() != null;

        // 解析响应
        return assembleResponse(response, httpResponseEntity);
    }

    /**
     * @param url , json
     * @return java.lang.String
     * @author xiaobu
     * @date 2019/3/4 11:13
     * @descprition
     * @version 1.0 post+json方式
     */
    public static HttpResponseEntity sendByPostXml(String url, String xml, Map<String, String> header) throws IOException {
        log.info("sendByPostXml -> 请求地址: {}， 请求内容： {}", url, xml);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.newHttpResponseEntity();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 , TimeUnit.SECONDS)
                .writeTimeout(10 ,TimeUnit.SECONDS)
                .readTimeout(10 ,TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(MEDIA_TYPE_APPLICATION_XML, xml);

//        log.info("请求内容 : {}" , xml);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(MethodType.POST, body)
                .addHeader(ContentTypeUtil.CONTENT_TYPE, ContentTypeConstant.APPLICATION_XML);

        Request request = builder.build();
        Response response;

        response = client.newCall(request).execute();
        assert response.body() != null;

        // 解析响应
        assembleResponse(response, httpResponseEntity);
        // WebService需要解析<return></return>之间的内容
        String responseStr = httpResponseEntity.getResponse();
        httpResponseEntity.setResponse(responseStr.substring(responseStr.indexOf("<return>") + 8, responseStr.indexOf("</return>")));
        return httpResponseEntity;
    }


    /**
     * @param url , json
     * @return java.lang.String
     * @author xiaobu
     * @date 2019/3/4 11:13
     * @descprition
     * @version 1.0 post+json方式
     */
    public static HttpResponseEntity sendByPutJson(String url, String json, Map<String, String> header) throws IOException {

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.newHttpResponseEntity();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 , TimeUnit.SECONDS)
                .writeTimeout(10 ,TimeUnit.SECONDS)
                .readTimeout(10 ,TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .put(body);

        header.forEach((k, val) -> {
            builder.addHeader(k, val);
        });

        Request request = builder.build();
        Response response;

        response = client.newCall(request).execute();
        assert response.body() != null;

        // 解析响应
        return assembleResponse(response, httpResponseEntity);
    }

    /**
     * @param url , json
     * @return java.lang.String
     * @author xiaobu
     * @date 2019/3/4 11:13
     * @descprition
     * @version 1.0 post+json方式
     */
    public static HttpResponseEntity sendByDeleteJson(String url, String json, Map<String, String> header) throws IOException {

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.newHttpResponseEntity();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 , TimeUnit.SECONDS)
                .writeTimeout(10 ,TimeUnit.SECONDS)
                .readTimeout(10 ,TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .delete(body);

        header.forEach((k, val) -> {
            builder.addHeader(k, val);
        });

        Request request = builder.build();
        Response response;

        response = client.newCall(request).execute();
        assert response.body() != null;

        // 解析响应
        return assembleResponse(response, httpResponseEntity);
    }

    /**
     * 解析请求想用信息
     * 1.响应头信息
     * 2.响应数
     * @param response
     * @param httpResponseEntity
     * @return
     * @throws IOException
     */
    private static HttpResponseEntity assembleResponse(Response response, HttpResponseEntity httpResponseEntity) throws IOException {
        Map<String, String> headerMap = assembleHeaders(response);
        assembleBodyData(headerMap, response, httpResponseEntity);
        httpResponseEntity.setHeaderMap(headerMap);
        httpResponseEntity.setStatusCode(response.code());
        return httpResponseEntity;
    }

    /**
     * 解析请求头
     * @param response
     */
    private static Map<String, String> assembleHeaders(Response response) {

        PropertyConfig properties = SpringContextHolder.getBean(PropertyConfig.class);

        Set<String> headerNames = response.headers().names();
        Map<String, String> headerMap = Maps.newHashMap();
        headerNames.forEach(e -> {
            if (properties.getLogDebug()) {
                log.info("响应头 : {} - {}", e, response.header(e));
            }
            headerMap.put(e, response.header(e));
        });

        return headerMap;
    }

    /**
     * 解析响应数据
     * @param headerMap
     * @param response
     * @param httpResponseEntity
     */
    private static void assembleBodyData(Map<String, String> headerMap, Response response, HttpResponseEntity httpResponseEntity) throws IOException {
        InputStream in = null;
        String result = null;
        try {
            if (ContentTypeUtil.isFileResponse(headerMap)) {
                in = response.body().byteStream();
                byte[] data = new byte[Integer.valueOf(headerMap.get(ContentTypeUtil.CONTENT_LENGTH))];
                in.read(data);
                httpResponseEntity.setBytes(data);
                result = null;
            } else {
                result = response.body().string();
            }
            httpResponseEntity.setHeaderMap(headerMap)
                    .setResponse(result);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    @SneakyThrows
    public static String buildUrl(String url, Map<String, String[]> parameterMap) {
        if (CollectionUtils.isEmpty(parameterMap)) {
            return url;
        }
        StringBuffer sb = new StringBuffer();
        parameterMap.forEach((k, v) -> {
            for (String e : v) {
                sb.append("&").append(k).append("=").append(e);
            }
        });
        return url + "?" + sb.toString().substring(1);
    }

    public static String getEncodeRequestUrl(HttpRequestEntity httpRequestEntity) {
        RemoteConfig remoteConfig = SpringContextHolder.getBean(RemoteConfig.class);
        return OkHttp3Util.buildUrl(remoteConfig.getRequestUrl() + httpRequestEntity.getRequestUri(), httpRequestEntity.getParameterMap());
    }

    public static String getWebServiceUrl(HttpRequestEntity httpRequestEntity) {
        RemoteConfig remoteConfig = SpringContextHolder.getBean(RemoteConfig.class);
        return remoteConfig.getWebServiceRequestUrl() + httpRequestEntity.getRequestUri() + "?wsdl";
    }
}
