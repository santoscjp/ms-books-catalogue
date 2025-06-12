CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    publication_date DATE,
    category VARCHAR(100),
    rating INT,
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    stock INT NOT NULL DEFAULT 0
);
