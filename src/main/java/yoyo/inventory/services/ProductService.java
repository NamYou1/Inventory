package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.ProductRequest;
import yoyo.inventory.dto.response.ProductResponse;
import yoyo.inventory.entities.Product;

import java.util.Map;

public interface ProductService {
    Page<ProductResponse> getAll(Map<String , String> params);
    Product  findById(Long id);
    ProductResponse getById(Long id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id , ProductRequest request);
    void delete(Long id);
}
