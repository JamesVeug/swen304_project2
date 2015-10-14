package swen304_project2;

/*
 * LibraryModel.java
 * Author:
 * Created on:
 */



import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LibraryModel {

    // For use in creating dialogs and making them modal
    private JFrame dialogParent;
    private Connection con = null;
    private boolean paused = false;

    public LibraryModel(JFrame parent, String userid, String password) throws Exception {
    	dialogParent = parent;

    	String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/"+userid+"_jdbc";

			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url, userid,password);


    }

    /**
     * Displays information about a specific book if it's in the database
     * @param isbn
     * @return
     */
    public String bookLookup(int isbn) {

    	String select = "SELECT * FROM Book Natural Join Book_Author Natural Join Author"
    			      + " WHERE ISBN="+isbn + " ORDER BY authorid";


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
			e.printStackTrace();
		}


    	return "Unable to look up book";
    }

    /**
     * Displays all the books in the database
     * @return
     */
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

				int isbn = rs.getInt("isbn");
				if( !isbns.contains(isbn) ){
					isbns.add(isbn);
					titles.add(rs.getString("title").trim());
					copies.add(rs.getInt("numofcop"));
					noLefts.add(rs.getInt("numleft"));
					editions.add(rs.getInt("edition_no"));
					surnames.add(rs.getString("surname").trim());
				}
				else{
					int index = isbns.indexOf(isbn);

					String surname = rs.getString("surname").trim();
					surnames.set(index, surnames.get(index) + ", " + surname);
				}
			}

			String finalString = "Show Catalogue: \n\n";
			for( int i = 0; i < isbns.size(); i++ ){
				finalString +=
						"Isbn: " + isbns.get(i) + ": " + titles.get(i) + "\n" +
						"\t Edition: " + editions.get(i) + " - Number of copies: " + copies.get(i) + " - Copies left: " + noLefts.get(i) + "\n" +
						"\t Authors: " + surnames.get(i) + "\n";

			}

			return finalString;
		} catch (SQLException e) {
			e.printStackTrace();
		}

    	return "Can not Show Catalogue";
    }

    public String showLoanedBooks() {

    	String select = "SELECT * FROM Customer Natural Join Cust_Book Natural Join"
    			+ " Book Natural Join Book_Author Natural Join Author"
    			      + " ORDER BY authorid";


		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(select);

			int j_isbn = -1;
			String j_title = "";
			int j_copies = -1;
			int j_noLeft = -1;
			int j_editionnumber = -1;
			int j_custid = -1;
			String j_l_name = "";
			String j_f_name = "";
			String j_city = "";
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
				j_custid = rs.getInt("customerid");
				j_l_name = rs.getString("l_name");
				j_f_name = rs.getString("f_name");
				j_city = rs.getString("city");
			}


			String finalRow = list.get(0);
			for(int i = 1; i < list.size(); i++){
				finalRow += ", " +list.get(i);
			}

			return "Show Loaned Books: \n \n" +
					"\t Isbn: " + j_isbn + ": " + j_title.trim() + "\n" +
					"\t Edition: " + j_editionnumber + " - Number of copies: " + j_copies + " - Copies left: " + j_noLeft + "\n" +
					"\t Authors: " + finalRow + "\n\t Borrowers:\n" + "\t\t" + j_custid+ ": " + j_l_name.trim() + ", " + j_f_name.trim() + " - " + j_city.trim() + "\n";
		} catch (SQLException e) {
			e.printStackTrace();
		}


    	return "Unable to look up book";
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


    	return "AuthorID does not exist!";
    }

    public String showAllAuthors() {

    	String select = "SELECT * FROM Author";


		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(select);

			int q_id = -1;
			String q_name = "";
			String q_surname = "";

			String result = "Show All Authors:\n";

			while (rs.next()){
				// extracting data from rs tuples
				// data processing

				q_id = rs.getInt("authorid");
				q_name = rs.getString("name").trim();
				q_surname = rs.getString("surname").trim();

				result += "\t" + q_id + ": " + q_surname + ", " + q_name + "\n";

			}

			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    	return "No authors in database!";
    }

    /**
     * Shows the information regarding a custom and the customers borrowed books
     * @param customerID
     * @return
     */
    public String showCustomer(int customerID) {
    	String select = "SELECT * FROM customer"
			      + " WHERE customerid="+customerID;


		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(select);

			int id = -1;
			String fname = "";
			String lname = "";
			String city= "";

			while (rs.next()){
				// extracting data from rs tuples
				// data processing

				id = rs.getInt("customerid");
				fname = rs.getString("f_name").trim();
				lname = rs.getString("l_name").trim();
				city = rs.getString("city").trim();
			}


			// Display message if not found
			String customer = "Customer with id " + customerID + " does not exist.";
			if( id != -1 ){
				customer = "Show Customer: \n" +
						"\t " + id+ ": " + lname + ", " + fname + " - " + city + "\n";


				String booksBorrowed = getBorrowedBooks(customerID);
				customer += "\t" +booksBorrowed;

				// if the customer has books, borrow books
			}


			return customer;
		} catch (SQLException e) {
			e.printStackTrace();
		}

    	return "Show Customer Stub";
    }

    /**
     * Returns a string containing the books the customer has borrowed, otherwise a message saying (No books borrowed)
     * @param customerID
     * @return
     */
    public String getBorrowedBooks(int customerID){

    	String selectBooks = "SELECT isbn,title from cust_book Natural Join book where customerID=" + customerID;
    	try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectBooks);

			String bookList = "Books Borrowed:";
			int bookCount = 0;
			while(rs.next()){
				String isbn = rs.getString("isbn");
				String title = rs.getString("title");

				bookList += "\n\t\t" + isbn + " - " + title;
				bookCount++;
			}

			if( bookCount > 0 ){
				return bookList;
			}
			else{
				return "(No books borrowed)";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

    	return "Could not get borrowed books.";
    }

    public String showAllCustomers() {

    	String select = "SELECT * FROM Customer";


		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(select);

			int q_id = -1;
			String q_l_name = "";
			String q_f_name = "";
			String q_city = "";

			String result = "Show All Authors:\n";

			while (rs.next()){
				// extracting data from rs tuples
				// data processing

				q_id = rs.getInt("customerid");
				q_l_name = rs.getString("l_name").trim();
				q_f_name = rs.getString("f_name").trim();
				q_city = rs.getString("city");
				if (q_city == null) q_city = "(no city)";
				else q_city = q_city.trim();

				result += "\t" + q_id + ": " + q_l_name + ", " + q_f_name + " - " + q_city + "\n";

			}

			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    	return "No customers in database!";
    }

    /**
     * Attempte borrowing a book
     * @param isbn
     * @param customerID
     * @param day
     * @param month
     * @param year
     * @return
     */
    public String borrowBook(int isbn, int customerID,
			     int day, int month, int year) {

    	String error = null;

    	String date = day + "-" + month + "-" + year;

    	// Check if customer exists
    	String checkCustomerExists = showCustomer(customerID);
    	if( checkCustomerExists.startsWith("Customer with id")){

    		// Customer does not exist
    		return checkCustomerExists;
    	}

    	// Save for reassigning
    	int originalIsolation = 0;
    	try {
			originalIsolation = con.getTransactionIsolation();
		} catch (SQLException e2) {e2.printStackTrace();}


    	// Attempt to Borrow the book
    	PreparedStatement update_stmt = null; // Update numLeft
    	PreparedStatement select_stmt = null; // Checking numLeft of book in table
    	PreparedStatement insert_stmt = null; // Inserting new elements into table
		try {

			//
			// Insert into cust_books
			//
	    	String insert="INSERT INTO cust_book " + "VALUES ("+isbn+",'"+date+"',"+customerID+")";

	    	con.setAutoCommit(false);
	    	con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
	    	insert_stmt = con.prepareStatement(insert);
	    	int insertChanges = insert_stmt.executeUpdate();
	    	con.commit();


			// Successful insert
	    	if( insertChanges == 0 ){
	    		throw new RuntimeException("Failed to Borrow book.");
	    	}


	    	//
	    	// Select numLeft of book
	    	//
	    	String getNumLeft = "SELECT numLeft from book where isbn= " + isbn;
	    	select_stmt = con.prepareStatement(getNumLeft);
	    	con.commit();

	    	ResultSet rs = select_stmt.executeQuery();
	    	int numLeft = -1;
	    	while(rs.next()){
	    		numLeft = rs.getInt("numLeft");
	    	}

	    	// Make sure we have boosk available
	    	if( numLeft < 1 ){
	    		throw new RuntimeException("No books available for this book.");
	    	}
	    	numLeft--;


	    	//
	    	// Update Book num left
	    	//
	    	String update = "UPDATE book set numLeft="+numLeft+" where isbn = " + isbn;
	    	update_stmt = con.prepareStatement(update);

	    	int updateChanges = update_stmt.executeUpdate();

	    	// Successful update
	    	if( updateChanges == 0 ){
	    		throw new RuntimeException("Failed to Reduce number of copies left.");
	    	}
	    	else{
	    		error = "Successfuly borrowed book.";
	    		error += "\n\t" + numLeft + " copies left.";
		    	con.commit();
	    	}

		} catch (Exception e) {

			// Display proper message
			String message = e.getMessage().trim();
			if( message.startsWith("ERROR: duplicate key value violates unique constraint \"cust_book_pkey\"")){
				error = "Book with isbn " + isbn + " is already being borrowed.";
			}
			else if( message.startsWith("ERROR: insert or update on table \"cust_book\" violates foreign key constraint")){
				error = "Book with isbn " + isbn + " does not exist.";
			}
			else{
				System.out.println("Message '" + e.getMessage() + "'");
				e.printStackTrace();
				error = message;
			}

			// Rollback
			try {
				if(con != null ){
					con.rollback();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				error += "\n Failed to rollback";
			}
		}
		finally{

			// Close statements
			if( insert_stmt != null ){
				try {
					insert_stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					error += "\n Failed to close insert statement.";
				}
			}
			if( update_stmt != null ){
				try {
					update_stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					error += "\n Failed to close update statement.";
				}
			}
			if( select_stmt != null ){
				try {
					select_stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					error += "\n Failed to close select statement.";
				}
			}

			// Reassign autocommit to true
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
				error += "\n Failed to assign connetion to AutoCommit(true).";
			}

			// Reassign isolation
			try {
				con.setTransactionIsolation(originalIsolation);
			} catch (SQLException e) {
				e.printStackTrace();
				error += "\n Could not reassign isolation";
			}
		}


		return error;
    }

    public void popupDialog() {
    	setPaused(true);
    	PauseDialog d = new PauseDialog();
	}

    /**
     * Return book to the library
     * @param isbn
     * @param customerid
     * @return
     */
	public String returnBook(int isbn, int customerid) {
		String error = null;

    	// Check if customer exists
    	String checkCustomerExists = showCustomer(customerid);
    	if( checkCustomerExists.startsWith("Customer with id")){

    		// Customer does not exist
    		return checkCustomerExists;
    	}

    	// Save for reassigning
    	int originalIsolation = 0;
    	try {
			originalIsolation = con.getTransactionIsolation();
		} catch (SQLException e2) {e2.printStackTrace();}


    	// Attempt to Borrow the book
    	PreparedStatement update_stmt = null; // Update numLeft
    	PreparedStatement select_stmt = null; // Checking numLeft of book in table
    	PreparedStatement insert_stmt = null; // Inserting new elements into table
		try {

			//
			// Insert into cust_books
			//
	    	String delete="DELETE FROM cust_book " + "WHERE isbn="+isbn+",AND customerid="+customerid+")";

	    	con.setAutoCommit(false);
	    	con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
	    	insert_stmt = con.prepareStatement(delete);
	    	int deleteChanges = insert_stmt.executeUpdate();
	    	con.commit();


			// Successful insert
	    	if( deleteChanges == 0 ){
	    		throw new RuntimeException("Failed to Return book.");
	    	}


	    	//
	    	// Select numLeft of book
	    	//
	    	String getNumLeft = "SELECT numLeft from book where isbn= " + isbn;
	    	select_stmt = con.prepareStatement(getNumLeft);
	    	con.commit();

	    	ResultSet rs = select_stmt.executeQuery();
	    	int numLeft = -1;
	    	while(rs.next()){
	    		numLeft = rs.getInt("numLeft");
	    	}
	    	numLeft++;


	    	//
	    	// Update Book num left
	    	//
	    	String update = "UPDATE book set numLeft="+numLeft+" where isbn = " + isbn;
	    	update_stmt = con.prepareStatement(update);

	    	int updateChanges = update_stmt.executeUpdate();

	    	// Successful update
	    	if( updateChanges == 0 ){
	    		throw new RuntimeException("Failed to Increase number of copies left.");
	    	}
	    	else{
	    		error = "Successfuly returned book.";
	    		error += "\n\t" + numLeft + " copies left.";
		    	con.commit();
	    	}

		} catch (Exception e) {

			// Display proper message
			String message = e.getMessage().trim();
			System.out.println("Message '" + e.getMessage() + "'");
			e.printStackTrace();
			error = message;

			// Rollback
			try {
				if(con != null ){
					con.rollback();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				error += "\n Failed to rollback";
			}
		}
		finally{

			// Close statements
			if( insert_stmt != null ){
				try {
					insert_stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					error += "\n Failed to close insert statement.";
				}
			}
			if( update_stmt != null ){
				try {
					update_stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					error += "\n Failed to close update statement.";
				}
			}
			if( select_stmt != null ){
				try {
					select_stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					error += "\n Failed to close select statement.";
				}
			}

			// Reassign autocommit to true
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
				error += "\n Failed to assign connetion to AutoCommit(true).";
			}

			// Reassign isolation
			try {
				con.setTransactionIsolation(originalIsolation);
			} catch (SQLException e) {
				e.printStackTrace();
				error += "\n Could not reassign isolation";
			}
		}


		return error;
	}

    public void closeDBConnection() {
    }

    public String deleteCus(int customerID) {

    	String delete = "DELETE FROM customer "
    			      + "WHERE customerid="+customerID;

    	String cust_delete = "DELETE FROM cust_book "
			      + "WHERE customerid="+customerID;


		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(cust_delete);
			Statement stmt2 = con.createStatement();
			stmt2.executeUpdate(delete);

			return "Deleted customer " + customerID;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    	return "Customer could not be deleted!";
    }

    public String deleteAuthor(int authorID) {

    	String delete = "DELETE FROM author "
    			      + "WHERE authorid="+authorID;

    	String foreign_delete = "DELETE FROM book_author "
			      + "WHERE authorid="+authorID;


		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(foreign_delete);
			Statement stmt2 = con.createStatement();
			stmt2.executeUpdate(delete);

			return "Deleted author " + authorID;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    	return "Author could not be deleted!";
    }

    public String deleteBook(int isbn) {

    	String delete = "DELETE FROM book "
    			      + "WHERE isbn="+isbn;

    	String foreign_delete = "DELETE FROM book_author "
			      + "WHERE isbn="+isbn;

    	String cust_delete = "DELETE FROM cust_book "
			      + "WHERE isbn="+isbn;


		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(foreign_delete);
			Statement stmt2 = con.createStatement();
			stmt2.executeUpdate(cust_delete);
			Statement stmt3 = con.createStatement();
			stmt3.executeUpdate(delete);

			return "Deleted book " + isbn;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    	return "Book could not be deleted!";
    }

    class PauseDialog extends JDialog{
        private boolean okButtonClicked = false;

        String introText = "Press okay to unpause the program";
        private JPanel dialogPanel = new JPanel();
        private JPanel labelPanel = new JPanel();
        private JPanel inputPanel = new JPanel();

        public PauseDialog() {
    	this(null, "Paused", false);
        }

        public PauseDialog(JFrame parent) {
    	this(parent, "Paused", true);
        }

        public PauseDialog(JFrame parent, String title) {
    	this(parent, title, true);
        }

        public PauseDialog(final JFrame parent, String title, boolean modal) {
    	super(parent, title, modal);

    	// Set up close behaviour
    	setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    	addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
    		    if (!okButtonClicked)
    			System.exit(0);
    		}
    	    });

    	// Set up OK button behaviour
    	JButton okButton = new JButton("OK");
    	okButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    		    okButtonClicked = true;
    		    setVisible(false);
    		    LibraryModel.this.setPaused(false);
    		}
    	    });

    	// Set up dialog contents
    	labelPanel.setBorder(BorderFactory.createEmptyBorder(20,20,5,5));
    	inputPanel.setBorder(BorderFactory.createEmptyBorder(20,5,5,20));

    	labelPanel.setLayout(new GridLayout(2, 1));
    	inputPanel.setLayout(new GridLayout(2, 1));

    	Box buttonPane = new Box(X_AXIS);
    	buttonPane.add(Box.createHorizontalGlue());
    	buttonPane.add(okButton);
    	buttonPane.add(Box.createHorizontalStrut(5));
    	buttonPane.add(Box.createHorizontalGlue());
    	buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

    	JLabel introLabel = new JLabel(introText);
    	introLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	getContentPane().add(introLabel, BorderLayout.NORTH);
    	getContentPane().add(labelPanel, BorderLayout.WEST);
    	getContentPane().add(inputPanel, BorderLayout.CENTER);
    	getContentPane().add(buttonPane, BorderLayout.SOUTH);

    	// Ensure the enter key triggers the OK button
    	getRootPane().setDefaultButton(okButton);

    	// And that the escape key exits
    	InputMap inputMap =
    	    getRootPane().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    	ActionMap actionMap = getRootPane().getActionMap();
    	inputMap.put(getKeyStroke("ESCAPE"), "exitAction");
    	actionMap.put("exitAction", new AbstractAction() {
    		public void actionPerformed(ActionEvent e) {
    		    System.exit(0);
    		}
    	    });

    	// Pack it all
    	pack();

    	// Center on the screen
    	setLocationRelativeTo(null);
    	setVisible(true);
        }
    }

	protected void setPaused(boolean b) {
		paused = b;
	}
}
