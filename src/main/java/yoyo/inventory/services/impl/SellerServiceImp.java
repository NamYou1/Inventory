package yoyo.inventory.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.Pagination;
import yoyo.inventory.common.UniqueChecker;
import yoyo.inventory.dto.request.SellerRequest;
import yoyo.inventory.dto.response.SellerResponse;
import yoyo.inventory.entities.Seller;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundExecption;
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
    public Page<SellerResponse> getAll(Map<String, String> params) {
        SellerFilter filter = objectMapper.convertValue(params , SellerFilter.class);
        int pageNumber  = params.containsKey(Pagination.PAGE_NUMBER) ? Integer.parseInt(params.get(Pagination.PAGE_NUMBER)) : Pagination.DEFAULT_PAGE_NUMBER;
        int pageSize  = params.containsKey(Pagination.DEFAULT_PAGE_LIMIT) ? Integer.parseInt(params.get(Pagination.DEFAULT_PAGE_LIMIT)) : Pagination.DEFAULT_PAGE_LIMIT;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Seller> spec = SellerSpec.filterBy(filter);
        return  sellerRepository.findAll(spec, pageable).map(sellerMapper::toResponse);
    }

    @Override
    public SellerResponse getById(Long id) {
        return  sellerMapper.toResponse(findByid(id));
    }

    @Override
    public Seller findByid(Long id) {
        return  sellerRepository.findById(id).orElseThrow(()->new ResourceNotFoundExecption("Seller" , id));
    }

    @Override
    public SellerResponse createSeller(SellerRequest request) {
        Seller seller = sellerMapper.toEntity(request);
        uniqueChecker.verify(sellerRepository , seller , "phone" , seller.getPhone());
        uniqueChecker.verify(sellerRepository , seller , "email" , seller.getEmail());
        return  sellerMapper.toResponse(sellerRepository.save(seller));


    }

    @Override
    public SellerResponse updateSeller(Long id, SellerRequest request) {
       Seller seller = findByid(id);
       sellerMapper.updateFromRequest(request , seller);
       uniqueChecker.verify(sellerRepository , seller , "phone" , seller.getPhone());
       uniqueChecker.verify(sellerRepository , seller , "email" , seller.getEmail());
       return  sellerMapper.toResponse(sellerRepository.save(seller));
    }

    @Override
    public void deleteSeller(Long id) {
        Seller seller = findByid(id);
         seller.setStatus(Status.INACTIVE);
         sellerRepository.save(seller);
    }
}
