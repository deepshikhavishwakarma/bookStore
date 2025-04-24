package com.hcl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcl.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Optional: add custom query methods if needed
	
	List<Order> findByUser_id(Long id);
}
