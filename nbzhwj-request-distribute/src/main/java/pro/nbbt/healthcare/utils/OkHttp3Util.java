package pro.nbbt.healthcare.utils;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.CollectionUtils;
import pro.nbbt.healthcare.config.PropertyConfig;
import pro.nbbt.healthcare.config.RemoteConfig;
import pro.nbbt.healthcare.entity.HttpRequestEntity;
import pro.nbbt.healthcare.entity.HttpResponseEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttp3Util {

    //MEDIA_TYPE <==> Content-Type
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    //MEDIA_TYPE_TEXT post请求不是application/x-www-form-urlencoded的，全部直接返回，不作处理，即不会解析表单数据来放到request parameter map中。所以通过request.getParameter(name)是获取不到的。只能使用最原始的方式，读取输入流来获取。
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    /**
     * @param url getUrl
     * @return java.lang.String
     * @author nirvana
     * @date 2019/3/4 11:20
     * @descprition
     * @version 1.0
     */
    public static String sendByGetUrl(String url) {
        String result;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            assert response.body() != null;
            result = response.body().string();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param url getUrl
     * @return java.lang.String
     * @author nirvana
     * @date 2019/3/4 11:20
     * @descprition
     * @version 1.0
     */
    public static String sendByGetUrl(String url, Map<String, String> header) {
        String result;
        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder().url(url);
        if (!CollectionUtils.isEmpty(header)) {
            header.forEach((k, val) -> {
                builder.addHeader(k, val);
            });
        }
        Request request = builder
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            assert response.body() != null;

            Set<String> headerNames = response.headers().names();
            headerNames.forEach(e -> {
                log.info("响应头 : {} - {}", e, response.header(e));
            });

            result = response.body().string();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpResponseEntity sendByGetUrl2(String url, Map<String, String> header) {
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
     * @author nirvana
     * @date 2019/3/4 11:13
     * @descprition
     * @version 1.0 post+json方式
     */
    public static String sendByPostJson(String url, String json) throws IOException {

        //        OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 , TimeUnit.SECONDS)
                .writeTimeout(10 ,TimeUnit.SECONDS)
                .readTimeout(10 ,TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;

        response = client.newCall(request).execute();
        assert response.body() != null;
        return response.body().string();
    }

    /**
     * @param url , json
     * @return java.lang.String
     * @author xiaobu
     * @date 2019/3/4 11:13
     * @descprition
     * @version 1.0 post+json方式
     */
    public static String sendByPostJson(String url, String json, Map<String, String> header) throws IOException {

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
        Response response = null;

        response = client.newCall(request).execute();
        assert response.body() != null;
        return response.body().string();
    }

    /**
     * @param url , json
     * @return java.lang.String
     * @author xiaobu
     * @date 2019/3/4 11:13
     * @descprition
     * @version 1.0 post+json方式
     */
    public static HttpResponseEntity sendByPostJson2(String url, String json, Map<String, String> header) throws IOException {

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
     * @author xiaobu
     * @date 2019/3/4 15:58
     * @param url , params]
     * @return java.lang.String
     * @descprition  post方式请求
     * @version 1.0
     */
    public static String sendByPostMap(String url, Map<String, String> params) {
        String result;
        OkHttpClient client = new OkHttpClient();
        StringBuilder content = new StringBuilder();
        Set<Map.Entry<String, String>> entrys = params.entrySet();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            content.append(entry.getKey()).append("=").append(entry.getValue());
            if (iterator.hasNext()) {
                content.append("&");
            }
        }

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_TEXT, content.toString());
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            assert response.body() != null;
            result = response.body().string();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
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
    public static HttpResponseEntity sendByPutJson2(String url, String json, Map<String, String> header) throws IOException {

        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();

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
    public static HttpResponseEntity sendByDeleteJson2(String url, String json, Map<String, String> header) throws IOException {

        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();

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
     * @param url getUrl
     * @return java.lang.String
     * @author nirvana
     * @date 2019/3/4 11:20
     * @descprition
     * @version 1.0
     */
    public static void downloadByGetUrl(String url, Map<String, String> header, String path) {
        InputStream is = null;
        FileOutputStream fos = null;
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder()
                .url(url);

        header.forEach((k, val) -> {
            builder.addHeader(k, val);
        });
        Request request = builder.build();
        Response response = null;
        try {
            response = client.newCall(request).execute();

            assert response.body() != null;
            is = response.body().byteStream();

            File file = new File(path);
            fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            int sum = 0;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes);
                sum += len;
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
}
