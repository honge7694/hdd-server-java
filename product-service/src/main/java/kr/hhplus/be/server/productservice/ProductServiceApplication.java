package kr.hhplus.be.server.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {
        "kr.hhplus.be.server.productservice",
        "kr.hhplus.be.server.config",
        "kr.hhplus.be.server.global"
})
@EntityScan(basePackages = {"kr.hhplus.be.server.productservice"})
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
