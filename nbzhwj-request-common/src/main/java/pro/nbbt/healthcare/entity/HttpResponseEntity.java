package pro.nbbt.healthcare.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pro.nbbt.healthcare.utils.ContentTypeUtil;

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
    public int statusCode;

    public byte bytes[];

    public String response;

    public static HttpResponseEntity newHttpResponseEntity() {
        return new HttpResponseEntity();
    }

    public ResponseEntity buildResponseEntity(HttpServletResponse response) throws IOException {

        int code = this.getStatusCode();
        ResponseEntity responseEntity = null;
        if (ContentTypeUtil.isFileResponse(this.headerMap)) {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(this.bytes);
            outputStream.flush();
//            responseEntity = new ResponseEntity<> (HttpStatus.resolve(code));
            ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.resolve(code));
            responseEntity = builder.body(this.response);
        } else {
//            MultiValueMap multiValueMap = new LinkedMultiValueMap<>();
            HttpHeaders httpHeaders = new HttpHeaders();
            if (this.headerMap != null) {
                this.headerMap.forEach((k, v) -> {
                    httpHeaders.add(k, v);
                });
            }
//            responseEntity = new ResponseEntity<> (this.response, httpHeaders, HttpStatus.resolve(code));
            ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.resolve(code));
            builder.headers(httpHeaders);
//            responseEntity = builder.body(this.response);

            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(this.response.getBytes());
            outputStream.flush();

            responseEntity = builder.build();
        }
        return responseEntity;
    }
}
