package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import yoyo.inventory.dto.request.ProductRequest;
import yoyo.inventory.dto.response.ProductImportResult;
import yoyo.inventory.dto.response.ProductResponse;
import yoyo.inventory.entities.Product;

import java.util.Map;

public interface ProductService {
    Page<ProductResponse> getAll(Map<String , String> params);
    Product  findById(Long id);
    ProductResponse getById(Long id);
    ProductResponse create(ProductRequest request ,  MultipartFile file);
    ProductResponse update(Long id , ProductRequest request);
    void delete(Long id);
    ProductImportResult importFromExcel(MultipartFile file);
    byte[] exportToExcel();
}
