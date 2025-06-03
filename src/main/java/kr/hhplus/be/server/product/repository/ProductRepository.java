package kr.hhplus.be.server.product.repository;

import kr.hhplus.be.server.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {


}
