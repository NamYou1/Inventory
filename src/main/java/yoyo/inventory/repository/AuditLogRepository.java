package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yoyo.inventory.entities.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
