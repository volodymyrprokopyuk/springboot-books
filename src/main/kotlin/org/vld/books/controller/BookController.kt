package org.vld.books.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import org.vld.books.domain.Book
import org.vld.books.domain.validate
import org.vld.books.service.BookService

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun findAllBooks(): ResponseEntity<List<Book>> =
            ResponseEntity(bookService.findAllBooks(), HttpStatus.OK)

    @GetMapping("/{id}")
    fun findBookById(@PathVariable("id") id: String): ResponseEntity<Book> {
        val book: Book? = bookService.findBookById(id)
        return if (book != null) ResponseEntity(book, HttpStatus.OK)
        else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping
    fun createBook(@RequestBody book: Book, uriBuilder: UriComponentsBuilder): ResponseEntity<Unit> {
        book.validate()
        val newBook = bookService.createBook(book)
        val headers = HttpHeaders()
        headers.location = uriBuilder.path("/books/{id}").buildAndExpand(newBook.id).toUri()
        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateBook(@PathVariable("id") id: String, @RequestBody book: Book): ResponseEntity<Book> {
        book.validate()
        val existingBook: Book? = bookService.findBookById(id)
        return if (existingBook != null) ResponseEntity(bookService.updateBook(book.copy(id = id)), HttpStatus.OK)
        else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable("id") id: String): ResponseEntity<Unit> =
            ResponseEntity(bookService.deleteBookById(id), HttpStatus.NO_CONTENT)
}

