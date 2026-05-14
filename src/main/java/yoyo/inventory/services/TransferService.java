package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.TransferRequest;
import yoyo.inventory.dto.response.TransferResponse;

import java.util.Map;

public interface TransferService {

    TransferResponse create(TransferRequest request);

    TransferResponse getById(Long id);

    Page<TransferResponse> getAll(Map<String , String> params);

    TransferResponse update(Long id, TransferRequest request, String updatedBy);

    TransferResponse approve(Long id, String updatedBy);

    TransferResponse complete(Long id, String updatedBy);

    TransferResponse cancel(Long id, String updatedBy);

    void delete(Long id, String deletedBy);
}