package swen304_project2;

/*
 * LibraryModel.java
 * Author:
 * Created on:
 */



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class LibraryModel {

    // For use in creating dialogs and making them modal
    private JFrame dialogParent;
    private Connection con = null;

    public LibraryModel(JFrame parent, String userid, String password) {
    	dialogParent = parent;

    	String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/"+userid+"_jdbc";

    	try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url, userid,password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public String bookLookup(int isbn) {

    	String select = "SELECT * FROM Book Natural Join Book_Author Natural Join Author"
    			      + " WHERE ISBN="+isbn;


		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(select);

			int j_isbn = -1;
			String j_title = "";
			int j_copies = -1;
			int j_noLeft = -1;
			int j_editionnumber = -1;
			List<String> list = new ArrayList<>();

			while (rs.next()){
				// extracting data from rs tuples
				// data processing

				j_isbn = rs.getInt("isbn");
				j_title = rs.getString("title");
				j_copies = rs.getInt("numofcop");
				j_noLeft = rs.getInt("numleft");
				j_editionnumber = rs.getInt("edition_no");
				list.add(rs.getString("surname").trim());
			}


			String finalRow = list.get(0);
			for(int i = 1; i < list.size(); i++){
				finalRow += ", " +list.get(i);
			}

			return "Book Lookup: \n" +
					"\t Isbn: " + j_isbn + ": " + j_title.trim() + "\n" +
					"\t Edition: " + j_editionnumber + " - Number of copies: " + j_copies + " - Copies left: " + j_noLeft + "\n" +
					"\t Authors: " + finalRow;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    	return "Unable to look up book";
    }

    public String showCatalogue() {

    	String select = "SELECT * FROM Book Natural Join Book_Author Natural Join Author order by isbn";


		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(select);

			List<Integer> isbns = new ArrayList<>();
			List<String> titles = new ArrayList<>();
			List<Integer> copies = new ArrayList<>();
			List<Integer> noLefts = new ArrayList<>();
			List<Integer> editions = new ArrayList<>();
			List<String> surnames = new ArrayList<>();

			while (rs.next()){
				// extracting data from rs tuples
				// data processing

				isbns.add(rs.getInt("isbn"));
				titles.add(rs.getString("title").trim());
				copies.add(rs.getInt("numofcop"));
				noLefts.add(rs.getInt("numleft"));
				editions.add(rs.getInt("edition_no"));
				surnames.add(rs.getString("surname").trim());
			}

			String finalString = "";
			for( int i = 0; i < isbns.size(); i++ ){
				finalString +=
						"Isbn: " + isbns.get(i) + ": " + titles.get(i) + "\n" +
						"\t Edition: " + editions.get(i) + " - Number of copies: " + copies.get(i) + " - Copies left: " + noLefts.get(i) + "\n" +
						"\t Authors: " + surnames.get(i);

			}

			return finalString;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return "Show Catalogue Stub";
    }

    public String showLoanedBooks() {
	return "Show Loaned Books Stub";
    }

    public String showAuthor(int authorID) {

    	String select = "SELECT * FROM Author Natural Join Book_Author Natural Join Book"
    			      + " WHERE authorid="+authorID;


		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(select);

			String q_name = "";
			String q_surname = "";
			List<Integer> isbn_list = new ArrayList<>();
			List<String> title_list = new ArrayList<>();

			while (rs.next()){
				// extracting data from rs tuples
				// data processing

				q_name = rs.getString("name").trim();
				q_surname = rs.getString("surname").trim();
				isbn_list.add(rs.getInt("isbn"));
				title_list.add(rs.getString("title").trim());
			}


			String finalRow = "";
			for (int i = 0; i < isbn_list.size(); i++) {
				finalRow += isbn_list.get(i) + " - " + title_list.get(i) + "\n";
			}

			return "Show Author:\n" + authorID + " - " + q_name + " " + q_surname +
					"\nBooks written:\n" + finalRow;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    	return "Wrong ID";
    }

    public String showAllAuthors() {
	return "Show All Authors Stub";
    }

    public String showCustomer(int customerID) {
	return "Show Customer Stub";
    }

    public String showAllCustomers() {
	return "Show All Customers Stub";
    }

    public String borrowBook(int isbn, int customerID,
			     int day, int month, int year) {



    	String insert="INSERT INTO Grades " +
    			"VALUES (007007,’C305’,’A+’)";

    	Statement stmt;
		try {
			stmt = con.createStatement();
			int return_value = stmt.executeUpdate(insert);
		} catch (SQLException e) {
			e.printStackTrace();
		}


	return "Borrow Book Stub";
    }

    public String returnBook(int isbn, int customerid) {
	return "Return Book Stub";
    }

    public void closeDBConnection() {
    }

    public String deleteCus(int customerID) {
    	return "Delete Customer";
    }

    public String deleteAuthor(int authorID) {
    	return "Delete Author";
    }

    public String deleteBook(int isbn) {
    	return "Delete Book";
    }
}
