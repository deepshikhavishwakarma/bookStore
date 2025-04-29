package com.hcl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcl.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	List<CartItem> findByUserId(Long userId);
	Optional<CartItem> findByUserIdAndBookBookId(Long userId, Long bookId); // Corrected method name
    void deleteByUserId(Long userId);
    void deleteByBookBookId(Long bookId);
   
}