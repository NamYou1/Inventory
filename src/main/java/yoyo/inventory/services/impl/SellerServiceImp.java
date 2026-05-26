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
import yoyo.inventory.dto.request.SellerRequest;
import yoyo.inventory.dto.response.SellerResponse;
import yoyo.inventory.entities.Seller;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.SellerMapper;
import yoyo.inventory.repository.SellerRepository;
import yoyo.inventory.services.SellerService;
import yoyo.inventory.specification.sellers.SellerFilter;
import yoyo.inventory.specification.sellers.SellerSpec;

import java.util.Map;
@Service
@RequiredArgsConstructor
public class SellerServiceImp implements SellerService {
    private  final SellerRepository sellerRepository;
    private  final ObjectMapper objectMapper ;
    private  final SellerMapper sellerMapper ;
    private  final UniqueChecker uniqueChecker ;
    @Override
//    @Cacheable(cacheNames = "seller-page", key = "#params.toString()")
    public Page<SellerResponse> getAll(Map<String, String> params) {
        SellerFilter filter = objectMapper.convertValue(params , SellerFilter.class);
        Pageable pageable = PageUtil.fromParams(params);

        Specification<Seller> spec = SellerSpec.filterBy(filter);
        return  sellerRepository.findAll(spec, pageable).map(sellerMapper::toResponse);
    }

    @Override
//    @Cacheable(cacheNames = "seller-response", key = "#id")
    public SellerResponse getById(Long id) {
        return  sellerMapper.toResponse(findBid(id));
    }

    @Override
//    @Cacheable(cacheNames = "seller-entity", key = "#id")
    public Seller findBid(Long id) {
        return  sellerRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Seller" , id));
    }

    @Override
//    @CacheEvict(cacheNames = {"seller-page", "seller-response", "seller-entity"}, allEntries = true)
    public SellerResponse createSeller(SellerRequest request) {
        Seller seller = sellerMapper.toEntity(request);
        uniqueChecker.verify(sellerRepository , seller , "phone" , seller.getPhone());
        uniqueChecker.verify(sellerRepository , seller , "email" , seller.getEmail());
        return  sellerMapper.toResponse(sellerRepository.save(seller));


    }

    @Override
//    @CacheEvict(cacheNames = {"seller-page", "seller-response", "seller-entity"}, allEntries = true)
    public SellerResponse updateSeller(Long id, SellerRequest request) {
       Seller seller = findBid(id);
       sellerMapper.updateFromRequest(request , seller);
       uniqueChecker.verify(sellerRepository , seller , "phone" , seller.getPhone());
       uniqueChecker.verify(sellerRepository , seller , "email" , seller.getEmail());
       return  sellerMapper.toResponse(sellerRepository.save(seller));
    }

    @Override
//    @CacheEvict(cacheNames = {"seller-page", "seller-response", "seller-entity"}, allEntries = true)
    public void deleteSeller(Long id) {
        Seller seller = findBid(id);
         seller.setStatus(Status.INACTIVE);
         sellerRepository.save(seller);
    }
}
