package yoyo.inventory.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.InvoiceService;
import yoyo.inventory.common.PageUtil;
import yoyo.inventory.dto.request.TransferItemRequest;
import yoyo.inventory.dto.request.TransferRequest;
import yoyo.inventory.dto.response.TransferResponse;
import yoyo.inventory.entities.*;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.entities.status.TransferStatus;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.enums.TransactionType;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.execption.SameStoreException;
import yoyo.inventory.mappers.TransferMapper;
import yoyo.inventory.repository.TransferRepository;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.StockService;
import yoyo.inventory.services.StoreService;
import yoyo.inventory.services.TransactionService;
import yoyo.inventory.services.TransferService;
import yoyo.inventory.specification.transfer.TransferFilter;
import yoyo.inventory.specification.transfer.TransferSpec;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private  final StockService stockService ;
    private final ProductService productService;
    private  final StoreService storeService ;
    private final TransactionService transactionService;
    private  final InvoiceService invoiceService ;
    private final ObjectMapper objectMapper;

    // CREATE
    @Override
    public TransferResponse create(TransferRequest request) {

        Transfer transfer = transferMapper.toEntity(request);
        transfer.setStatus(TransferStatus.PENDING);
        transfer.setTransferNo(invoiceService.generate("TRF"));
        transfer.setCreatedAt(LocalDateTime.now());

        Stores fromStoreId = storeService.findById(request.getFromStoreId());
        Stores toStoreId = storeService.findById(request.getToStoreId());
        if (Objects.equals(fromStoreId.getId(), toStoreId.getId())) {
            throw new ResourceNotFoundException("From and To store cannot be the same") ;
        }
//        if (Object.E)
        BigDecimal total = BigDecimal.ZERO ;
        List<TransferItem> items = new ArrayList<>();

        for (TransferItemRequest itemRequest : request.getItems()) {

            Product product = productService.findById(itemRequest.getProductId());
            TransferItem item = transferMapper.toItemRequest(itemRequest);
            item.setTransfer(transfer);
            item.setCostPrice(product.getCostPrice());
            item.setProduct(product);
//            item.setProduct(product.getId());
            item.setSubtotal(item.getQuantity().multiply(product.getCostPrice()));
            total = total.add(item.getSubtotal());
            items.add(item);
        }
        transfer.setItems(items);
        transfer.setTotal(total);
        transfer.setGrandTotal(total);
        Transfer saved = transferRepository.save(transfer);
        return transferMapper.toResponse(transferRepository.save(saved));
    }

    // GET
    @Override
    @Transactional
    public TransferResponse getById(Long id) {
        return transferMapper.toResponse(find(id));
    }
    // LIST
    @Override
    @Transactional
    public Page<TransferResponse> getAll(Map<String, String> params) {
        TransferFilter filter = objectMapper.convertValue(params, TransferFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Transfer> spec = TransferSpec.filterBy(filter);
        return  transferRepository.findAll(spec, pageable).map(transferMapper::toResponse);
    }

    // UPDATE
    @Override
    public TransferResponse update(Long id, TransferRequest request, String updatedBy) {
        Transfer transfer = find(id);
        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new ResourceNotFoundException("Only DRAFT can be updated");
        }

        transferMapper.updateEntity(request, transfer);
        transfer.setUpdatedBy(updatedBy);
        transfer.setUpdatedAt(LocalDateTime.now());

        return transferMapper.toResponse(transferRepository.save(transfer));
    }

    // APPROVE
    @Override
//    @CacheEvict(cacheNames = {"transfer-page", "transfer-response", "txn-summary"}, allEntries = true)
    public TransferResponse approve(Long id, String updatedBy) {

        Transfer transfer = find(id);
        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new ResourceNotFoundException("Only PENDING transfer can be approved");
        }

        transfer.setStatus(TransferStatus.APPROVED);
        transfer.setUpdatedBy(updatedBy);
        transfer.setUpdatedAt(LocalDateTime.now());

        return transferMapper.toResponse(
                transferRepository.save(transfer)
        );
    }

    @Override
    public TransferResponse complete(Long id, String updatedBy) {
        Transfer transfer = find(id);
        if (transfer.getStatus() != TransferStatus.APPROVED) {
            throw new ResourceNotFoundException("Only APPROVED transfer can complete");
        }
        for (TransferItem item : transfer.getItems()) {
            stockService.transferStock(
                    item.getProduct().getId(),
                    transfer.getFromStoreId().getId(),
                    transfer.getToStoreId().getId(),
                    item.getQuantity()
            );

            Long unitId = resolveUnitId(item);

            transactionService.logStockMovement(
                    TransactionType.TRANSFER_OUT,
                    transfer.getId(),
                    transfer.getTransferNo(),
                    item.getProduct().getId(),
                    transfer.getFromStoreId().getId(),
                    unitId,
                    item.getQuantity(),
                    item.getUnitQuantity(),
                    item.getCostPrice(),
                    SaleStatus.COMPLETED,
                    updatedBy
            );

            transactionService.logStockMovement(
                    TransactionType.TRANSFER_IN,
                    transfer.getId(),
                    transfer.getTransferNo(),
                    item.getProduct().getId(),
                    transfer.getToStoreId().getId(),
                    unitId,
                    item.getQuantity(),
                    item.getUnitQuantity(),
                    item.getCostPrice(),
                    SaleStatus.COMPLETED,
                    updatedBy
            );
        }
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setUpdatedBy(updatedBy);

        transfer.setUpdatedAt(LocalDateTime.now());

        return transferMapper.toResponse(transferRepository.save(transfer));
    }

    // CANCEL
    @Override
//    @CacheEvict(cacheNames = {"transfer-page", "transfer-response", "txn-summary"}, allEntries = true)
    public TransferResponse cancel(Long id, String updatedBy) {

        Transfer transfer = find(id);

        if (transfer.getStatus() == TransferStatus.CANCELLED) {
            throw new ResourceNotFoundException("Transfer already cancelled");
        }

        if (transfer.getStatus() == TransferStatus.COMPLETED) {
            for (TransferItem item : transfer.getItems()) {
                stockService.reverseStock(
                        item.getProduct().getId(),
                        transfer.getFromStoreId().getId(),
                        transfer.getToStoreId().getId(),
                        item.getQuantity()
                );

                transactionService.logStockMovement(
                        TransactionType.TRANSFER_OUT,
                        transfer.getId(),
                        transfer.getTransferNo(),
                        item.getProduct().getId(),
                        transfer.getToStoreId().getId(),
                        resolveUnitId(item),
                        item.getQuantity(),
                        item.getUnitQuantity(),
                        item.getCostPrice(),
                        SaleStatus.CANCELLED,
                        updatedBy
                );

                transactionService.logStockMovement(
                        TransactionType.TRANSFER_IN,
                        transfer.getId(),
                        transfer.getTransferNo(),
                        item.getProduct().getId(),
                        transfer.getFromStoreId().getId(),
                        resolveUnitId(item),
                        item.getQuantity(),
                        item.getUnitQuantity(),
                        item.getCostPrice(),
                        SaleStatus.CANCELLED,
                        updatedBy
                );
            }
        }

        transfer.setStatus(TransferStatus.CANCELLED);
        transfer.setUpdatedBy(updatedBy);
        transfer.setUpdatedAt(LocalDateTime.now());

        return transferMapper.toResponse(transferRepository.save(transfer));
    }

    // DELETE (SOFT)
    @Override
//    @CacheEvict(cacheNames = {"transfer-page", "transfer-response", "txn-summary"}, allEntries = true)
    public void delete(Long id, String deletedBy) {

        Transfer transfer = find(id);
        if (transfer.getDeletedAt() != null || transfer.getIsActive() == Status.INACTIVE) {
            throw new ResourceNotFoundException("Transfer already deleted");
        }

        if (transfer.getStatus() == TransferStatus.COMPLETED) {
            throw new ResourceNotFoundException("Cannot delete completed transfer");
        }
        transfer.setIsActive(Status.INACTIVE);
        transfer.setDeletedBy(deletedBy);
        transfer.setDeletedAt(LocalDateTime.now());

        transferRepository.save(transfer);
    }

    private Transfer find(Long id) {
        return transferRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transfer  " , id));
    }

    private Long resolveUnitId(TransferItem item) {
        if (item.getUnit() != null && item.getUnit().getId() != null) {
            return item.getUnit().getId();
        }
        if (item.getProduct() != null && item.getProduct().getTblUnit() != null && item.getProduct().getTblUnit().getId() != null) {
            return item.getProduct().getTblUnit().getId();
        }
        throw new IllegalStateException("Unit is required to log transfer transaction for product " + item.getProduct().getId());
    }
}
