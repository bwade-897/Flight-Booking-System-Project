package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import database.ConnectionMethod;
import database.DBQueries;
import database.Queries;
import common.User;

public class Action {


	// Name of customer info table
	final static String cTName = "`world`.`customer_info`";
	
	//Gets What flights user has reserved
	public static String removeUserFlight(String userId, String flightId) {
		try {
			
			if (correctFlightId(flightId)) {
				int passCount = Integer.parseInt(Queries.SELECT(flightId, "`world`.`flights`", "flight_number", "passenger_count"));
				--passCount;
				Queries.DELETE(userId, flightId);
				Queries.UPDATE("`world`.`reservations`", "passenger_count", Integer.toString(passCount), flightId, "flight_number");
				return "Removed";
			} else {
				return "Not found";
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			return "Error";
		}
		
	}

	//Booking flight method
	@SuppressWarnings("unused")
	public static String bookFlight(String flightId, String customerId) {
		int passCount;
		
		
		//Checks to see if input is correct
		if (correctFlightId(flightId)) {
			//Gets flights number of passengers
			try {
				passCount = Integer.parseInt(Queries.SELECT(flightId, "`world`.`flights`", "flight_number", "passenger_count"));
			} catch (Exception ex) {
				return "error";
			}
			
			//Checks to see if user already booked flight
			if (alreadyBooked(flightId, customerId) == false) {
				//Checks to see if passenger limit has been reached
				if (passCount < 10) {
					try {
						//Inserts data into reservations table
						Queries.INSERT(flightId, customerId);
						++passCount;
						//adds passenger to flight
						Queries.UPDATE("`world`.`flights`", "passenger_count", Integer.toString(passCount), flightId, "flight_number");
						return "flight booked";
					} catch (Exception ex) {
						System.out.println(ex);
						return "Problem booking flight";
					}
				} else {
					return "Full";
				}
			} else {
				return "Already booked";
			}
		} else {
			return "Not found";
		}

	}
	
	//Checks to see if user already booked flight
	public static boolean alreadyBooked(String fId, String uId) {
		try {
			ArrayList<String> flightIds = Queries.GETCOLUMN("flight_id", "`world`.`reservations`");
			ArrayList<String> userIds = Queries.GETCOLUMN("cust_id", "`world`.`reservations`");
			for (int i = 0; i < flightIds.size(); i++) {
				if (fId.equals(flightIds.get(i)) && uId.equals(userIds.get(i))) {
					return true;
				}
			}
			return false;
		} catch (Exception ex) {
			System.out.println("Already booked error");
			System.out.println(ex);
			return true;
		}
	}

	//Checks to see if input is correct
	public static boolean correctFlightId(String id) {
		try {
			Queries.SELECT(id, "`world`.`flights`", "flight_number");
			return true;
		} catch (Exception ex) {
			System.out.println(ex);
			return false;
		}
	}

	// Gets account info from window and stores in database
	public static void createAccount(Customer cust) throws Exception {
		try {
			Queries.INSERT(cust);
			System.out.println("Added");
		} catch (Exception ex) {
			System.out.println("Failed");
			System.out.println(ex);

		}

	}

	public static Flight insertFlight(String dest, String date, String time) throws Exception {

		try { // create database connection
			Connection connect = Queries.getConnection();
			Flight f1 = new Flight(departure, destination, flightNumber,);
			Queries.INSERT(f1);
			return f1;
		} catch (Exception o) {
			System.out.print("eat a potato");
			return null;
		}
	}

	// Authenticates Username and password
	public static boolean validLogin(String a, String b) throws Exception {

		boolean validUsername = false;
		boolean validPassword = false;
		try {
			ArrayList<String> usernames = Queries.GETCOLUMN("username", "`world`.`customer_info`");
			ArrayList<String> passwords = Queries.GETCOLUMN("password", "`world`.`customer_info`");
			for (int i = 0; i < usernames.size(); i++) {
				if (a.equals(usernames.get(i))) {
					validUsername = true;
					break;
				}
			}
			if (validUsername) {

				for (int i = 0; i < passwords.size(); i++) {
					if (b.equals(passwords.get(i))) {
						validPassword = true;
						return true;
					}
				}
			}

			return false;

		}

		catch (Exception ex) {
			System.out.println("Not found");
			System.out.println(ex);
			return false;
		}

	}

	public static User setUser(String username, String password) {
		User account = new User();
		String setUserId = null;

		try {
			ArrayList<String> usernames = Queries.GETCOLUMN("username", "`world`.`customer_info`");
			ArrayList<String> passwords = Queries.GETCOLUMN("password", "`world`.`customer_info`");
			ArrayList<String> userIds = Queries.GETCOLUMN("user_id", "`world`.`customer_info`");
			for (int i = 0; i < usernames.size(); i++) {
				if (usernames.get(i).equals(username) && passwords.get(i).equals(password)) {
					setUserId = userIds.get(i);
				}

			}
		} catch (Exception ex) {
			System.out.println("Find userId failed");
			System.out.println(ex);
			return null;
		}

		try {

			ArrayList<String> usernames = Queries.GETCUSTOMERROW("user_id", setUserId);
			account = userToArray(usernames);
			return account;

		} catch (Exception ex) {
			System.out.println("Set user failed");
			System.out.println(ex);
			return null;
		}
	}

	// Converts array to user
	public static User userToArray(ArrayList<String> arr) {

		Customer output = new Customer();

		output.setUserId(arr.get(0));
		output.setFirstName(arr.get(1));
		output.setLastName(arr.get(2));
		output.setUserName(arr.get(3));
		output.setPassword(arr.get(4));
		output.setAddress(arr.get(5));
		output.setZipcode(arr.get(6));
		output.setCity(arr.get(7));
		output.setState(arr.get(8));
		output.setPhone(arr.get(9));
		output.setEmail(arr.get(10));
		output.setStatus(arr.get(11));
		output.setsSN(arr.get(12));
		output.setSecurityQuestion(arr.get(13));
		output.setSecurityAnswer(arr.get(14));

		return output;
	}

	public static String getPassword(String username) {
		String password = Queries.SELECT("password", "`world`.`customer_info`", username);
		return password;

	}

	// method to book a flight
	/*
	 * public static void bookFlight(String a, String b, String c) { String
	 * selectedflight = "null";
	 * 
	 * String destination = a; String date = b; String time = c;
	 * DBQueries.viewFlights(a, b, c);
	 * 
	 * for (int i = 0; i < flights.length; i++) {
	 * 
	 * if (flights[i].)
	 * 
	 * bookFlight();//do i need to add this break; }
	 * 
	 * else {
	 * 
	 * Customer.addflight(selectedflight); } //add flight to user flight list //need
	 * to add
	 * 
	 * }
	 * 
	 * 
	 * //method to check if flight if full public static boolean isFull(Flight a) {
	 * 
	 * //check to see if seating is full
	 * 
	 * if ( a.getPassengerCount() == 10){ return true; } else return false; }
	 * 
	 * 
	 * //cannot book flight more than once, Method to check if flight if booked
	 * public static boolean isBooked(String a) { boolean alreadybooked = false;
	 * 
	 * for (int i = 0; i < flights.length;i++ )//is this wrong code, I want the
	 * length of the get flights array to iterate thru it. if
	 * (flights[i].equals(a)); alreadybooked = true;
	 * 
	 * return false; }
	 * 
	 * //delete booked flight from flight array public void deleteBooked(String a) {
	 * 
	 * for (int i = 0; i < flights.length;i++ ) if (flights[i].equals(a)) {
	 * flights[i] = null; removePassenger.subtract(1);
	 * 
	 * }
	 * 
	 * public void deleteFlight(Flight id) { .getConnection(); } public void
	 * insertFlight(Flight id) { getConnection() } public void updateFlight(Flight
	 * id) { INSERT(id); } private void INSERT(Flight id) { // TODO Auto-generated
	 * method stub
	 * 
	 * }
	 * 
	 * //Authenticates Username and password public static boolean validLogin throws
	 * Exception(String a, String b) {
	 * 
	 * boolean validUsername; boolean validPassword; try { ArrayList<String>
	 * usernames = Queries.GETCOLUMN("username", "`world`.`customer_info`"); for
	 * (int i = 0; i < usernames.size(); i++) { if (a.equals(usernames.get(i))) {
	 * validUsername = true; break; } } if (validUsername) { ArrayList<String>
	 * passwords = Queries.GETCOLUMN("password", "`world`.`customer_info`"); for
	 * (int i = 0; i < passwords.size(); i++) { if (a.equals(passwords.get(i))) {
	 * validPassword = true; break; } } }
	 * 
	 * if (validPassword && validUsername) { return true; } else { return false; } }
	 * 
	 * catch(Exception ex) { System.out.println("Not found");
	 * System.out.println(ex); return null; }
	 * 
	 * }
	 * 
	 * //view a list of available flights public static Flight viewFlights(Customer
	 * a) throws Exception {
	 * 
	 * //create database connection String query = DBQueries.viewAllFlights();
	 * //this i sdumb { Connection con = ConnectionMethod.getConnection(); Statement
	 * stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query); while
	 * (rs.next()) { String airportName = rs.getString("airport_name"); String
	 * destination = rs.getString("destination");//THESE NEED TO BE FILLEDOUT int
	 * flightNumber = rs.getInt("flightNumber"); //WITH THE COLUMN NAMES int
	 * passengerCount = rs.getInt("passenger"); String date = rs.getString("date");
	 * String time = rs.getString("");
	 * 
	 * //need to display the information to the user.
	 * 
	 * } //catch exception } catch (Exception e ) {
	 * 
	 * //close connection } finally { //TODO } { stmt.close(); } } }
	 * 
	 * //insert a new flight into the database for booking public static void
	 * insertFlight(Connection con, Flight ) throws SQLException {
	 * 
	 * try { //create database connection Class.forName(" "); //what is this?
	 * Connection con = DriverManager.getConnection(" ", " ", " ");
	 * 
	 * //mysql query insert statement String query = "  ";
	 * 
	 * 
	 * // Inserting data in database with preparedstatement PreparedStatement
	 * preparedStmt = con.prepareStatement(query); preparedStmt.setString(1,
	 * Flight.getAirportName());//are these correct, like the int? or is flight is a
	 * string? preparedStmt.setDate(2, ); preparedStmt.setString(3,);
	 * preparedStmt.setString(4, ); preparedStmt.setDate(5, );
	 * 
	 * int x = stmt.executeUpdate(q1); if (x > 0)
	 * System.out.println("Successfully Inserted"); else
	 * System.out.println("Insert Failed");
	 * 
	 * } catch(Exception e) {
	 * 
	 * } finally //close connection {con.close(); } } }
	 */
}
