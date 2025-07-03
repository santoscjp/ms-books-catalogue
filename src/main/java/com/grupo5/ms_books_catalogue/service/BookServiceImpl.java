package com.grupo5.ms_books_catalogue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // <-- Añadir
import com.fasterxml.jackson.databind.SerializationFeature;  // <-- Añadir
import com.grupo5.ms_books_catalogue.entity.Book;
import com.grupo5.ms_books_catalogue.payload.BookQueryResponse;
import com.grupo5.ms_books_catalogue.payload.BookRequest;
import com.grupo5.ms_books_catalogue.payload.BookResponse;
import com.grupo5.ms_books_catalogue.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository repository;
    private final ElasticsearchOperations elasticsearchClient;
    private final RestHighLevelClient client;  // <--- inyecta el cliente aquí

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
                    .price(request.getPrice())
                    .discount(request.getDiscount())
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
            book.setPrice(req.getPrice());
            book.setDiscount(req.getDiscount());
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

    @Override
    public List<BookRequest> fullTextSearch(String query) {
        SearchRequest searchRequest = new SearchRequest("books");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders
                .multiMatchQuery(query, "title", "author", "description")
                .fuzziness(org.elasticsearch.common.unit.Fuzziness.AUTO)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);

        sourceBuilder.query(multiMatchQuery);
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return Arrays.stream(response.getHits().getHits())
                    .map(hit -> {
                        try {
                            return objectMapper.readValue(hit.getSourceAsString(), BookRequest.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> suggest(String prefix) {
        SearchRequest searchRequest = new SearchRequest("books");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // Usar match_phrase_prefix sobre el campo 'title' tipo 'search_as_you_type'
        MatchPhrasePrefixQueryBuilder matchQuery = QueryBuilders
                .matchPhrasePrefixQuery("title", prefix);

        sourceBuilder.query(matchQuery);
        sourceBuilder.size(5); // limitar resultados
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            List<String> results = new ArrayList<>();

            response.getHits().forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Object titleObj = sourceAsMap.get("title");
                if (titleObj != null) {
                    results.add(titleObj.toString());
                }
            });

            return results;
        } catch (IOException e) {
            throw new RuntimeException("Error executing suggest query", e);
        }
    }


    @Override
    public List<String> correct(String word) {
        SearchRequest searchRequest = new SearchRequest("books");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        TermSuggestionBuilder suggestionBuilder = SuggestBuilders
                .termSuggestion("title")
                .text(word)
                .suggestMode(TermSuggestionBuilder.SuggestMode.ALWAYS);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("spellcheck", suggestionBuilder);

        sourceBuilder.suggest(suggestBuilder);
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> suggestion =
                    response.getSuggest().getSuggestion("spellcheck");

            List<String> results = new ArrayList<>();
            for (var entry : suggestion.getEntries()) {
                for (var option : entry.getOptions()) {
                    results.add(option.getText().string());
                }
            }
            return results;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
