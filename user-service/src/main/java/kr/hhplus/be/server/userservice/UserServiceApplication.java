package kr.hhplus.be.server.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "kr.hhplus.be.server.userservice",
        "kr.hhplus.be.server.config",
        "kr.hhplus.be.server.global"
})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
