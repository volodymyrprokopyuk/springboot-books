package org.vld.books

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class BookApplication

fun main(args: Array<String>) = SpringApplication.run(BookApplication::class.java, *args)
