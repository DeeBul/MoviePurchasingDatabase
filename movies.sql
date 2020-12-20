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

Drop Table ratings;
DROP TABLE userWishlist;
DROP TABLE userRating;

DROP TABLE comments;
DROP TABLE users;

ALTER TABLE prices
RENAME COLUMN prices TO price;

ALTER TABLE userRating
RENAME COLUMN userRating TO rating;

ALTER TABLE comments
RENAME COLUMN movieTitle TO movie;

ALTER TABLE ratings
RENAME COLUMN movieTitle TO movieName;

-- Purchase Table
--When a user buys a movie, the userID, movie title, and purchase date is stored in purchases.
INSERT INTO purchases(userID, purchasedMovies, purchaseDate) VALUES
    (1, 'Inception', '2020-11-09'),
    (3, 'Groundhog Day', '2019-12-09'),
    (4, 'Spider-Man: Into the Spider-Verse', '2020-05-29'),
    (6, 'My Life as a Zucchini', '2018-04-16');

DELETE FROM purchases
WHERE purchaseID = 2;

INSERT INTO purchases(userID, purchasedMovies, purchaseDate) VALUES
    (2, 'Groundhog Day', '2019-12-09');

UPDATE purchases
SET purchaseID = 2 WHERE purchaseID = 3;

UPDATE purchases
SET purchaseID = 3 WHERE purchaseID = 4;

UPDATE purchases
SET purchaseID = 4 WHERE purchaseID = 5;


-- User Ratings Table
--user can rate movies
INSERT INTO userRating(userID, movieTitle, userRating) VALUES
    (1, 'Inception', 5),
    (3, 'Groundhog Day', 4.3),
    (4, 'Spider-Man: Into the Spider-Verse', 4.2),
    (6, 'My Life as a Zucchini', 3.9);

UPDATE userRating
SET userRating = 4.7 WHERE userID = 6;

DELETE FROM userRating
WHERE userID = 1;

INSERT INTO userRating(userID, movieTitle, userRating) VALUES
    (1, 'Inception', 4.4);

UPDATE userRating
SET ratingID = 1 WHERE userID = 1;


-- Wishlist Table
    INSERT INTO userWishlist(userID, movieTitle) VALUES
        (1, '3 Idiots'),
        (1, 'The Good  the Bad and the Ugly'),
        (4, 'Monty Python and the Holy Grail'),
        (5, 'Indiana Jones and the Last Crusade'),
        (5, 'Ex Machina');

DELETE FROM userWishlist
WHERE movieTitle = 'Ex Machina';




------------------------------------------------------------------------------------------------------
--Inserting into user means creating a new user. //Later, user will input a name and the name will
--be added as a new user.
INSERT INTO users(username, age, joinDate, country) VALUES --NEED TO MODIFY THIS
    ('BandoWalker', 24, '2020-11-09', 'UNITED STATES'),
    ('SyndyM', 30, '2019-02-29', 'CANADA'),
    ('RenTakanashi', 17, '2017-09-20', 'JAPAN'),
    ('LilySteel', 44, '2017-05-04', 'FRANCE');



--Deleting a user means the user deleted the account.age
DELETE FROM users 
WHERE userID = 2;

--Change user name or country
UPDATE users
SET username = 'LilyTakao' WHERE userID = 4;

UPDATE users
SET country = 'SWEDEN' WHERE userID = 3;


---------------------------------------------------------------------------------------

--Searching for movie by title. This search brings up
--the movie title, genre, age rating, directors, and run time
--if the movie title has similar words to a
--movie in our database                  //DON'T NEED THIS

SELECT movieTitle, genres, age, directors, runtime
FROM movies
WHERE movieTitle LIKE '%Berserk%';

SELECT movieTitle, genres, age, directors, runtime
FROM movies
WHERE movieTitle LIKE '%Under the Shadow%';



----------------------------------------------------------------------------

--inserting into comments table means creating a new comment. //DON'T NEED THIS
INSERT INTO comments(userID, movieTitle, comment, commentDate) VALUES
    (2, 'Minority Report', 'I was not expecting it to be so weird.', '2020-11-10'),
    (2, 'Django Unchained', 'Really good. One of the best movies I have seen.', '2019-03-05'),
    (4, 'Sin City', 'Some funny moments, but overall, the storytelling was bad. Plus the movie is in black and white.', '2019-11-11'),
    (4, 'Back to the Future', 'This is a classic.', '2020-08-16'),
    (1, 'Back to the Future', 'Not all oldies are goldies.', '2020-08-16');



--Deleting a comment means the user deleted one of their comments. //NEED THIS
DELETE FROM comments 
WHERE userID = 2 AND
    commentID = 1;

--updating a comment means the user decided to edit one of their comments.//NEED THIS
UPDATE comments
SET comment = 'It was ok.' WHERE userID = 4 AND commentID = 3; 

-------------------------------------------------------------------------------------

--View comments
--The comments on a specified movie and the commenter will be displayed.//DON'T NEED THIS

SELECT movieTitle, username, comment
FROM comments, users
WHERE movieTitle LIKE '%Back to the Future%' AND
    comments.userID = users.userID;


SELECT movieTitle, username, comment
FROM comments, users
WHERE movieTitle LIKE '%Sin City%' AND
    comments.userID = users.userID;






