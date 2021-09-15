package pro.nbbt.healthcare.utils;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static pro.nbbt.healthcare.common.ContentTypeConstant.*;

public final class ContentTypeUtil {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

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
        if (!StringUtils.hasLength(contentType)) {
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
        if (CollectionUtils.isEmpty(headerMap)) {
            return false;
        }
        return isFileResponse(headerMap.get(CONTENT_TYPE));
    }
}
