package pro.nbbt.healthcare.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 多媒体实体
 */
@Data
@Accessors(chain = true)
@ToString
public class MultipartFileEntity implements Serializable {

    private byte[] bytes;

    private String originalFilename;

    private String name;

    private String contentType;

    private long size;
}
