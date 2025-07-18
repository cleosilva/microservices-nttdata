package cleosilva.order_simulator.client;

import cleosilva.order_simulator.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "products-catalog")
public interface ProductCatalogFeignClient {

    @GetMapping("/products/{id}")
    ProductDto findProductById(@PathVariable("id") Long id);

    @GetMapping("/products")
    List<ProductDto> listProducts();
}
