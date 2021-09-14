package pro.nbbt.healthcare;

import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pro.nbbt.healthcare.rabbit.RabbitSender;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class RequestGatherApplication implements ApplicationRunner
{
    public static void main( String[] args ) {
        SpringApplication.run(RequestGatherApplication.class, args);
    }

    @Autowired
    RabbitSender rabbitSender;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(() -> {
            try {
                while (1 == 1) {
                    // rabbitSender.send("Hello RabbitMQ - " + LocalDateTime.now().toString(), Maps.newHashMap());
                    // TimeUnit.SECONDS.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}
