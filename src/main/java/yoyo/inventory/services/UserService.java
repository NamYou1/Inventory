package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.UserRequest;
import yoyo.inventory.dto.response.UserResponse;

import yoyo.inventory.entities.User;

import java.util.Map;

public interface UserService {
    Page<UserResponse> getAll(Map<String, String> params);
    UserResponse getById(Long id);
    User findById(Long id);
    UserResponse create(UserRequest request);
    UserResponse update(Long id, UserRequest request);
    void delete(Long id);
}
