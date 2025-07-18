package kr.hhplus.be.server.productservice.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"kr.hhplus.be.server.product", "kr.hhplus.be.server.config"})
@EnableJpaRepositories(basePackages = "kr.hhplus.be.server.product")
@EntityScan(basePackages = "kr.hhplus.be.server.product")
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
