package pro.nbbt.healthcare.utils;

import com.google.common.collect.Lists;
import org.springframework.util.StringUtils;

import java.util.List;

import static pro.nbbt.healthcare.common.ContentTypeConstant.*;

public final class ContentTypeUtil {

    static final List<String> IO_TYPES;

    static {
        IO_TYPES = Lists.newArrayList(IMAGE_GIF, IMAGE_JPEG, IMAGE_PNG, APPLICATION_PDF, APPLICATION_MSWORD, APPLICATION_OCTET_STREAM);
    }

    /**
     * 判断响应是否为文件
     * @param contentType
     * @return
     */
    public static boolean fileResponse(String contentType) {
        boolean ret = false;
        if (StringUtils.hasLength(contentType)) {
            return ret;
        }
        for (String ct : IO_TYPES) {
            if (contentType.contains(ct)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

}
