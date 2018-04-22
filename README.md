# simple-query-creator
Simple query creator and executor based on [Wrapp](https://github.com/SocketByte/Wrapp).

### Installation
Just use my Maven repository.
```xml
<repositories>
    <repository>
        <id>socketbyte-repo</id>
        <url>http://repo.socketbyte.pl/repository/nexus-releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>pl.socketbyte</groupId>
        <artifactId>simple-query-creator</artifactId>
        <version>1.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```
It also contains `Wrapp` framework.

### Usage
Create an instance of your `data class` (or normal class if you wish)
like so:
```kotlin
data class Book(val title: String,
                val author: String,
                var boughtCopies: Int,
                var rating: Double)

```
```kotlin
val book = Book("Harry Potter", "J.K Rowling", 39283, 4.98)
```
Then, create `QueryProvider` instance:
```kotlin
val queryProvider = QueryProvider<Book>(book, Book::class.java)
```
You can also use extension:
```kotlin
val queryProvider = book.createQueryProvider(Book::class.java)
```
That's it! You can now use your query provider!
#### Disclaimer
You can use `QueryProvider` without giving the `connection` parameter.
But! You cannot use any of `query()` or `execute()` features!
To provide connection use this instead:

```kotlin
val queryProvider = book.createQueryProvider(Book::class.java, connection, isConnectionPool)
```
or
```kotlin
val queryProvider = QueryProvider<Book>(book, Book::class.java, connection, isConnectionPool)
```

### Examples
##### Inserting
```kotlin
queryProvider
    .insert()
    .execute()
```
Creates an insert query and executes it with all replacements. :)

Result:
```
INSERT INTO books VALUES (?, ?, ?, ?)
```

##### Updating
```kotlin
queryProvider
    .update("author" to "J.K Rowling")
    .execute()
```
Updates everything in table where author is equal to **J.K Rowling**.

##### Table creation
```kotlin
queryProvider
    .table()
    .execute()
```
Creates a table with `nullable` data.

Result:
```
CREATE TABLE IF NOT EXIST books (author TEXT, boughtCopies INT, rating DOUBLE, title TEXT)
```

##### Selecting
```kotlin
queryProvider
    .builder()
    .select()
    .from()
    .where("title" to "Harry Potter")
    .query() // <- it will return ResultSet
```
You can also use default builder class to make more advanced queries.
Query above selects everything where title is equal to **Harry Potter**

You can modify it to take for example only **rating** parameter:
```kotlin
queryProvider
    .builder()
    .select("rating")
    .from()
    .where("title" to "Harry Potter")
    .query() // <- it will return ResultSet
```
You can of course make few conditions to `where`!
```kotlin
queryProvider
    .builder()
    .select()
    .from()
    .where("title" to "Harry Potter", "boughtCopies" to 39283)
    .query() // <- it will return ResultSet
```
That will do the same as above, but will check also for `boughtCopies`!

Result:
```
SELECT * FROM books WHERE title = ? AND boughtCopies = ?
```

### License
Project is based on MIT License.