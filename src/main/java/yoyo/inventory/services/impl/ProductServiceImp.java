package yoyo.inventory.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.PageUtil;
import yoyo.inventory.common.UniqueChecker;
import yoyo.inventory.dto.request.ProductRequest;
import yoyo.inventory.dto.response.ProductResponse;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.ProductMapper;
import yoyo.inventory.repository.ProductRepository;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.specification.product.ProductFilter;
import yoyo.inventory.specification.product.ProductSpec;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImp  implements ProductService {
    private  final ProductRepository productRepository ;
    private  final ObjectMapper objectMapper ;
    private  final UniqueChecker uniqueChecker ;
    private  final ProductMapper productMapper ;

    @Override
//    @Cacheable(cacheNames = "product-page", key = "#params.toString()")
    public Page<ProductResponse> getAll(Map<String, String> params) {
        ProductFilter filter= objectMapper.convertValue(params , ProductFilter.class);
        Pageable pageable = PageUtil.fromParams(params);

        Specification<Product> spec  = ProductSpec.filterBy(filter);
        return  productRepository.findAll(spec , pageable).map(productMapper::toResponse);
    }

    @Override
//    @Cacheable(cacheNames = "product-entity", key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product",id));
    }

    @Override
//    @Cacheable(cacheNames = "product-response", key = "#id")
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(findById(id));
    }

    @Override
//    @CacheEvict(cacheNames = {"product-page", "product-entity", "product-response"}, allEntries = true)
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        uniqueChecker.verify(productRepository , product , "name" , product.getName());
        uniqueChecker.verify(productRepository , product , "code" , product.getCode());
        Product savedProduct = productRepository.save(product);
        return  productMapper.toResponse(savedProduct);
    }

    @Override
//    @CacheEvict(cacheNames = {"product-page", "product-entity", "product-response"}, allEntries = true)
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findById(id);
        productMapper.updateFromRequest(request , product);
        uniqueChecker.verify(productRepository , product , "name" , product.getName());
        uniqueChecker.verify(productRepository , product , "code" , product.getCode());
        Product savedProduct = productRepository.save(product);
        return  productMapper.toResponse(savedProduct);
    }

    @Override
//    @CacheEvict(cacheNames = {"product-page", "product-entity", "product-response"}, allEntries = true)
    public void delete(Long id) {
        Product product = findById(id);
        product.setStatus(Status.ACTIVE);
        productRepository.save(product);
    }
}
