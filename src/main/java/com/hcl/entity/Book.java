package com.hcl.entity;



import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(name = "book_name")
    private String bookName;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Double price;

    // Default Constructor
    public Book() {
    }

    // Constructor with fields
    public Book(String bookName, String authorName, Integer quantity, Double price) {
        this.bookName = bookName;
        this.authorName = authorName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}