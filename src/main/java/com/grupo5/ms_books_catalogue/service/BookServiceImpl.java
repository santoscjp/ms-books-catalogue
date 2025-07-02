package com.grupo5.ms_books_catalogue.service;

import com.grupo5.ms_books_catalogue.entity.Book;
import com.grupo5.ms_books_catalogue.payload.BookQueryResponse;
import com.grupo5.ms_books_catalogue.payload.BookRequest;
import com.grupo5.ms_books_catalogue.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository repository;
    private final ElasticsearchOperations elasticsearchClient;
    private final String[] titleSearchFields = {"title", "title._2gram", "title._3gram"};
    private final String[] authorSearchFields = {"author", "author._2gram", "author._3gram"};

    @Value("${HOST:http://localhost}:${PORT:${server.port}}")
    private String serverAddress;

    @SneakyThrows
    public BookQueryResponse search(
            String title,
            String author,
            String category,
            String isbn,
            String description
    ) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (title != null && !title.isBlank()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(title, titleSearchFields).type(MultiMatchQueryBuilder.Type.BOOL_PREFIX));
        }
        if (author != null && !author.isBlank()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(author, authorSearchFields).type(MultiMatchQueryBuilder.Type.BOOL_PREFIX));
        }
        if (category != null && !category.isBlank()) {
            boolQuery.must(QueryBuilders.termQuery("category", category));
        }
        if (isbn != null && !isbn.isBlank()) {
            boolQuery.must(QueryBuilders.termQuery("isbn", isbn));
        }
        if (description != null && !description.isBlank()) {
            boolQuery.must(QueryBuilders.matchQuery("description", description));
        }

        if(!boolQuery.hasClauses()){
            boolQuery.must(QueryBuilders.matchAllQuery());
        }

        boolQuery.must(QueryBuilders.termQuery("visible", true));


        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder().withQuery(boolQuery);
        Query query = searchQuery.build();
        SearchHits<Book> result = elasticsearchClient.search(query, Book.class);

        return new BookQueryResponse(result.getSearchHits().stream().map(SearchHit::getContent).toList());
    }


    @Override
    public Book getById(String productId) {
        return repository.findById(productId).orElse(null);
    }

    @Override
    public Book create(BookRequest request){
        if (request != null && StringUtils.hasLength(request.getTitle().trim())
                && StringUtils.hasLength(request.getDescription().trim())
                && StringUtils.hasLength(request.getIsbn().trim())
                && request.getVisible() != null
                && StringUtils.hasLength(request.getAuthor().trim())
                && StringUtils.hasLength(request.getCategory().trim())
                && StringUtils.hasLength(request.getAuthor().trim())
        ) {

            Book book = Book.builder()
                    .isbn(request.getIsbn())
                    .title(request.getTitle())
                    .author(request.getAuthor())
                    .description(request.getDescription())
                    .publicationDate(request.getPublicationDate())
                    .category(request.getCategory())
                    .rating(request.getRating())
                    .visible(request.getVisible() != null ? request.getVisible() : true)
                    .stock(request.getStock() != null ? request.getStock() : 0)
                    .build();

            return repository.save(book);
        } else {
            return null;
        }
    }

    @Override
    public Book update(String id, BookRequest req){
        Book book = repository.findById(id).orElse(null);
        if (book != null) {
            book.setId(id);
            book.setTitle(req.getTitle());
            book.setDescription(req.getDescription());
            book.setIsbn(req.getIsbn());
            book.setAuthor(req.getAuthor());
            book.setCategory(req.getCategory());
            book.setRating(req.getRating());
            book.setVisible(req.getVisible());
            book.setStock(req.getStock());
            book.setPublicationDate(req.getPublicationDate());
            return repository.save(book);
        }
        return null;
    }

    @Override
    public Boolean delete(String id) {
        Book book = repository.findById(id).orElse(null);
        if (book != null) {
            repository.delete(book);
            return true;
        }else{
            return false;
        }
    }

    // --- helpers ---
    private Book mapToEntity(BookRequest r) {
        Book b = new Book();
        b.setIsbn(r.getIsbn());
        b.setTitle(r.getTitle());
        b.setAuthor(r.getAuthor());
        b.setPublicationDate(r.getPublicationDate());
        b.setCategory(r.getCategory());
        b.setRating(r.getRating());
        b.setVisible(r.getVisible());
        b.setStock(r.getStock());
        return b;
    }

    private void applyUpdates(Book b, BookRequest r) {
        b.setIsbn(r.getIsbn());
        b.setTitle(r.getTitle());
        b.setAuthor(r.getAuthor());
        b.setPublicationDate(r.getPublicationDate());
        b.setCategory(r.getCategory());
        b.setRating(r.getRating());
        b.setVisible(r.getVisible());
        b.setStock(r.getStock());
    }
}

