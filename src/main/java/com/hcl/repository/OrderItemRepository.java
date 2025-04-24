package com.hcl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcl.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

}
