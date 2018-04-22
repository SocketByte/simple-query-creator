package pl.socketbyte.querycreator.example

import pl.socketbyte.querycreator.QueryProvider

// Prints:
//---- Replacements ----
//1 -> J.K Rowling
//2 -> 39283
//3 -> 4.98
//4 -> Harry Potter
//---- Query ----
//INSERT INTO books VALUES (?, ?, ?, ?)
//
//
//---- Replacements ----
//1 -> J.K Rowling
//2 -> 39283
//3 -> 4.98
//4 -> Harry Potter
//5 -> J.K Rowling
//---- Query ----
//UPDATE books SET (author = ?, boughtCopies = ?, rating = ?, title = ?) WHERE author = ?
//
//
//---- Replacements ----
//---- Query ----
//CREATE TABLE IF NOT EXIST books (author TEXT, boughtCopies INT, rating DOUBLE, title TEXT)
//
//
//---- Replacements ----
//1 -> Harry Potter
//---- Query ----
//SELECT * FROM books WHERE title = ?
fun main(args: Array<String>) {
    val book = Book("Harry Potter", "J.K Rowling", 39283, 4.98)
    val queryProvider = QueryProvider<Book>(book, Book::class.java)

    queryProvider
            .insert()
            .print()

    queryProvider
            .update("author" to "J.K Rowling")
            .print()

    queryProvider
            .table()
            .print()

    queryProvider
            .builder()
            .select()
            .from()
            .where("title" to "Harry Potter")
            .print()
}

fun String.print() {
    println(this)
}