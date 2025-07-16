package cleosilva.microservices.product_catalog.repository;

import cleosilva.microservices.product_catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
