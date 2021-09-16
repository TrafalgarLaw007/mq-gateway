package pro.nbbt.healthcare.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.nbbt.healthcare.constants.ContentTypeConstant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pro.nbbt.healthcare.constants.ContentTypeConstant.*;

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
        return Optional.ofNullable(headerMap.get(CONTENT_TYPE)).orElse(headerMap.get(CONTENT_TYPE.toLowerCase())).contains(MULTIPART_FORM_DATA);
    }

    /**
     * 判断请求是否为文件
     * @param headerMap
     * @return
     */
    public static boolean isWebService(Map<String, String> headerMap) {
        if (headerMap == null || headerMap.size() == 0) {
            return false;
        }
        return headerMap.get(CONTENT_TYPE).contains(APPLICATION_XML) && headerMap.containsKey(CUSTOM_HEADER_WEB_SERVICE);
    }
}
