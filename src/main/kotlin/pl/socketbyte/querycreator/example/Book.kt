package pl.socketbyte.querycreator.example

data class Book(val title: String,
                val author: String,
                var boughtCopies: Int,
                var rating: Double)