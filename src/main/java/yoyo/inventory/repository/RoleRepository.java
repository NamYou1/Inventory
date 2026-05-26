package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yoyo.inventory.entities.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String code);
}
