package com.grupo5.ms_books_catalogue.payload;
import com.grupo5.ms_books_catalogue.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookQueryResponse {
    private List<Book> books;
}
