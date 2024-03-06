package application;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class Resolver {
	
@FXML
RadioButton rbtn1,rbtn2,rbtn3,rbtn4,rbtn5,rbtn6,rbtn7;

@FXML
TextField hnumber,hnumber1,fname,sname,refill_date;

@FXML
Label feedback;

static String host,user,pass;

static String hospnumber,firstname,lastname,date_refill;


@FXML
private void initialize()
{
this.feedback.setVisible(false);	

}
private void alertOnComplete(int rows)
{
	final int rowsAffected = rows;
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(rowsAffected + " row(s) affected in update");
		alert.show();
		feedback.setVisible(false);
		}});


}

protected void executeQuery()
{
 
 if(this.rbtn1.isSelected()) {
	if(hnumber.getText().equals("") || hnumber.getText().isEmpty())
	{
		Alert info = new Alert(AlertType.INFORMATION);
		info.setHeaderText("Please supply a hospital Number");
		info.show();
		return;
	}
	hospnumber = hnumber.getText();
	this.feedback.setVisible(true);
	Thread runner = new Thread(new Runnable() {

		@Override
		public void run() {
			Connection insertcon;
			try {
				insertcon = DriverManager.getConnection(host,user,pass);
				PreparedStatement prepstmt = insertcon.prepareStatement(Queries.ISSUE1);
				prepstmt.setString(1, "%" + hospnumber + "%");
				int rows = prepstmt.executeUpdate();
				alertOnComplete(rows);
				insertcon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}});
	runner.start();
 };
 if(this.rbtn2.isSelected()) {
	 if(fname.getText().equals("") || fname.getText().isEmpty())
		{
			Alert info = new Alert(AlertType.INFORMATION);
			info.setHeaderText("Please supply first name");
			info.show();
			return;
		};
		if(sname.getText().equals("") || sname.getText().isEmpty())
		{
			Alert info = new Alert(AlertType.INFORMATION);
			info.setHeaderText("Please supply last name");
			info.show();
			return;
		}
		firstname = fname.getText();
		lastname = sname.getText();
		this.feedback.setVisible(true);
		Thread runner = new Thread(new Runnable() {

			@Override
			public void run() {
				Connection insertcon;
				try {
					insertcon = DriverManager.getConnection(host,user,pass);
					PreparedStatement prepstmt = insertcon.prepareStatement(Queries.ISSUE2);
					prepstmt.setString(1, "%"+firstname+"%");
					prepstmt.setString(2, "%"+lastname+"%");
					int rows = prepstmt.executeUpdate();
					alertOnComplete(rows);
					insertcon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}});
		runner.start();
 };
 if(this.rbtn3.isSelected()) {
	 this.feedback.setVisible(true);
		Thread runner = new Thread(new Runnable() {

			@Override
			public void run() {
				Connection insertcon;
				try {
					insertcon = DriverManager.getConnection(host,user,pass);
					PreparedStatement prepstmt = insertcon.prepareStatement(Queries.ISSUE3);
					int rows = prepstmt.executeUpdate();
					alertOnComplete(rows);
					insertcon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}});
		runner.start();
 };
 if(this.rbtn4.isSelected()) {
	 this.feedback.setVisible(true);
		Thread runner = new Thread(new Runnable() {

			@Override
			public void run() {
				Connection insertcon;
				try {
					insertcon = DriverManager.getConnection(host,user,pass);
					PreparedStatement prepstmt = insertcon.prepareStatement(Queries.ISSUE4);
					int rows = prepstmt.executeUpdate();
					alertOnComplete(rows);
					insertcon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}});
		runner.start();
 };
 if(this.rbtn5.isSelected()) {
	 if(hnumber1.getText().equals("") || hnumber1.getText().isEmpty())
		{
			Alert info = new Alert(AlertType.INFORMATION);
			info.setHeaderText("Please supply hospital number");
			info.show();
			return;
		};
		if(refill_date.getText().equals("") || refill_date.getText().isEmpty())
		{
			Alert info = new Alert(AlertType.INFORMATION);
			info.setHeaderText("Please supply date of refill");
			info.show();
			return;
		}
		hospnumber = hnumber1.getText();
		date_refill = refill_date.getText();
	 this.feedback.setVisible(true);
		Thread runner = new Thread(new Runnable() {

			@Override
			public void run() {
				Connection insertcon;
				try {
					insertcon = DriverManager.getConnection(host,user,pass);
					PreparedStatement prepstmt = insertcon.prepareStatement(Queries.ISSUE5);
					prepstmt.setString(1, "%"+hospnumber+"%");
					prepstmt.setDate(2, java.sql.Date.valueOf(date_refill));
					int rows = prepstmt.executeUpdate();
					alertOnComplete(rows);
					insertcon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}});
		runner.start();
 };
 if(this.rbtn6.isSelected()) {
	 this.feedback.setVisible(true);
		Thread runner = new Thread(new Runnable() {

			@Override
			public void run() {
				Connection insertcon;
				try {
					insertcon = DriverManager.getConnection(host,user,pass);
					PreparedStatement prepstmt = insertcon.prepareStatement(Queries.ISSUE6);
					int rows = prepstmt.executeUpdate();
					alertOnComplete(rows);
					insertcon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}});
		runner.start();
 };
 if(this.rbtn7.isSelected()) {
	 this.feedback.setVisible(true);
		Thread runner = new Thread(new Runnable() {

			@Override
			public void run() {
				Connection insertcon;
				try {
					insertcon = DriverManager.getConnection(host,user,pass);
					PreparedStatement prepstmt = insertcon.prepareStatement(Queries.ISSUE7);
					int rows = prepstmt.executeUpdate();
					alertOnComplete(rows);
					insertcon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}});
		runner.start();
 };
 


}
public void getDatabaseCredentials()
{
	try {
	try(InputStream is = new FileInputStream("application.properties"))
    {
	Properties prop = new Properties();
	prop.load(is);
	host = prop.getProperty("db.url");
	user = prop.getProperty("db.user");
	pass = prop.getProperty("db.pass");
    }
	
	}
	catch(Exception ex)
	{
		System.out.println(ex.getMessage());
	}

}
@FXML
private void OnResolveButtonClicked()
{
	this.getDatabaseCredentials();
	this.executeQuery();
}
}
