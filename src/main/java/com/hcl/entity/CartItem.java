package com.hcl.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
