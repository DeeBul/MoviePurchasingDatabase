// STEP: Import required packages
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Calendar;
import java.util.Date;


public class Project {
    private Connection c = null;
    private String dbName;
    private boolean isConnected = false;

    private void openConnection(String _dbName) {
        dbName = _dbName;

        if (false == isConnected) {
            System.out.println("++++++++++++++++++++++++++++++++++");
            System.out.println("Open database: " + _dbName);

            try {
                String connStr = new String("jdbc:sqlite:");
                connStr = connStr + _dbName;

                // STEP: Register JDBC driver
                Class.forName("org.sqlite.JDBC");

                // STEP: Open a connection
                c = DriverManager.getConnection(connStr);

                // STEP: Diable auto transactions
                c.setAutoCommit(false);

                isConnected = true;
                System.out.println("success");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }

            System.out.println("++++++++++++++++++++++++++++++++++");
        }
    }

    private void closeConnection() {
        if (true == isConnected) {
            System.out.println("++++++++++++++++++++++++++++++++++");
            System.out.println("Close database: " + dbName);

            try {
                // STEP: Close connection
                c.close();

                isConnected = false;
                dbName = "";
                System.out.println("success");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }

            System.out.println("++++++++++++++++++++++++++++++++++");
        }
    }

    private static String getInput() { //function for taking multiple string inputs 
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    private void create_User() {
        Scanner input = new Scanner(System.in);
        String username;
        String country;
        int ageYear;
        int userID = 59;
        
        // getting the current date in real time and converting it into a string
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // capitol M is for the month
        String strDate = dateFormat.format(date); //date is in string

        // for calculating the age from the current year and given year
        Calendar now = Calendar.getInstance();
        int currYear = now.get(Calendar.YEAR);

        //username
        System.out.print("Please enter a username: ");
        username = input.next();

        // age which takes in their year and minuses current year with year they were born
        System.out.print("Please enter year you were born: ");
        ageYear = input.nextInt();
        int age = currYear - ageYear;

        //country
        System.out.print("Enter the country you are from: ");
        country = getInput();
       
        System.out.println();
       
        //print everything out 
        System.out.println("Is the information below correct (Y or N): ");
        System.out.println("Username: " + username);
        System.out.println("Age: " + age);
        System.out.println("Country: " + country.toUpperCase());
        char confirm = input.next().charAt(0);
       
        if(confirm == 'y' || confirm == 'Y') {
            System.out.println("Inserting into database");
            System.out.println();

            try {
                String sql = "INSERT INTO users VALUES(?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = c.prepareStatement(sql);

                // stmt.setInt(1, userID++);
                stmt.setString(2, username);
                stmt.setInt(3, age);
                stmt.setString(4, strDate);
                stmt.setString(5, country.toUpperCase());
                stmt.setInt(6, 0);
                stmt.addBatch();
                stmt.executeBatch();

                c.commit();
                stmt.close();

            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }

            System.out.println("Keep note of your userID for login!");
            try {
                System.out.printf("%-20s %-20s %-20s %-20s %-20s %-20s\n", "userID", "username", "age", "joinDate", "country", "balance");
                String sql = "SELECT *" + 
                                " FROM users" +
                                " WHERE userID = (SELECT MAX(userID) FROM users)";
                
                Statement stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                c.commit(); 
                
                while(rs.next()) {
                    int getID = rs.getInt(1);
                    String getUsername = rs.getString(2);
                    int getAge = rs.getInt(3);
                    String getDate = rs.getString(4);
                    String getCountry = rs.getString(5);
                    int getBalance = rs.getInt(6);
                    System.out.printf("%-20s %-20s %-20s %-20s %-20s %-20s\n", getID, getUsername, getAge, getDate, getCountry, getBalance);
                }
                System.out.println();
                rs.close();
                stmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                try {
                    c.rollback();
                } catch (Exception e1) {
                    System.err.println(e1.getClass().getName() + ": " + e1.getMessage());
                }
            }
        }
        else if(confirm == 'n' || confirm == 'N') {
            System.out.println("Something needs to get changed");
        }
    }

    private void movie_Search(String searchChoice){
        Scanner input = new Scanner(System.in);
    
        if (searchChoice.equals("1")) { //search by title
            System.out.print("\nEnter title: "); 
            String title = input.nextLine();  // Read user input
            
            try {
            
                String sql = "SELECT movieTitle, genres, age, directors, runtime, price " +
                             "FROM movies, prices " +
                             "WHERE movieTitle LIKE " + "'%" + title + "%' " + " AND " +
                                 "prices.movieID = movies.movieID";
    
                PreparedStatement stmt = c.prepareStatement(sql);
    
                ResultSet rs = stmt.executeQuery();
    
                System.out.printf("%-60s %-60s\n", "movieTitle:", "price:");
                while (rs.next()) {
                    String a = rs.getString("movieTitle");
                    int f = rs.getInt("price");
                    System.out.printf("%-60s %-60s\n", a, "$" + f);
                }  

            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
        else if (searchChoice.equals("2")) { //search by genre
            System.out.print("\nEnter genre: "); 
            String genre = input.nextLine();  // Read user input  

            try {     
                String sql = "SELECT movies.movieTitle, genres, age, directors, runtime, price " +
                             "FROM movies, prices, ratings " +
                             "WHERE genres LIKE " + "'%" + genre + "%' " + " AND " +
                                 "prices.movieID = movies.movieID AND " +
                                 "movies.movieTitle = ratings.movieTitle AND " +
                                 "ratings.AvgUserRating >= 9"; //decided to narrow searches to movies that 
                                                               //were highly rated on our database.   
                PreparedStatement stmt = c.prepareStatement(sql);
    
                ResultSet rs = stmt.executeQuery();
    
                System.out.printf("%-60s %-60s\n", "movieTitle:", "price:");
                while (rs.next()) {
                    String a = rs.getString("movieTitle");
                    int f = rs.getInt("price");
                    System.out.printf("%-60s %-60s\n", a, "$" + f);

                }
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
        else if (searchChoice.equals("3")) { //search by director
            System.out.print("\nEnter Director: "); 
            String director = input.nextLine();  // Read user input 

            try {

                String sql = "SELECT movieTitle, genres, age, directors, runtime, price " +
                             "FROM movies, prices " +
                             "WHERE directors LIKE " + "'%" + director + "%' " + "AND " +
                                 "prices.movieID = movies.movieID AND " +
                                 "movieTitle = movieTitle";
    
                PreparedStatement stmt = c.prepareStatement(sql);
    
                ResultSet rs = stmt.executeQuery();
    
                System.out.printf("%-60s %-60s\n", "movieTitle:", "price:");
                while (rs.next()) {
                    String a = rs.getString("movieTitle");
                    int f = rs.getInt("price");
                    System.out.printf("%-60s %-60s\n", a, "$" + f);

                }
    
    
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }    

    private void purchase_Movie(int userID) { 
        Scanner input = new Scanner(System.in);
        //int userID = 4;
        boolean alreadyPurchased = false;
        boolean inDatabase = false;
        boolean enoughBalance = false;
        String confirm = "nothing";

        //will need to insert the date if a movie is purchased
        // getting the current date in real time and converting it into a string
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // capitol M is for the month
        String strDate = dateFormat.format(date); //date is in string



        System.out.print("Enter the title of the movie you want to purchase: ");
        String chosenMovie = input.nextLine();
        //input.nextLine();


        /*First Check if the movie to be purchased is in the database or not.*/
        try {

            String sql = "SELECT movies.movieTitle " +
                         "FROM movies " +
                         "WHERE movies.movieTitle = ?";

            PreparedStatement stmt = c.prepareStatement(sql);

        
            stmt.setString(1, chosenMovie);
            //stmt.setInt(2, userID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String a = rs.getString("movieTitle");
                //System.out.print(a + "\n");
                //System.out.print(chosenMovie + "\n");

                    if (a.equals(chosenMovie)) {
                    //System.out.print("Movie is in database" + "\n");
                    inDatabase = true;
                    }
            }

            rs.close();
            stmt.close();

            //c.commit();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }



        /*Second, check if the user has already purchased the movie before*/
        if (inDatabase == true) {
            try {

                String sql = "SELECT purchases.purchasedMovies " +
                            "FROM purchases " +
                            "WHERE purchases.purchasedMovies = ? " +
                            "AND purchases.userID = ?";

                PreparedStatement stmt = c.prepareStatement(sql);

            
                stmt.setString(1, chosenMovie);
                stmt.setInt(2, userID);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String a = rs.getString("purchasedMovies");
                    //System.out.print(a + "\n");
                    //System.out.print(chosenMovie + "\n");

                        if (a.equals(chosenMovie)) {
                        System.out.print("\nYou have already purchased " + chosenMovie + "." + "\n");
                        alreadyPurchased = true;
                        }
                }

                //c.commit();
                rs.close();
                stmt.close();
            

            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }


        /*Third, check if the user has enought money in their balance to purchase the movie*/
        if (alreadyPurchased == false && inDatabase == true) {

            try {

                String sql = "SELECT prices.price, users.balance " +
                             "FROM movies, users, prices " +
                             "WHERE userID = ? AND " +
                             "movies.movieTitle = ? AND " +
                             "movies.movieID = prices.movieID";

                PreparedStatement stmt = c.prepareStatement(sql);

            
                stmt.setInt(1, userID);
                stmt.setString(2, chosenMovie);

                ResultSet rs = stmt.executeQuery();

                System.out.printf("\n");
                System.out.printf("%-20s %-20s\n", "Your Balance:", "Movie Price:");

                while (rs.next()) {
                    int a = rs.getInt("price");
                    int b = rs.getInt("balance");
                    //System.out.print(a + "\n");
                    //System.out.print(chosenMovie + "\n");
                    
                    System.out.printf("%-20s %-20s\n", "$" + b, "$" + a);

                        if (a <= b) {
                            enoughBalance = true;
                        }
                }

                rs.close();
                stmt.close();
            
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }


        //Statements get printed according to what values are assigned to several boolean variables
        if (inDatabase == false && alreadyPurchased == false && enoughBalance == false) {
            System.out.println("\nThat movie is not in our database.");
            inDatabase = false;
        }
        else if (enoughBalance == false && inDatabase == true && alreadyPurchased == false) {
            System.out.println("\nYou don't have enough money in your balance to purchase " + chosenMovie + ".");
        }

        /*Ask the user to confirm the purchase*/
        else if (enoughBalance == true && inDatabase == true && alreadyPurchased == false) {
        


            System.out.println("Confirm purchase of " + chosenMovie + "? (Y or N)");
            confirm = input.next();
        }

        /*if purchased is confirmed, then add the movie to the purchases table, subtract from the user's balance
        and print that the movie has been purchased.*/
        if ((confirm.equals("Y") && enoughBalance == true && inDatabase == true && alreadyPurchased == false) || (confirm.equals("y") && enoughBalance == true && inDatabase == true && alreadyPurchased == false)) {

            try {
                //insert movie into purchases table.
                String sql = "INSERT INTO purchases(userID, purchasedMovies, purchaseDate) " +
                            "VALUES (?, ?, ?)";

                PreparedStatement stmt = c.prepareStatement(sql);

                stmt.setInt(1, userID);
                stmt.setString(2, chosenMovie);
                stmt.setString(3, strDate);

                stmt.addBatch();
                stmt.executeBatch();

                c.commit();
                stmt.close();
            
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            //Subtract price from user's balance.
            try {

                String sql = "UPDATE users " +
                            "SET balance = (users.balance - (SELECT prices.price " +
                                                            "FROM movies, prices " +
                                                            "WHERE prices.movieID = movies.movieID AND " +
                                                            "movies.movieTitle = ?)) " +
                            "WHERE users.userID = ?";

                PreparedStatement stmt = c.prepareStatement(sql);

                stmt.setString(1, chosenMovie);
                stmt.setInt(2, userID);

                stmt.addBatch();
                stmt.executeBatch();

                c.commit();
                stmt.close();
            
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }

            try { //Remove the purchased movie from the user's wishlist if it is on his/her wishlist

                String sql = "DELETE FROM userWishlist " +
                            "WHERE userID = ? AND " +
                                "movieTitle = ?";

                PreparedStatement stmt = c.prepareStatement(sql);

                stmt.setInt(1, userID);
                stmt.setString(2, chosenMovie);

                stmt.addBatch();
                stmt.executeBatch();

                c.commit();
                stmt.close();
            
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }


            //Print out that the movie has been purchased
            System.out.println("Transaction confirmed:");
            System.out.println("Thank you for purchasing " + chosenMovie);


            //Print out the user's new account balance
            try {

                String sql = "SELECT users.balance " +
                            "FROM users " +
                            "WHERE userID = ?";

                PreparedStatement stmt = c.prepareStatement(sql);

            
                stmt.setInt(1, userID);

                ResultSet rs = stmt.executeQuery();

                System.out.printf("\n");
                System.out.printf("%-20s\n", "Your New Balance:");

                while (rs.next()) {
                    int a = rs.getInt("balance");

                    
                    System.out.printf("%-20s\n", "$" + a);


                }

                rs.close();
                stmt.close();
            
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
            }
            /*if user did not confirm, then the transaction is canceled, nothing is inserted into purchases, and the
            user's account balance is not subtracted from*/
            else if ((confirm.equals("N") && enoughBalance == true && inDatabase == true && alreadyPurchased == false) || (confirm.equals("n") && enoughBalance == true && inDatabase == true && alreadyPurchased == false)) {
                System.out.println("Transaction canceled");

            }
    }

    private void comment(int userID) {
        Scanner input = new Scanner(System.in);
        String comment, movie, confirm;
        int rating;

        // For the comment date being in real time
        Date commentDate = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
        String strDate = dateFormat.format(commentDate); // convert date to string

        // 2) movie name
        System.out.print("Enter name of movie you want to rate: ");
        movie = input.nextLine();

        // 3) rating
        System.out.print("Enter rating for movie (0 - 10): ");
        rating = input.nextInt();

        while(rating < 0 || rating > 10) {
            System.out.print("Rating out of range, please enter rating (1 - 10): ");
            rating = input.nextInt();
        }

        System.out.print("Enter a comment for the movie: ");
        comment = input.nextLine();
        comment = input.nextLine();

        System.out.println();

        System.out.println("Is the information below correct (Y or N)");
        System.out.println("Movie: " + movie);
        System.out.println("Rating: " + rating + "/10");
        System.out.println("Comment: " + comment);
        System.out.println("Date: " + strDate);
        confirm = input.next();

        if(confirm.equals("y") || confirm.equals("Y")) {
            System.out.println("Inserting in Database");

            try {
                String sql = "INSERT INTO comments VALUES(?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = c.prepareStatement(sql);

                stmt.setInt(2, userID);
                stmt.setString(3, movie);
                stmt.setString(4, comment);
                stmt.setString(5, strDate);
                stmt.setInt(6, rating);
                stmt.addBatch();
                stmt.executeBatch();

                c.commit();
                stmt.close();
           
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            System.out.println();

            try {
                System.out.printf("%-20s %-20s %-20s %-20s %-20s\n", "commentID", "userID", "movie", "comment", "commentDate", "rating");
                String sql = "SELECT *" +
                            " FROM comments" + 
                            " WHERE commentID = (SELECT MAX(commentID) FROM comments)";

                Statement stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                c.commit();

                while(rs.next()) {
                    int getCommentID = rs.getInt(1);
                    int getuserID = rs.getInt(2);
                    String getMovie = rs.getString(3);
                    String getComment = rs.getString(4);
                    String getCommentDate = rs.getString(5);
                    int getRating = rs.getInt(6);
                    System.out.printf("%-20s %-20s %-20s %-20s %-20s %-20s\n", getCommentID, getuserID, getMovie, getComment, getCommentDate, getRating);
                }
                rs.close();
                stmt.close();

            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                try {
                    c.rollback();
                } catch (Exception e1) {
                    System.err.println(e1.getClass().getName() + ": " + e1.getMessage());
                }
            }
        }
        else if(confirm.equals("N") || confirm.equals("n")) {
            System.out.println("Comment Cancelled");
        }
    }

    private void rate() {
        Scanner input = new Scanner(System.in);
        String movie;
        System.out.print("Please enter name of movie you want to see ratings of: ");
        movie = input.nextLine();
        System.out.println(movie);

            try {

                String sql = "UPDATE ratings" + 
                            " SET AvgUserRating = (SELECT AVG(rating) FROM comments WHERE movie = ?)" + 
                            " WHERE movieTitle = ?";

                PreparedStatement stmt = c.prepareStatement(sql);

                
                stmt.setString(1, movie);
                stmt.setString(2, movie);


                stmt.addBatch();
                stmt.executeBatch();

                c.commit();
                stmt.close();
            
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }

            try {
                String sql = "SELECT * FROM ratings" + 
                            " WHERE movieTitle = ?";

                PreparedStatement stmt = c.prepareStatement(sql);

                stmt.setString(1, movie);
                ResultSet rs = stmt.executeQuery();
                // c.commit();

                System.out.printf("%-20s %-20s %-20s %-20s\n", "movieTitle", "RottenTomatoes", "IMDB", "AvgUserRating");
                while(rs.next()) {
                   String getMovie = rs.getString(1);
                   int getRottenTomatoes = rs.getInt(2);
                   double getIMDB = rs.getDouble(3);
                   double getAvgRating = rs.getDouble(4);
                   System.out.printf("%-20s %-20s %-20s %-20s\n", getMovie, getRottenTomatoes, getIMDB, getAvgRating);
                }
                rs.close();
                stmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                try {
                    c.rollback();
                } catch (Exception e1) {
                    System.err.println(e1.getClass().getName() + ": " + e1.getMessage());
                }
            }
        }

    private void wish_List(int userID) {
        Scanner input = new Scanner(System.in);

        boolean inList = false;
        boolean inDatabase = false;
        boolean alreadyPurchased = false;


        System.out.println("\n1. View WishList");
        System.out.println("2. Add to WishList");
        System.out.println("3. Remove from WishList");
        int choice = input.nextInt();
        input.nextLine();

            //View the user's WishList
            if (choice == 1) {
            try {
                
                    String sql = "SELECT movieTitle " +
                                "FROM userWishlist " +
                                "WHERE userID = ?";
        
                    PreparedStatement stmt = c.prepareStatement(sql);

                
                    stmt.setInt(1, userID);

                    ResultSet rs = stmt.executeQuery();

                    System.out.printf("\n");
                    System.out.printf("%-20s\n", "Your WishList:");

                    while (rs.next()) {
                        String a = rs.getString("movieTitle");
                        
                        System.out.printf("%-20s\n",  a);

                    }

                    rs.close();
                    stmt.close();
                   
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
        }

            else if (choice == 2) { //insert movie into wishlist if the movie is not already in the wishlist'

                System.out.print("Enter the movie you would like to add to your WishList: ");
                String chosenMovie = input.nextLine();
                //input.nextLine();

                try {

                    String sql = "SELECT movies.movieTitle " +
                                 "FROM movies " +
                                 "WHERE movies.movieTitle = ?";
        
                    PreparedStatement stmt = c.prepareStatement(sql);

                    stmt.setString(1, chosenMovie);
                    //stmt.setInt(2, userID);

                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        String a = rs.getString("movieTitle");


                            if (a.equals(chosenMovie)) {
                            inDatabase = true;
                            }
                    }

                    rs.close();
                    stmt.close();
    
                    //c.commit();
        
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }


            if (inDatabase == true) {
                try {
    
                    String sql = "SELECT purchases.purchasedMovies " +
                                "FROM purchases " +
                                "WHERE purchases.purchasedMovies = ? " +
                                "AND purchases.userID = ?";
    
                    PreparedStatement stmt = c.prepareStatement(sql);

                
                    stmt.setString(1, chosenMovie);
                    stmt.setInt(2, userID);

                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        String a = rs.getString("purchasedMovies");
                        //System.out.print(a + "\n");
                        //System.out.print(chosenMovie + "\n");

                        if (a.equals(chosenMovie)) {
                        System.out.print("\nYou have already purchased " + chosenMovie + "." + "\n");
                        alreadyPurchased = true;
                        }
                    }

                    //c.commit();
                    rs.close();
                    stmt.close();
    

                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }

                if (inDatabase == true && alreadyPurchased == false) {
                    try {
                    
                        String sql = "SELECT movieTitle " +
                                    "FROM userWishlist " +
                                    "WHERE userID = ?";
            
                        PreparedStatement stmt = c.prepareStatement(sql);

                    
                        stmt.setInt(1, userID);

                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            String a = rs.getString("movieTitle");
                            if (a.equals(chosenMovie)) {
                                inList = true;
                                System.out.println("\n" + chosenMovie + " is already in your WishList.");
                            }

                        }

                        rs.close();
                        stmt.close();
                    

                
                    } catch (Exception e) {
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    }
                }


                if (inDatabase == true && alreadyPurchased == false && inList == false) {

                    try {

                        String sql = "INSERT INTO userWishlist(userID, movieTitle) " +
                                    "SELECT ?, ?";
        
                        PreparedStatement stmt = c.prepareStatement(sql);

                        stmt.setInt(1, userID);
                        stmt.setString(2, chosenMovie);

                        stmt.addBatch();
                        stmt.executeBatch();
        
                        c.commit();
                        stmt.close();

                        System.out.println("\n" + chosenMovie + " has been successfully added to your WishList.");
                    
                    } catch (Exception e) {
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    }

                }



                if (inDatabase == false && alreadyPurchased == false && inList == false) {
                    System.out.println("\nThat movie is not in our database.");
                }
        }

        else if (choice == 3) { //insert movie into wishlist if the movie is not already in the wishlist'

            System.out.print("Enter the movie you would like to remove from your WishList: ");
            String chosenMovie = input.nextLine();
            //input.nextLine();

            try {

                String sql = "SELECT movies.movieTitle " +
                            "FROM movies " +
                            "WHERE movies.movieTitle = ?";

                PreparedStatement stmt = c.prepareStatement(sql);

            
                stmt.setString(1, chosenMovie);
                //stmt.setInt(2, userID);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String a = rs.getString("movieTitle");


                        if (a.equals(chosenMovie)) {
                        inDatabase = true;
                        }
                }

                            rs.close();
                            stmt.close();
            
                            //c.commit();

            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }


            if (inDatabase == true) {
                try {

                    String sql = "SELECT purchases.purchasedMovies " +
                                "FROM purchases " +
                                "WHERE purchases.purchasedMovies = ? " +
                                "AND purchases.userID = ?";

                    PreparedStatement stmt = c.prepareStatement(sql);

                
                    stmt.setString(1, chosenMovie);
                    stmt.setInt(2, userID);

                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        String a = rs.getString("purchasedMovies");
                        //System.out.print(a + "\n");
                        //System.out.print(chosenMovie + "\n");

                            if (a.equals(chosenMovie)) {
                            System.out.print("\nYou have already purchased " + chosenMovie + "." + "\n");
                            alreadyPurchased = true;
                            }
                    }

                    //c.commit();
                    rs.close();
                                stmt.close();
                

                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }



            if (inDatabase == true && alreadyPurchased == false) {

                try {

                    String sql = "DELETE FROM userWishlist " +
                                "WHERE userID = ? AND " +
                                    "movieTitle = ?";

                    PreparedStatement stmt = c.prepareStatement(sql);

                    stmt.setInt(1, userID);
                    stmt.setString(2, chosenMovie);

                    stmt.addBatch();
                    stmt.executeBatch();

                    c.commit();
                    stmt.close();

                    System.out.println("\n" + chosenMovie + " has been successfuly removed from your WishList.");
                
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }

            }



            if (inDatabase == false) {
                System.out.println("\nThat movie is not in our database.");
            }

        }
    }

boolean sign_In(String userName, int userID) {
    boolean verification = false;


    try {
            
        String sql = "SELECT users.userID, users.username " +
                     "FROM users " +
                     "WHERE users.userID = ? AND " +
                     "users.username = ?";

        PreparedStatement stmt = c.prepareStatement(sql);
        stmt.setInt(1, userID);
        stmt.setString(2, userName);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int a = rs.getInt("userID");
            String b = rs.getString("username");

            if (a == userID && b.equals(userName)) {
                verification = true;
            }

        }
    
        c.commit();
        stmt.close();


    } catch (Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }

    if (verification == true) {
        System.out.println("\nSign in succesful!");
        System.out.println("Hello, " + userName + ".");
    }
    else if (verification == false) {
        System.out.println("\nIncorrect Login Data!\n");
    }

    return verification;

}

    private void add_Funds(int userID) {

        try {

            String sql = "SELECT users.balance " +
                        "FROM users " +
                        "WHERE userID = ?";

            PreparedStatement stmt = c.prepareStatement(sql);

        
            stmt.setInt(1, userID);

            ResultSet rs = stmt.executeQuery();

            System.out.printf("\n");
            System.out.printf("%-20s\n", "Your Balance:");

            while (rs.next()) {
                int a = rs.getInt("balance");

                
                System.out.printf("%-20s\n", "$" + a);

            }

            rs.close();
            stmt.close();
        
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }

        Scanner input = new Scanner(System.in);
        System.out.print("\nEnter amount of money you would like to add to your account balance: ");
        int amount = input.nextInt();
        input.nextLine();


        try {

            String sql = "UPDATE users " +
                        "SET balance = ? + balance " +
                        "WHERE userID = ?";

            PreparedStatement stmt = c.prepareStatement(sql);

            stmt.setInt(1, amount);
            stmt.setInt(2, userID);

            stmt.addBatch();
            stmt.executeBatch();

            c.commit();
            stmt.close();
        
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }


        try {

            String sql = "SELECT users.balance " +
                        "FROM users " +
                        "WHERE userID = ?";

            PreparedStatement stmt = c.prepareStatement(sql);

        
            stmt.setInt(1, userID);

            ResultSet rs = stmt.executeQuery();

            System.out.println("\nTransaction success.");
            System.out.println("You have added " + "$" + amount + " to your account balance.");

            System.out.printf("\n");
            System.out.printf("%-20s\n", "Your New Balance:");

            while (rs.next()) {
                int a = rs.getInt("balance");
                
                System.out.printf("%-20s\n", "$" + a);

            }

            rs.close();
            stmt.close();
        
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }

    }

    public static void main(String args[]) {
        Project sj = new Project();
        Scanner input = new Scanner(System.in);
        boolean exit = false;
        String username = null;
        boolean signIn = false;
        int userID = -1;

        sj.openConnection("data/movieData.sqlite");


        do {
            if (signIn == false) {
                System.out.println("1. Create user");
                System.out.println("2. Sign in");
                System.out.println("3. Exit");
                int choice = input.nextInt();  // Read user input
                input.nextLine();

                if (choice == 1) {
                    sj.create_User();
                }
                else if (choice == 2) {
                    System.out.print("Enter username: ");
                    username = input.nextLine();
                    System.out.print("Enter user ID: ");
                    userID = input.nextInt();
                    input.nextLine();
                    signIn = sj.sign_In(username, userID);
                }
                else if (choice == 3) {
                    System.out.println("Exited");
                    //username = null;
                    exit = true;
                }
            }   
    
            if (signIn == true) {
        
                    System.out.println("\n1. Search Movie"); //leads to searching by title, genre, etc...
                    System.out.println("2. Purchase Movie");
                    System.out.println("3. Comment"); //goes to Comment() which shoul give a choice of createing, viewing, or deleting comment.
                    System.out.println("4. View Ratings");  //goes to Rate(); which should give a choice of view ratings or create rating
                    System.out.println("5. Wish List");
                    System.out.println("6. Add funds");
                    System.out.println("7. Log Out");
        
                    int choice3 = input.nextInt();
        
                    if (choice3 == 1) {
                        System.out.println("\n1. Search by title"); 
                        System.out.println("2. Search by genre");
                        System.out.println("3. Search by Director"); 

                        String searchChoice = input.next();  // Read user input
                        input.nextLine();  //need this line to finish the line and move to the next so nextLine will work
                        
                        sj.movie_Search(searchChoice);

                    }
                    else if (choice3 == 2) {
                        sj.purchase_Movie(userID); //Takes user ID as input
                    }
                    else if (choice3 == 3) {
                        sj.comment(userID); //takes username as input in case user wants to create a comment
                    }
                    else if (choice3 == 4) {
                        sj.rate(); //Also takes username as input
                    }
                    else if (choice3 == 5) {
                        sj.wish_List(userID); //Also takes username as input
                    }
                    else if (choice3 == 6) {
                        sj.add_Funds(userID);
                    }
                    else if (choice3 == 7) {
                        System.out.println("\nLogged Out\n");
                        signIn = false;
                    }
        
                }
            
        } while (exit == false);


        input.close();



        sj.closeConnection();

    }
}
