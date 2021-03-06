package application;

import java.sql.Connection;
import java.sql.ResultSet;

import common.Action;
import common.Customer;
import common.User;
import database.Queries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;

public class UserFlights implements ControlledScreen {

	// Variables
	ScreensController myController;

	@FXML
	private TableView<FlightDetails> tableFlight;
	@FXML
	private TableColumn<FlightDetails, String> columnFlightNumber;
	@FXML
	private TableColumn<FlightDetails, String> columnDeparture;
	@FXML
	private TableColumn<FlightDetails, String> columnDestination;
	@FXML
	private TableColumn<FlightDetails, String> columnDate;
	@FXML
	private TableColumn<FlightDetails, String> columnTime;
	@FXML
	private TableColumn<FlightDetails, String> columnPassengerCount;
	@FXML
	private TableColumn<FlightDetails, String> columnBook;
	@FXML
	private Button btnLoad;
	@FXML
	private Label result;
	@FXML
	private TextField input;

	public User currentUser;

	private ObservableList<FlightDetails> data;

	// Set the screen
	public void setScreenParent(ScreensController screenParent) {
		myController = screenParent;
	}

	// Takes user back to Login Page
	public void back(ActionEvent event) {

		myController.setScreen(Main.profilePageID);
	}

	// Takes user back to home page
	public void home(ActionEvent event) {

		myController.setScreen(Main.homePageID);

	}

	@FXML
	// Loads User Flights
	private void loadDataFromDatabase(ActionEvent event) throws Exception {

		currentUser = (Customer) (myController.getScreen("Customer"));

		try {
			Connection con = Queries.getConnection();
			data = FXCollections.observableArrayList();
			String tempQuer = "SELECT * FROM `world`.`flights`"
					+ "WHERE flight_number IN(SELECT flight_id FROM `world`.`reservations` WHERE cust_id = "
					+ currentUser.getUserId() + ")";
			ResultSet rs = con.createStatement().executeQuery(tempQuer);
			while (rs.next()) {
				data.add(new FlightDetails(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6)));
			}
		} catch (Exception ex) {
			System.out.println("Error" + ex);
		}

		columnFlightNumber.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
		columnDeparture.setCellValueFactory(new PropertyValueFactory<>("departure"));
		columnDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
		columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		columnTime.setCellValueFactory(new PropertyValueFactory<>("time"));
		columnPassengerCount.setCellValueFactory(new PropertyValueFactory<>("passengerCount"));

		tableFlight.setItems(null);
		tableFlight.setItems(data);

	}

	// Removes User from Flight
	public void removeFlight(ActionEvent e) {

		currentUser = (Customer) (myController.getScreen("Customer"));
		result.setText(Action.removeUserFlight(currentUser.getUserId(), input.getText()));
		if (result.getText().equals("Removed")) {
			result.setTextFill(Paint.valueOf("00CC00"));
		} else {
			result.setTextFill(Paint.valueOf("#FF0000"));
		}

	}

}
