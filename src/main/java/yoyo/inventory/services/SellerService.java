package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.SellerRequest;
import yoyo.inventory.dto.response.SellerResponse;
import yoyo.inventory.entities.Seller;

import java.util.Map;

public interface SellerService {
   Page<SellerResponse> getAll(Map<String , String> params);
   SellerResponse getById(Long id);
   Seller findByid(Long id );
   SellerResponse createSeller (SellerRequest request);
   SellerResponse updateSeller(Long id , SellerRequest request);
   void deleteSeller(Long id);

}
