package xin.keepmoving.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import xin.keepmoving.utils.ContentTypeUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

@Data
@Accessors(chain = true)
@ToString
public class HttpResponseEntity implements Serializable {

    public Map<String, String> headerMap;

    public InputStream byteStream;

    /**
     * 相应状态码
     */
    public int statusCode = 200;

    public byte bytes[];

    public String response;

    public static HttpResponseEntity newHttpResponseEntity() {
        return new HttpResponseEntity();
    }

    public ResponseEntity buildResponseEntity(HttpServletResponse response) throws IOException {

        int code = this.getStatusCode();
        if (ContentTypeUtil.isFileResponse(this.headerMap)) {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(this.bytes);
            outputStream.flush();
            return new ResponseEntity(this.response, HttpStatus.resolve(code));
        } else {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (this.headerMap != null && this.headerMap.containsKey(ContentTypeUtil.CONTENT_TYPE)) {
                httpHeaders.add(ContentTypeUtil.CONTENT_TYPE, this.headerMap.get(ContentTypeUtil.CONTENT_TYPE));
            }
            return new ResponseEntity(this.response, httpHeaders, HttpStatus.resolve(code));
        }
    }
}
