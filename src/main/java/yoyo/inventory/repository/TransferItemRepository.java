package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yoyo.inventory.entities.TransferItem;

import java.util.List;

@Repository
public interface TransferItemRepository extends JpaRepository<TransferItem, Long> {

    List<TransferItem> findByTransferId(Long transferId);

    @Modifying
    @Query("DELETE FROM TransferItem ti WHERE ti.transfer.id = :transferId")
    void deleteByTransferId(@Param("transferId") Long transferId);
}