package xin.keepmoving;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xin.keepmoving.rabbit.RabbitSender;
import xin.keepmoving.webservice.impl.WebServiceImpl;

/**
 *
 */
@SpringBootApplication
public class RequestGatherApplication implements ApplicationRunner {
    public static void main( String[] args ) {
        SpringApplication.run(RequestGatherApplication.class, args);
    }

    @Autowired
    RabbitSender rabbitSender;

    @Override
    public void run(ApplicationArguments args) {
        // 发布数据接口服务
        WebServiceImpl.publishWebService();
    }
}
