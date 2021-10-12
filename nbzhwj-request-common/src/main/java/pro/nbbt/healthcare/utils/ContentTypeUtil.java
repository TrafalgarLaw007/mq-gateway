package pro.nbbt.healthcare.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pro.nbbt.healthcare.constants.ContentTypeConstant.*;

@Slf4j
public final class ContentTypeUtil {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CUSTOM_HEADER_WEB_SERVICE = "Web-Service";

    private static final List<String> IO_TYPES;

    static {
        IO_TYPES = Lists.newArrayList(IMAGE_GIF, IMAGE_JPEG, IMAGE_PNG, APPLICATION_PDF, APPLICATION_MSWORD, APPLICATION_OCTET_STREAM);
    }

    /**
     * 判断响应是否为文件
     * @param contentType
     * @return
     */
    public static boolean isFileResponse(String contentType) {
        boolean ret = false;
        if (StringUtils.isBlank(contentType)) {
            return false;
        }

        ret = IO_TYPES.contains(contentType) || (contentType.contains(";") && IO_TYPES.contains(contentType.substring(0, contentType.indexOf(";"))));
        if (ret) {
            return true;
        }

        for (String ct : IO_TYPES) {
            if (contentType.contains(ct)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    /**
     * 判断响应是否为文件
     * @param headerMap
     * @return
     */
    public static boolean isFileResponse(Map<String, String> headerMap) {
        if (headerMap == null || headerMap.size() == 0) {
            return false;
        }
        return isFileResponse(headerMap.get(CONTENT_TYPE)) || isFileResponse(headerMap.get(CONTENT_TYPE.toLowerCase())) ;
    }

    /**
     * 判断请求是否为文件
     * @param headerMap
     * @return
     */
    public static boolean isMultipartFormData(Map<String, String> headerMap) {
        if (headerMap == null || headerMap.size() == 0) {
            return false;
        }
        String contentType = Optional.ofNullable(headerMap.get(CONTENT_TYPE)).orElse(headerMap.get(CONTENT_TYPE.toLowerCase()));
        if (StringUtils.isBlank(contentType)) {
            return false;
        }
        return contentType.contains(MULTIPART_FORM_DATA);
    }

    /**
     * 判断请求是否为WebService
     * @param headerMap
     * @return
     */
    public static boolean isWebService(Map<String, String> headerMap) {
        if (headerMap == null || headerMap.size() == 0) {
            return false;
        }
        log.info(headerMap.toString());
        log.info("是否为WebService : {}", (headerMap.containsKey(CUSTOM_HEADER_WEB_SERVICE) || headerMap.containsKey(CUSTOM_HEADER_WEB_SERVICE.toLowerCase())));
        /*String contentType = Optional.ofNullable(headerMap.get(CONTENT_TYPE)).orElse(headerMap.get(CONTENT_TYPE.toLowerCase()));
        if (StringUtils.isBlank(contentType)) {
            return false;
        }
        log.info(headerMap.toString());
        return contentType.contains(APPLICATION_XML)
                && headerMap.containsKey(CUSTOM_HEADER_WEB_SERVICE);*/
        return headerMap.containsKey(CUSTOM_HEADER_WEB_SERVICE) || headerMap.containsKey(CUSTOM_HEADER_WEB_SERVICE.toLowerCase());
    }

    private static boolean containKey(Map<String, String> headerMap, String expireKey) {
        return headerMap.containsKey(expireKey) && headerMap.containsKey(expireKey.toLowerCase());
    }

    private static boolean containKeyAndValue(Map<String, String> headerMap, String expireKey, String containValue) {
        return Optional.ofNullable(headerMap.get(expireKey)).orElse(headerMap.get(expireKey.toLowerCase())).contains(containValue);
    }
}
