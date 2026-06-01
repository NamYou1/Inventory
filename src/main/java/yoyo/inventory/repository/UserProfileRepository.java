package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yoyo.inventory.entities.User;
import yoyo.inventory.entities.UserProfile;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
    Optional<UserProfile> findByUser(User user);
}
