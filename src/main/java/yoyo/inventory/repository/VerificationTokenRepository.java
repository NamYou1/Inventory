package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yoyo.inventory.entities.User;
import yoyo.inventory.entities.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByTokenAndType(String token, String type);
    Optional<VerificationToken> findByUserAndType(User user, String type);
    void deleteByUserAndType(User user, String type);
}
