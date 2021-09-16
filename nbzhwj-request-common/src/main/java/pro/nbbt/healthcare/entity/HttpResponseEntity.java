package pro.nbbt.healthcare.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

@Data
@Accessors(chain = true)
@ToString
public class HttpResponseEntity implements Serializable {

    public Map<String, String> headerMap;

    public InputStream byteStream;

    public byte bytes[];

    public String response;

    public static HttpResponseEntity newHttpResponseEntity() {
        return new HttpResponseEntity();
    }
}
