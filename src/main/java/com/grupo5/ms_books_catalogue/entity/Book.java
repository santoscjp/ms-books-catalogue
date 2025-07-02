package com.grupo5.ms_books_catalogue.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "books", createIndex = true)
@Builder
@ToString
public class Book {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String isbn;

    @Field(type = FieldType.Search_As_You_Type)
    private String title;

    @Field(type = FieldType.Search_As_You_Type)
    private String author;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate publicationDate;

    @Field(type = FieldType.Keyword)
    private String category;
    @Field(type = FieldType.Integer)
    private Integer rating;

    @Field(type = FieldType.Boolean)
    private Boolean visible = true;

    @Field(type = FieldType.Integer)
    private Integer stock = 0;
}
