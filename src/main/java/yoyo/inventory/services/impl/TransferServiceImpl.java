package yoyo.inventory.services.impl;

import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.InvoiceService;
import yoyo.inventory.dto.request.TransferItemRequest;
import yoyo.inventory.dto.request.TransferRequest;
import yoyo.inventory.dto.response.TransferResponse;
import yoyo.inventory.entities.*;
import yoyo.inventory.entities.status.TransferStatus;
import yoyo.inventory.mappers.TransferMapper;
import yoyo.inventory.repository.StockRepository;
import yoyo.inventory.repository.TransferRepository;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.StockService;
import yoyo.inventory.services.StoreService;
import yoyo.inventory.services.TransferService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
//    private final StockRepository stockRepository;
    private final TransferMapper transferMapper;
    private  final StockService stockService ;
    private final ProductService productService;
    private  final StoreService storeService ;
    private  final InvoiceService invoiceService ;
    // CREATE
    @Override
    public TransferResponse create(TransferRequest request) {

        Transfer transfer = transferMapper.toEntity(request);
        transfer.setStatus(TransferStatus.PENDING);
        transfer.setTransferNo(invoiceService.generate("TRF"));
        transfer.setCreatedAt(LocalDateTime.now());
        BigDecimal total = BigDecimal.ZERO ;
        BigDecimal grandTotal = BigDecimal.ZERO;
        Stores fromStoreId = storeService.findById(request.getFromStoreId());
        Stores toStoreId = storeService.findById(request.getToStoreId());
        List<TransferItem> items = new ArrayList<>();
        for (TransferItemRequest itemRequest : request.getItems()){
            Product product = productService.findById(itemRequest.getProductId());
            TransferItem item = transferMapper.toItemRequest(itemRequest);
            item.setProductId(product.getId().intValue());
            item.setTransfer(transfer);
            item.setSubtotal(item.getQuantity().multiply(item.getUnitPrice()));
            items.add(item);
            total = total.add(item.getSubtotal());
            stockService.transfer(fromStoreId,toStoreId, product, item.getQuantity());
        }
        grandTotal = total.subtract(request.getShipping());
        transfer.setTotal(total);
        transfer.setGrandTotal(grandTotal);
        transfer.setItems(items);
        return transferMapper.toResponse(transferRepository.save(transfer));
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
        return  null;
//        return transferRepository.findAll(
//                TransferSpecification.filter(filter),
//                pageable
//        ).map(transferMapper::toResponse);
    }

    // UPDATE
    @Override
    public TransferResponse update(Long id, TransferRequest request, String updatedBy) {

        Transfer transfer = find(id);

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new RuntimeException("Only DRAFT can be updated");
        }

        transferMapper.updateEntity(request, transfer);
        transfer.setUpdatedBy(updatedBy);
        transfer.setUpdatedAt(LocalDateTime.now());

        return transferMapper.toResponse(transferRepository.save(transfer));
    }

    // APPROVE
    @Override
    public TransferResponse approve(Long id, String updatedBy) {

        Transfer transfer = find(id);

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new RuntimeException("Only DRAFT can be approved");
        }

        transfer.setStatus(TransferStatus.APPROVED);
        transfer.setUpdatedBy(updatedBy);

        return transferMapper.toResponse(transferRepository.save(transfer));
    }

    @Override
    public TransferResponse complete(Long id, String updatedBy) {
        return null;
    }

    // COMPLETE (STOCK LOGIC)
//    @Override
//    public TransferResponse complete(Long id, String updatedBy) {
//
//        Transfer transfer = find(id);
//
//        if (transfer.getStatus() != TransferStatus.APPROVED) {
//            throw new RuntimeException("Only APPROVED can be completed");
//        }
//
//        for (TransferItem item : transfer.getItems()) {
//
//            Stock from = stockRepository.findById(item.getFromStockId())
//                    .orElseThrow(() -> new RuntimeException("From stock not found"));
//
//            Stock to = stockRepository.findById(item.getToStockId())
//                    .orElseThrow(() -> new RuntimeException("To stock not found"));
//
//            if (from.getQuantity() < item.getQuantity()) {
//                throw new RuntimeException("Insufficient stock");
//            }
//
//            from.setQuantity(from.getQuantity() - item.getQuantity());
//            to.setQuantity(to.getQuantity() + item.getQuantity());
//
//            stockRepository.save(from);
//            stockRepository.save(to);
//        }
//
//        transfer.setStatus(TransferStatus.COMPLETED);
//        transfer.setCompletedAt(LocalDateTime.now());
//        transfer.setUpdatedBy(updatedBy);
//
//        return transferMapper.toResponse(transferRepository.save(transfer));
//    }

    // CANCEL
    @Override
    public TransferResponse cancel(Long id, String updatedBy) {

        Transfer transfer = find(id);

        if (transfer.getStatus() == TransferStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed transfer");
        }

        transfer.setStatus(TransferStatus.CANCELLED);
        transfer.setUpdatedBy(updatedBy);

        return transferMapper.toResponse(transferRepository.save(transfer));
    }

    // DELETE (SOFT)
    @Override
    public void delete(Long id, String deletedBy) {

        Transfer transfer = find(id);
        transfer.setIsDeleted(true);
        transfer.setDeletedBy(deletedBy);
        transfer.setDeletedAt(LocalDateTime.now());

        transferRepository.save(transfer);
    }

    private Transfer find(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + id));
    }
}