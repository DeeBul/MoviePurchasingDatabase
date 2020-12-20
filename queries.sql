-- Script of Queries
CREATE TABLE movies (
    movieID int not null,
    movieTitle varchar(55) not null,
    year datetime not null,
    age char(25),
    directors varchar(155),
    genres char(55),
    country char(55),
    language char(75),
    runtime int
);

CREATE TABLE users (
    userID integer PRIMARY KEY AUTOINCREMENT,
    username varchar(55) not null,
    age int not null,
    joinDate datetime not null,
    country char(35) not null,
    balance int not null
);

CREATE TABLE purchases (
    purchaseID integer PRIMARY KEY AUTOINCREMENT,
    userID int not null,
    purchasedMovies varchar(255),
    purchaseDate datetime not null
);

CREATE TABLE comments (
    commentID integer PRIMARY KEY AUTOINCREMENT,
    userID int not null,
    movieTitle varchar(55) not null,
    comment varchar(255) not null,
    commentDate datetime not null
);

CREATE TABLE userRating (
    ratingID integer PRIMARY KEY AUTOINCREMENT,
    userID int not null, 
    movieTitle varchar(55),
    userRating decimal(1,1)
);

CREATE TABLE ratings (
    movieTitle varchar(55) not null,
    RottenTomatoes char(10),
    IMDB decimal(2,1),
    AvgUserRating decimal(2,1)
);

CREATE TABLE userWishlist (
    wishlistID integer PRIMARY KEY AUTOINCREMENT,
    userID int,
    movieTitle varchar(55)
);

CREATE TABLE prices (
    movieID int NOT NULL,
    prices int NOT NULL
);

-- Drop Table ratings;
-- DROP TABLE userWishlist;
-- DROP TABLE userRating;

-- DROP TABLE comments;
-- DROP TABLE users;

-- ALTER TABLE prices
-- RENAME COLUMN prices TO price;

-- ALTER TABLE userRating
-- RENAME COLUMN userRating TO rating;

ALTER TABLE comments
RENAME COLUMN movieTitle TO movie;

-- ALTER TABLE ratings
-- RENAME COLUMN movieTitle TO movieName;


-------------------------------------------------------------------------------------


--search for movie by title
SELECT movieTitle, genres, age, directors, runtime, price
FROM movies, prices
WHERE movieTitle LIKE '%Berserk%' AND
    prices.movieID = movies.movieID;

SELECT movieTitle, genres, age, directors, runtime, price
FROM movies, prices
WHERE movieTitle LIKE '%Under the Shadow%' AND
    prices.movieID = movies.movieID;

--find the minimum, maximum, and average prices of the movies in our database
SELECT MIN(price) AS minPrice, MAX(price) as maxPrice, AVG(price) as avgPrice
FROM prices;

--search for movies by price
SELECT movieTitle, genres, age, directors, runtime, price
FROM movies, prices
WHERE prices.price <=10 AND
    prices.movieID = movies.movieID;

--search for movies by runtime. Also can search movies by genre.
SELECT movieTitle, genres, age, directors, runtime, price
FROM movies, prices
WHERE runtime BETWEEN 150 AND 200
    AND movies.genres LIKE '%Horror%' AND
    prices.movieID = movies.movieID;

--search for movies directed by a specific person
SELECT movieTitle, genres, age, directors, runtime, price
FROM movies, prices
WHERE directors LIKE '%Quentin Tarantino%'
AND age = '18+'
AND year BETWEEN 2000 AND 2015 AND
prices.movieID = movies.movieID;

--find average user rating for a movie
SELECT avg(userRating)
FROM userRating
WHERE movieTitle = 'Ex Machina';



--search comments by movie
SELECT movie, username, comment
FROM comments, users
WHERE movie LIKE '%Back to the Future%' AND
    comments.userID = users.userID;

SELECT movie, username, comment
FROM comments, users
WHERE movie LIKE '%Sin City%' AND
    comments.userID = users.userID;


--search for comments made by a specific user
SELECT username, movie, comment
FROM comments
INNER JOIN users ON comments.userID=users.userID
WHERE username = 'Laura';


--insert movie into the user's wish list if the movie is not already in the user's wish list.
DELETE FROM userWishlist
WHERE userID = 41 AND
    movieTitle = 'Inception';

INSERT INTO userWishlist(userID, movieTitle)
SELECT 41, 'Inception'
WHERE NOT EXISTS (SELECT movieTitle FROM userWishlist WHERE userID = 41 AND movieTitle = 'Inception');



-----------------------------------------------------------------------------------------------------------
--view user wish list
SELECT movieTitle
FROM userWishlist
WHERE userID = 10;

--User can insert a comment if they have bought the movie they want to comment on.
DELETE FROM comments
WHERE userID = 23 AND
    movie = 'Peyote';


INSERT INTO comments(userID, movie, comment, commentDate)
SELECT 23, 'Peyote', 'I liked it.', '2020-09-22'
WHERE EXISTS (SELECT purchasedMovies FROM purchases WHERE userID = 23 AND purchasedMovies = 'Peyote');

--User can edit comments they made.
UPDATE comments
SET comment = 'It was ok.' WHERE userID = 23 AND comment = 'I liked it.';


---user can purchase movie only if the user has enough money in their balance to pay for the movie. *
--The movie drive is $18
--user with ID 4 has $96 and can purchase the movie.
--user with ID 8 has $9 and cannot purchase the movie.

-- UPDATE users
-- SET balance = 96
-- WHERE users.userID = 4


DELETE FROM purchases
WHERE userID = 4 AND
    purchasedMovies = 'Drive';


INSERT INTO purchases(userID, purchasedMovies, purchaseDate)
SELECT users.userID, movies.movieTitle, '2020-06-19'
FROM users, movies, prices
WHERE users.userID = 4 AND
    movies.movieID = prices.movieID AND
    users.balance >= prices.price AND
    movies.movieTitle = 'Drive';


UPDATE users
SET balance = (users.balance - 18)
WHERE users.userID = 4

-- UPDATE users
-- SET balance = 96
-- WHERE users.userID = 4



INSERT INTO purchases(userID, purchasedMovies, purchaseDate)
SELECT users.userID, movies.movieTitle, '2020-06-19'
FROM users, movies, prices
WHERE users.userID = 8 AND
    movies.movieID = prices.movieID AND
    users.balance >= prices.price AND
    movies.movieTitle = 'Drive';



----user can delete account
DELETE FROM users
WHERE username = 'LilyTakao';

--Create a new user. A user must have a unique name.
INSERT INTO users(username, age, joinDate, country, balance)
SELECT 'McrilleyM', 22, '2020-03-05', 'UNITED STATES', 0
WHERE NOT EXISTS (SELECT username FROM users WHERE username = 'McrilleyM');



--Change user name or country
UPDATE users
SET username = 'LilyTakao' WHERE username = 'McrilleyM';

UPDATE users
SET country = 'SWEDEN' WHERE username = 'LilyTakao';



--User can create a rating. 
INSERT INTO userRating(userID, movieTitle, userRating) VALUES
(3, 'The Matrix', 9);

--After a new rating is created, the average user rating must be updated.
SELECT AVG(userRating.userRating)
FROM userRating, ratings
WHERE userRating.movieTitle = 'The Matrix'

UPDATE ratings
SET AvgUserRating = 8
WHERE movieTitle = 'The Matrix';


UPDATE userRating
SET ratingID = 16748 WHERE userID = 3;

-- DELETE FROM userRating
-- WHERE ratingID = 16748;