package kr.hhplus.be.server.couponservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "kr.hhplus.be.server.couponservice",
        "kr.hhplus.be.server.config",
        "kr.hhplus.be.server.global"
})
public class CouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }

}
