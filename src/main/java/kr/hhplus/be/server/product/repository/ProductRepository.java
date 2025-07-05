package kr.hhplus.be.server.product.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByWithLock(@Param("id") Long productId);

    List<Product> findByIdIn(List<Long> productIds);
}
