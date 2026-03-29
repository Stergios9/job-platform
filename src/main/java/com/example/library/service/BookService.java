package com.example.library.service;

import com.example.library.entity.Book;

import java.util.List;

public interface BookService {

    List<Book> getAllBooks();

    Book getBookById(int id);


    boolean saveBook(Book book);
    boolean updateBook(Book book);
    boolean deleteBook(int id);



}
