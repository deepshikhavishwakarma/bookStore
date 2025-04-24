package com.hcl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcl.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	List<CartItem> findByUser_id(Long userId);
	Optional<CartItem> findByUser_idAndBook_BookId(Long userId, Long bookId); // Corrected method name
    void deleteByUser_id(Long userId);
    void deleteByBook_BookId(Long bookId);
   
}