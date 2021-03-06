DROP TABLE IF EXISTS book;
CREATE TABLE book (
    created_date timestamp NOT NULL,
    updated_date timestamp NOT NULL,
    book_id integer NOT NULL,
    isbn bigint NOT NULL,
    book_title varchar(50) NOT NULL,
    book_author varchar(50) NOT NULL,
    PRIMARY KEY ("book_id")
);

ALTER TABLE public.book ALTER COLUMN book_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.book_book_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

INSERT INTO book(isbn, book_title, book_author, created_date, updated_date)
VALUES(9780062315007, 'The Alchemist', 'Paulo Coelho', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );

INSERT INTO book(isbn, book_title, book_author, created_date, updated_date)
VALUES(9780439708180, 'Harry Potter and the Sorcerer''s Stone (#1)', 'J.K. Rowling', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );

INSERT INTO book(isbn, book_title, book_author, created_date, updated_date)
VALUES(9780439064873, 'Harry Potter and the Chamber of Secrets (#2)', 'J.K. Rowling', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );

