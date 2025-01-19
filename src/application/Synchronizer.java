package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.print.attribute.standard.DateTimeAtCompleted;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Synchronizer implements Runnable {

private String url,user,pass,server,lastUpdate;
private HBox hbox;
Label description;
Connection insertcon;
int facility_id;
private int errorCount;
Hashtable<String,String> errors = new Hashtable<>();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.errorCount = 0;
		this.errors.clear();
		this.uploadPatientData();
		this.uploadVisitData();
		this.uploadTriageData();
		this.uploadEnrollmentData();
		this.uploadEncounterData();
		this.uploadPharmacyData();
		this.uploadObservationData();
		this.uploadBiometricData();
		this.uploadEacData();
		this.uploadEacSessionData();
		this.uploadSampleData();
		this.uploadTestData();
		this.uploadResultData();
		this.uploadStatusData();
		this.uploadPrepClinicData();
		this.uploadPrepEligibilityData();
		this.uploadPrepEnrollmentData();
		this.uploadPrepInterruptionData();
		this.uploadHTSClientData();
		this.uploadHTSElicitationData();
		this.uploadHTSStratificationData();
		this.uploadCaseManagerData();
		this.uploadCaseManagerPatientData();
		this.uploadDsdDevolvementData();
		this.alertOnComplete(this.errorCount);
		try {
			if(!insertcon.isClosed())
			{
				insertcon.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.logErrors();
		if(this.errorCount == 0)
		this.updatePropertyFile();
		
	}
public Synchronizer()
{
	
}
	
public Synchronizer(HBox hbox,Label description,int facility)
{
	this.hbox = hbox;
	this.description = description;
	this.facility_id = facility;
}
private void updatePropertyFile()
{
	DateFormat format = new SimpleDateFormat("YYYY-MM-dd");
	Date Now = Calendar.getInstance().getTime();
	String strDate = format.format(Now);
	
	try {
		try(OutputStream os = new FileOutputStream("upload.properties"))
	    {
			Properties prop = new Properties();
			prop.setProperty("lastupdate", strDate);
			prop.store(os, null);
			    	
	    }}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
}
private void logErrors()
{
String strnow = new SimpleDateFormat("YYYY-MM-dd:HH:mm:ss").format(Calendar.getInstance().getTime());
if(errors.size() > 0)
{
try {
Writer wr = new FileWriter("application.debug",true);
errors.forEach((key,value)->{
String entry = String.format("%s: %s ---- %s\n",strnow,key,value);
try {
	wr.write(entry);
	
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

});
wr.close();
}
catch(Exception ex)
{
	
}
}

}
public void getDatabaseCredentials()
{
	try {
	try(InputStream is = new FileInputStream("application.properties"))
    {
	Properties prop = new Properties();
	prop.load(is);
	url = prop.getProperty("db.url");
	user = prop.getProperty("db.user");
	pass = prop.getProperty("db.pass");
	server = prop.getProperty("db.remote");
	lastUpdate = prop.getProperty("lastupdate");
		    	
    }
	
	try(InputStream istream = new FileInputStream("upload.properties"))
    {
	Properties prop = new Properties();
	prop.load(istream);
	lastUpdate = prop.getProperty("lastupdate");
		    	
    }
	
	
	}
	catch(Exception ex)
	{
		System.out.println(ex.getMessage());
	}

}

	
public void uploadPatientData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Patient Data......................");
			
		}});
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, active, contact_point, address, gender, identifier, deceased, deceased_date_time, marital_status, employment_status, education, organization, contact, date_of_birth, date_of_registration, archived, facility_id, uuid, nin_number, emr_id, first_name, sex, surname, other_name, hospital_number, is_date_of_birth_estimated, full_name, case_manager_id\r\n"
			+ "	FROM patient_person where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.patient_person(\r\n"
			+ "	created_date, created_by, last_modified_date, last_modified_by, active, contact_point, address, gender, identifier, deceased, deceased_date_time, marital_status, employment_status, education, organization, contact, date_of_birth, date_of_registration, archived, facility_id, uuid, nin_number, emr_id, first_name, sex, surname, other_name, hospital_number, is_date_of_birth_estimated, full_name, case_manager_id)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Patient Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDate(1, rs.getDate(1));
		         prepstmt.setString(2, rs.getString(2));
		         prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setBoolean(5, rs.getBoolean(5));
		         prepstmt.setObject(6, rs.getObject(6));
		         prepstmt.setObject(7, rs.getObject(7));
		         prepstmt.setObject(8, rs.getObject(8));
		         prepstmt.setObject(9, rs.getObject(9));
		         prepstmt.setBoolean(10, rs.getBoolean(10));
		         prepstmt.setDate(11, rs.getDate(11));
		         prepstmt.setObject(12, rs.getObject(12));
		         prepstmt.setObject(13, rs.getObject(13));
		         prepstmt.setObject(14, rs.getObject(14));
		         prepstmt.setObject(15, rs.getObject(15));
		         prepstmt.setObject(16, rs.getObject(16));
		         prepstmt.setDate(17, rs.getDate(17));
		         prepstmt.setDate(18, rs.getDate(18));
		         prepstmt.setInt(19, rs.getInt(19));
		         prepstmt.setInt(20, rs.getInt(20));
		         prepstmt.setString(21, rs.getString(21));
		         prepstmt.setString(22, rs.getString(22));
		         prepstmt.setString(23, rs.getString(23));
		         prepstmt.setString(24, rs.getString(24));
		         prepstmt.setString(25, rs.getString(25));
		         prepstmt.setString(26, rs.getString(26));
		         prepstmt.setString(27, rs.getString(27));
		         prepstmt.setString(28, rs.getString(28));
		         prepstmt.setBoolean(29, rs.getBoolean(29));
		         prepstmt.setString(30, rs.getString(30));
		         prepstmt.setInt(31, rs.getInt(31));
		         prepstmt.addBatch();
		         
		       }
		       try {
		       prepstmt.executeBatch();
		       }
		       catch(Exception ex)
		       {
		    	System.out.println(ex.getMessage());	
		    	errors.put("Patient Data", ex.getMessage());
		    	this.errorCount++;
		       }
		       rs.close();
		       
		       con.close();
		       //insertcon.close();
		    }
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Patient Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}

public void uploadVisitData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Visit Data......................");
			
		}});
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, person_uuid, visit_start_date, visit_end_date, uuid, archived\r\n"
			+ "	FROM public.patient_visit where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.patient_visit(\r\n"
			+ "	created_date, created_by, last_modified_date, last_modified_by, facility_id, person_uuid, visit_start_date, visit_end_date, uuid, archived)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Visit Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDate(1, rs.getDate(1));
		         prepstmt.setString(2, rs.getString(2));
		         prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setInt(5, rs.getInt(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setDate(7, rs.getDate(7));
		         prepstmt.setDate(8, rs.getDate(8));
		         prepstmt.setString(9, rs.getString(9));
		         prepstmt.setInt(10, rs.getInt(10));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Visit Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());
	errors.put("Visit Data", ex.getMessage());
	this.errorCount++;	
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadTriageData()
{
	
	
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Triage Data......................");
			
		}});
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, body_weight, diastolic, capture_date, height, temperature, pulse, respiratory_rate, person_uuid, visit_id, systolic, archived, uuid\r\n"
			+ "	FROM public.triage_vital_sign where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.triage_vital_sign(\r\n"
			+ "	created_date, created_by, last_modified_date, last_modified_by, facility_id, body_weight, diastolic, capture_date, height, temperature, pulse, respiratory_rate, person_uuid, visit_id, systolic, archived, uuid)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Triage Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDate(1, rs.getDate(1));
		         prepstmt.setString(2, rs.getString(2));
		         prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setInt(5, rs.getInt(5));
		         prepstmt.setDouble(6, rs.getDouble(6));
		         prepstmt.setDouble(7, rs.getDouble(7));
		         prepstmt.setDate(8, rs.getDate(8));
		         prepstmt.setDouble(9, rs.getDouble(9));
		         prepstmt.setDouble(10, rs.getDouble(10));
		         prepstmt.setDouble(11, rs.getDouble(11));
		         prepstmt.setDouble(12, rs.getDouble(12));
		         prepstmt.setString(13, rs.getString(13));
		         prepstmt.setString(14, rs.getString(14));
		         prepstmt.setDouble(15, rs.getDouble(15));
		         prepstmt.setInt(16, rs.getInt(16));
		         prepstmt.setString(17, rs.getString(17));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Triage Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Triage Data", ex.getMessage());
	this.errorCount++;
	}
	//this.updatePropertyFile();
	this.updateProgressBar();
}
public void uploadEnrollmentData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Enrollment Data......................");
			
		}});
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, unique_id, entry_point_id, target_group_id, date_confirmed_hiv, date_enrolled_pmtct, source_of_referrer_id, time_hiv_diagnosis, pregnant, breastfeeding, date_of_registration, status_at_registration_id, enrollment_setting_id, date_started, send_message, person_uuid, visit_id, uuid, archived, facility_name, ovc_number, house_hold_number, care_entry_point_other, referred_to_ovcpartner, referred_from_ovcpartner, date_referred_to_ovcpartner, date_referred_from_ovcpartner, date_of_lpm, pregnancy_status_id, tb_status_id, lip_name\r\n"
			+ "	FROM public.hiv_enrollment where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hiv_enrollment(\r\n"
			+ "	created_date, created_by, last_modified_date, last_modified_by, facility_id, unique_id, entry_point_id, target_group_id, date_confirmed_hiv, date_enrolled_pmtct, source_of_referrer_id, time_hiv_diagnosis, pregnant, breastfeeding, date_of_registration, status_at_registration_id, enrollment_setting_id, date_started, send_message, person_uuid, visit_id, uuid, archived, facility_name, ovc_number, house_hold_number, care_entry_point_other, referred_to_ovcpartner, referred_from_ovcpartner, date_referred_to_ovcpartner, date_referred_from_ovcpartner, date_of_lpm, pregnancy_status_id, tb_status_id, lip_name)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Enrollment Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDate(1, rs.getDate(1));
		         prepstmt.setString(2, rs.getString(2));
				 prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setInt(5, rs.getInt(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setInt(7, rs.getInt(7));
		         prepstmt.setInt(8, rs.getInt(8));
		         prepstmt.setDate(9, rs.getDate(9));
		         prepstmt.setDate(10, rs.getDate(10));
		         prepstmt.setInt(11, rs.getInt(11));
		         prepstmt.setDate(12, rs.getDate(12));
		         prepstmt.setBoolean(13, rs.getBoolean(13));
		         prepstmt.setBoolean(14, rs.getBoolean(14));
		         prepstmt.setDate(15, rs.getDate(15));
		         prepstmt.setInt(16, rs.getInt(16));
		         prepstmt.setInt(17, rs.getInt(17));
		         prepstmt.setDate(18, rs.getDate(18));
		         prepstmt.setBoolean(19, rs.getBoolean(19));
		         prepstmt.setString(20, rs.getString(21));
		         prepstmt.setString(21, rs.getString(21));
		         prepstmt.setString(22, rs.getString(22));
		         prepstmt.setInt(23, rs.getInt(23));
		         prepstmt.setString(24, rs.getString(24));
		         prepstmt.setString(25, rs.getString(25));
		         prepstmt.setString(26, rs.getString(26));
		         prepstmt.setString(27, rs.getString(27));
		         prepstmt.setString(28, rs.getString(28));
		         prepstmt.setString(29, rs.getString(29));
		         prepstmt.setDate(30, rs.getDate(30));
		         prepstmt.setDate(31, rs.getDate(31));
		         prepstmt.setDate(32, rs.getDate(32));
		         prepstmt.setInt(33, rs.getInt(33));
		         prepstmt.setInt(34, rs.getInt(34));
		         prepstmt.setString(35, rs.getString(35));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Enrollment Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Enrollment Data", ex.getMessage());
	this.errorCount++;
	}
	//this.updatePropertyFile();
	this.updateProgressBar();
}
public void uploadEncounterData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Encounter Data......................");
			
		}});
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, visit_date, cd_4, cd_4_percentage, is_commencement, functional_status_id, clinical_stage_id, clinical_note, uuid, hiv_enrollment_uuid, regimen_id, regimen_type_id, art_status_id, archived, vital_sign_uuid, who_staging_id, person_uuid, visit_id, oi_screened, sti_ids, pregnancy_status, sti_treated, opportunistic_infections, adr_screened, adverse_drug_reactions, adherence_level, adheres, next_appointment, lmp_date, tb_screen, is_viral_load_at_start_of_art, viral_load_at_start_of_art, date_of_viral_load_at_start_of_art, cryptococcal_screening_status, cervical_cancer_screening_status, cervical_cancer_treatment_provided, hepatitis_screening_result, family_planing, on_family_planing, level_of_adherence, tb_status, tb_prevention, arvdrugs_regimen, viral_load_order, cd4_count, cd4_semi_quantitative, cd4_flow_cytometry, extra, cd4_type,reason,who\r\n"
			+ "	FROM public.hiv_art_clinical where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hiv_art_clinical(\r\n"
			+ "	created_date, created_by, last_modified_date, last_modified_by, facility_id, visit_date, cd_4, cd_4_percentage, is_commencement, functional_status_id, clinical_stage_id, clinical_note, uuid, hiv_enrollment_uuid, regimen_id, regimen_type_id, art_status_id, archived, vital_sign_uuid, who_staging_id, person_uuid, visit_id, oi_screened, sti_ids, pregnancy_status, sti_treated, opportunistic_infections, adr_screened, adverse_drug_reactions, adherence_level, adheres, next_appointment, lmp_date, tb_screen, is_viral_load_at_start_of_art, viral_load_at_start_of_art, date_of_viral_load_at_start_of_art, cryptococcal_screening_status, cervical_cancer_screening_status, cervical_cancer_treatment_provided, hepatitis_screening_result, family_planing, on_family_planing, level_of_adherence, tb_status, tb_prevention, arvdrugs_regimen, viral_load_order, cd4_count, cd4_semi_quantitative, cd4_flow_cytometry, extra, cd4_type,reason,who)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Encounter Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDate(1, rs.getDate(1));
		         prepstmt.setString(2, rs.getString(2));
				 prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setInt(5, rs.getInt(5));
		         prepstmt.setDate(6, rs.getDate(6));
		         prepstmt.setInt(7, rs.getInt(7));
		         prepstmt.setInt(8, rs.getInt(8));
		         prepstmt.setBoolean(9, rs.getBoolean(9));
		         prepstmt.setInt(10, rs.getInt(10));
		         prepstmt.setInt(11, rs.getInt(11));
		         prepstmt.setString(12, rs.getString(12));
		         prepstmt.setString(13, rs.getString(13));
		         prepstmt.setString(14, rs.getString(14));
		         prepstmt.setInt(15, rs.getInt(15));
		         prepstmt.setInt(16, rs.getInt(16));
		         prepstmt.setInt(17, rs.getInt(17));
		         prepstmt.setInt(18, rs.getInt(18));
		         prepstmt.setString(19, rs.getString(19));
		         prepstmt.setInt(20, rs.getInt(20));
		         prepstmt.setString(21, rs.getString(21));
		         prepstmt.setString(22, rs.getString(22));
		         prepstmt.setString(23, rs.getString(23));
		         prepstmt.setString(24, rs.getString(24));
		         prepstmt.setString(25, rs.getString(25));
		         prepstmt.setString(26, rs.getString(26));
		         prepstmt.setObject(27, rs.getObject(27));
		         prepstmt.setString(28, rs.getString(28));
		         prepstmt.setObject(29, rs.getObject(29));
		         prepstmt.setString(30, rs.getString(30));
		         prepstmt.setObject(31, rs.getObject(31));
		         prepstmt.setDate(32, rs.getDate(32));
		         prepstmt.setDate(33, rs.getDate(33));
		         prepstmt.setObject(34, rs.getObject(34));
		         prepstmt.setBoolean(35, rs.getBoolean(35));
		         prepstmt.setDouble(36, rs.getDouble(36));
		         prepstmt.setDate(37, rs.getDate(37));
		         prepstmt.setString(38, rs.getString(38));
		         prepstmt.setString(39, rs.getString(39));
		         prepstmt.setString(40, rs.getString(40));
		         prepstmt.setString(41, rs.getString(41));
		         prepstmt.setString(42, rs.getString(42));
		         prepstmt.setString(43, rs.getString(43));
		         prepstmt.setString(44, rs.getString(44));
		         prepstmt.setString(45, rs.getString(45));
		         prepstmt.setString(46, rs.getString(46));
		         prepstmt.setObject(47, rs.getObject(47));
		         prepstmt.setObject(48, rs.getObject(48));
		         prepstmt.setString(49, rs.getString(49));
		         prepstmt.setString(50, rs.getString(50));
		         prepstmt.setInt(51, rs.getInt(51));
		         prepstmt.setObject(52, rs.getObject(52));
		         prepstmt.setString(53, rs.getString(53));
		         prepstmt.setString(54, rs.getString(54));
		         prepstmt.setObject(55, rs.getObject(55));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Clinic Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		      // insertcon.close();
		      
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Clinic Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadPharmacyData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Pharmacy Data......................");
			
		}});
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, visit_id, person_uuid, visit_date, ard_screened, prescription_error, adherence, mmd_type, uuid, next_appointment, extra, adverse_drug_reactions, is_devolve, refill_period, dsd_model_type, refill, refill_type, delivery_point, dsd_model, archived, ipt_type, ipt, visit_type\r\n"
			+ "	FROM public.hiv_art_pharmacy where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hiv_art_pharmacy(\r\n"
			+ "	created_date, created_by, last_modified_date, last_modified_by, facility_id, visit_id, person_uuid, visit_date, ard_screened, prescription_error, adherence, mmd_type, uuid, next_appointment, extra, adverse_drug_reactions, is_devolve, refill_period, dsd_model_type, refill, refill_type, delivery_point, dsd_model, archived, ipt_type, ipt, visit_type)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Pharmacy Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDate(1, rs.getDate(1));
		         prepstmt.setString(2, rs.getString(2));
				 prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setInt(5, rs.getInt(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setDate(8, rs.getDate(8));
		         prepstmt.setBoolean(9, rs.getBoolean(9));
		         prepstmt.setBoolean(10, rs.getBoolean(10));
		         prepstmt.setBoolean(11, rs.getBoolean(11));
		         prepstmt.setString(12, rs.getString(12));
		         prepstmt.setString(13, rs.getString(13));
		         prepstmt.setDate(14, rs.getDate(14));
		         prepstmt.setObject(15, rs.getObject(15));
		         prepstmt.setObject(16, rs.getObject(16));
		         prepstmt.setBoolean(17, rs.getBoolean(17));
		         prepstmt.setInt(18, rs.getInt(18));
		         prepstmt.setString(19, rs.getString(19));
		         prepstmt.setString(20, rs.getString(20));
		         prepstmt.setString(21, rs.getString(21));
		         prepstmt.setString(22, rs.getString(22));
		         prepstmt.setString(23, rs.getString(23));
		         prepstmt.setInt(24, rs.getInt(24));
		         prepstmt.setString(25, rs.getString(25));
		         prepstmt.setObject(26, rs.getObject(26));
		         prepstmt.setString(27, rs.getString(27));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Pharmacy Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Pharmacy Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
private void updateProgressBar()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(false);
			
		}});
   	

}
private void alertOnComplete(int err)
{
	
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		Alert alert = new Alert(AlertType.INFORMATION);
		if(err > 0)
		alert.setHeaderText("Uploads Completed with errors");
		else 
		//alert.setHeaderText(lastUpdate);
		alert.setHeaderText("Uploads Completed");
		alert.show();
			
		}}
	);


}

public void uploadObservationData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Observation Data......................");
			
		}});
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, date_of_observation, person_uuid, visit_id, type, uuid, data, archived\r\n"
			+ "	FROM public.hiv_observation where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hiv_observation(\r\n"
			+ "	created_date, created_by, last_modified_date, last_modified_by, facility_id, date_of_observation, person_uuid, visit_id, type, uuid, data, archived)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Observation Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDate(1, rs.getDate(1));
		         prepstmt.setString(2, rs.getString(2));
				 prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setInt(5, rs.getInt(5));
		         prepstmt.setDate(6, rs.getDate(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setString(9, rs.getString(9));
		         prepstmt.setString(10, rs.getString(10));
		         prepstmt.setObject(11, rs.getObject(11));
		         prepstmt.setInt(12, rs.getInt(12));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Observation Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Observation Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadBiometricData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Biometric Data......................");
			
		}});
	String sql = "SELECT  id,created_date, created_by, last_modified_date, last_modified_by, facility_id, person_uuid, template, biometric_type, template_type, enrollment_date, archived, iso, extra, device_name, reason, version_iso_20, image_quality, recapture, recapture_message, hashed, count,replace_date,match_type,match_biometric_id,match_person_uuid\r\n"
			+ "	FROM public.biometric where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.biometric(\r\n"
			+ "	id, created_date, created_by, last_modified_date, last_modified_by, facility_id, person_uuid, template, biometric_type, template_type, enrollment_date, archived, iso, extra, device_name, reason, version_iso_20, image_quality, recapture, recapture_message, hashed, count,replace_date,match_type,match_biometric_id,match_person_uuid)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Biometric Data......................");
					
				}});
		       while (rs.next()) {
		         prepstmt.setString(1, rs.getString(1));
		         prepstmt.setDate(2, rs.getDate(2));
		         prepstmt.setString(3, rs.getString(3));
				 prepstmt.setDate(4, rs.getDate(4));
		         prepstmt.setString(5, rs.getString(5));
		         prepstmt.setInt(6, rs.getInt(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setBytes(8, rs.getBytes(8));
		         prepstmt.setString(9, rs.getString(9));
		         prepstmt.setString(10, rs.getString(10));
		         prepstmt.setDate(11, rs.getDate(11));
		         prepstmt.setInt(12, rs.getInt(12));
		         prepstmt.setBoolean(13, rs.getBoolean(13));
		         prepstmt.setObject(14, rs.getObject(14));
		         prepstmt.setString(15, rs.getString(15));
		         prepstmt.setString(16, rs.getString(16));
		         prepstmt.setBoolean(17, rs.getBoolean(17));
		         prepstmt.setInt(18, rs.getInt(18));
		         prepstmt.setInt(19, rs.getInt(19));
		         prepstmt.setString(20, rs.getString(20));
		         prepstmt.setString(21, rs.getString(21));
		         prepstmt.setInt(22, rs.getInt(22));
		         prepstmt.setDate(23, rs.getDate(23));
		         prepstmt.setString(24, rs.getString(24));
		         prepstmt.setString(25, rs.getString(25));
		         prepstmt.setString(26, rs.getString(26));
		         //prepstmt.setString(27, rs.getString(27));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Biometric Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Biometric Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadEacData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching EAC Data......................");
			
		}});
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, person_uuid, visit_id, last_viral_load, date_of_last_viral_load, uuid, status, archived, test_result_id, test_group, test_name, lab_number, reason_to_stop_eac\r\n"
			+ "	FROM public.hiv_eac where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hiv_eac(\r\n"
			+ "	created_date, created_by, last_modified_date, last_modified_by, facility_id, person_uuid, visit_id, last_viral_load, date_of_last_viral_load, uuid, status, archived, test_result_id, test_group, test_name, lab_number, reason_to_stop_eac)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading EAC Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDate(1, rs.getDate(1));
		         prepstmt.setString(2, rs.getString(2));
				 prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setInt(5, rs.getInt(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setDouble(8, rs.getDouble(8));
		         prepstmt.setDate(9, rs.getDate(9));
		         prepstmt.setString(10, rs.getString(10));
		         prepstmt.setString(11, rs.getString(11));
		         prepstmt.setInt(12, rs.getInt(12));
		         prepstmt.setInt(13, rs.getInt(13));
		         prepstmt.setString(14, rs.getString(14));
		         prepstmt.setString(15, rs.getString(15));
		         prepstmt.setString(16, rs.getString(16));
		         prepstmt.setString(17, rs.getString(17));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("eac Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("eac Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadEacSessionData()
{
	
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching EAC_SESSION Data......................");
			
		}});
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, eac_id, person_uuid, visit_id, barriers, intervention, barriers_others, intervention_others, comment, follow_up_date, eac_session_date, referral, adherence, uuid, status, archived\r\n"
			+ "	FROM public.hiv_eac_session where last_modified_date >= '"+this.lastUpdate+"'"+" and person_uuid in (select uuid from patient_person where facility_id ="+this.facility_id+")";
	String insertsql = "INSERT INTO public.hiv_eac_session(\r\n"
			+ "	created_date, created_by, last_modified_date, last_modified_by, facility_id, eac_id, person_uuid, visit_id, barriers, intervention, barriers_others, intervention_others, comment, follow_up_date, eac_session_date, referral, adherence, uuid, status, archived)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading EAC session Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDate(1, rs.getDate(1));
		         prepstmt.setString(2, rs.getString(2));
				 prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setInt(5, rs.getInt(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setObject(9, rs.getObject(9));
		         prepstmt.setObject(10, rs.getObject(10));
		         prepstmt.setString(11, rs.getString(11));
		         prepstmt.setString(12, rs.getString(12));
		         prepstmt.setString(13, rs.getString(13));
		         prepstmt.setDate(14, rs.getDate(14));
		         prepstmt.setDate(15, rs.getDate(15));
		         prepstmt.setString(16, rs.getString(16));
		         prepstmt.setString(17, rs.getString(17));
		         prepstmt.setString(18, rs.getString(18));
		         prepstmt.setString(19, rs.getString(19));
		         prepstmt.setInt(20, rs.getInt(20));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("eac_session Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("eac_session Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadSampleData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Sample Data......................");
			
		}});
	String sql = "SELECT uuid, sample_number, sample_type_id, sample_collection_mode, date_sample_collected, comment_sample_collected, sample_collected_by, date_sample_verified, comment_sample_verified, sample_verified_by, sample_accepted, patient_uuid, facility_id, patient_id, test_id, date_sample_logged_remotely, sample_logged_remotely, created_by, date_created, modified_by, date_modified, archived\r\n"
			+ "	FROM public.laboratory_sample where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.laboratory_sample(\r\n"
			+ "	 uuid, sample_number, sample_type_id, sample_collection_mode, date_sample_collected, comment_sample_collected, sample_collected_by, date_sample_verified, comment_sample_verified, sample_verified_by, sample_accepted, patient_uuid, facility_id, patient_id, test_id, date_sample_logged_remotely, sample_logged_remotely, created_by, date_created, modified_by, date_modified, archived)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		       insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Sample Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setString(1, rs.getString(1));
				 prepstmt.setString(2, rs.getString(2));
		         prepstmt.setInt(3, rs.getInt(3));
		         prepstmt.setInt(4, rs.getInt(4));
		         prepstmt.setDate(5, rs.getDate(5));
		         prepstmt.setString(6, rs.getString(6));
				 prepstmt.setString(7, rs.getString(7));
				 prepstmt.setDate(8, rs.getDate(8));
				 prepstmt.setString(9, rs.getString(9));
				 prepstmt.setString(10, rs.getString(10));
				 prepstmt.setString(11, rs.getString(11));
				 prepstmt.setString(12, rs.getString(12));
				 prepstmt.setInt(13, rs.getInt(13));
		         prepstmt.setInt(14, rs.getInt(14));
		         prepstmt.setInt(15, rs.getInt(15));
		         prepstmt.setDate(16, rs.getDate(16));
		         prepstmt.setInt(17, rs.getInt(17));
		         prepstmt.setString(18, rs.getString(18));
		         prepstmt.setDate(19, rs.getDate(19));
		         prepstmt.setString(20, rs.getString(20));
		         prepstmt.setDate(21, rs.getDate(21));
		         prepstmt.setInt(22, rs.getInt(22));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Test sample Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());
	errors.put("Test sample Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadTestData()
{
	
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Test Data......................");
			
		}});
	String sql = "SELECT uuid, patient_id, lab_test_id, description, lab_number, lab_test_group_id, order_priority, unit_measurement, lab_test_order_status, viral_load_indication, patient_uuid, facility_id, lab_order_id, created_by, date_created, modified_by, date_modified, archived,clinical_note\r\n"
			+ "	FROM public.laboratory_test where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.laboratory_test(\r\n"
			+ "	 uuid, patient_id, lab_test_id, description, lab_number, lab_test_group_id, order_priority, unit_measurement, lab_test_order_status, viral_load_indication, patient_uuid, facility_id, lab_order_id, created_by, date_created, modified_by, date_modified, archived,clinical_note)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Test Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         
		         prepstmt.setString(1, rs.getString(1));
		         prepstmt.setInt(2, rs.getInt(2));
		         prepstmt.setInt(3, rs.getInt(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setString(5, rs.getString(5));
		         prepstmt.setInt(6, rs.getInt(6));
		         prepstmt.setInt(7, rs.getInt(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setInt(9, rs.getInt(9));
		         prepstmt.setInt(10, rs.getInt(10));
		         prepstmt.setString(11, rs.getString(11));
		         prepstmt.setInt(12, rs.getInt(12));
		         prepstmt.setInt(13, rs.getInt(13));
		         prepstmt.setString(14, rs.getString(14));
		         prepstmt.setDate(15, rs.getDate(15));
		         prepstmt.setString(16, rs.getString(16));
		         prepstmt.setDate(17, rs.getDate(17));
		         prepstmt.setInt(18, rs.getInt(18));
		         prepstmt.setString(19, rs.getString(19));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Test Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());
	errors.put("Test Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadResultData()
{
	
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Test Result Data......................");
			
		}});
	String sql = "SELECT uuid, date_assayed, date_result_reported, result_reported, result_report, result_reported_by, patient_uuid, facility_id, patient_id, pcr_lab_sample_number, date_sample_received_at_pcr_lab, test_id, date_checked, checked_by, date_result_received, created_by, date_created, modified_by, date_modified, archived, assayed_by, approved_by, date_approved, result_received_by, pcr_lab_name\r\n"
			+ "	FROM public.laboratory_result where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.laboratory_result(\r\n"
			+ "	 uuid, date_assayed, date_result_reported, result_reported, result_report, result_reported_by, patient_uuid, facility_id, patient_id, pcr_lab_sample_number, date_sample_received_at_pcr_lab, test_id, date_checked, checked_by, date_result_received, created_by, date_created, modified_by, date_modified, archived, assayed_by, approved_by, date_approved, result_received_by, pcr_lab_name)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Result Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         
		         prepstmt.setString(1, rs.getString(1));
				 prepstmt.setDate(2, rs.getDate(2));
				 prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setString(5, rs.getString(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setInt(8, rs.getInt(8));
		         prepstmt.setInt(9, rs.getInt(9));
		         prepstmt.setString(10, rs.getString(10));
		         prepstmt.setDate(11, rs.getDate(11));
		         prepstmt.setInt(12, rs.getInt(12));
		         prepstmt.setDate(13, rs.getDate(13));
		         prepstmt.setString(14, rs.getString(14));
		         prepstmt.setDate(15, rs.getDate(15));
		         prepstmt.setString(16, rs.getString(16));
		         prepstmt.setDate(17, rs.getDate(17));
		         prepstmt.setString(18, rs.getString(18));
		         prepstmt.setDate(19, rs.getDate(19));
		         prepstmt.setInt(20, rs.getInt(20));
		         prepstmt.setString(21, rs.getString(21));
		         prepstmt.setString(22, rs.getString(22));
		         prepstmt.setDate(23, rs.getDate(23));
		         prepstmt.setString(24, rs.getString(24));
		         prepstmt.setString(25, rs.getString(25));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Test Result Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Test Result Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadStatusData()
{
	
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching HIV_Status Data......................");
			
		}});
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, hiv_status, status_date, person_id, visit_id, tracking_outcome, track_date, agreed_date, reason_for_interruption, cause_of_death, auto, uuid, archived, va_cause_of_death, va_cause_of_death_type,biometric_status\r\n"
			+ "	FROM public.hiv_status_tracker where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hiv_status_tracker(\r\n"
			+ " created_date, created_by, last_modified_date, last_modified_by, facility_id, hiv_status, status_date, person_id, visit_id, tracking_outcome, track_date, agreed_date, reason_for_interruption, cause_of_death, auto, uuid, archived, va_cause_of_death, va_cause_of_death_type,biometric_status)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Status Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDate(1, rs.getDate(1));
		         prepstmt.setString(2, rs.getString(2));
				 prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setInt(5, rs.getInt(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setDate(7, rs.getDate(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setString(9, rs.getString(9));
		         prepstmt.setString(10, rs.getString(10));
		         prepstmt.setDate(11, rs.getDate(11));
		         prepstmt.setDate(12, rs.getDate(12));
		         prepstmt.setString(13, rs.getString(13));
		         prepstmt.setString(14, rs.getString(14));
                 prepstmt.setBoolean(15, rs.getBoolean(15));
                 prepstmt.setString(16, rs.getString(16));
                 prepstmt.setInt(17, rs.getInt(17));
                 prepstmt.setString(18, rs.getString(18));
                 prepstmt.setString(19, rs.getString(19));
                 prepstmt.setString(20, rs.getString(20));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Status Tracker Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Status Tracker Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadPrepClinicData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Prep Clinic Data......................");
			
		}});
	String sql = "SELECT weight, height, pulse, temperature, respiratory_rate, systolic, diastolic, adherence_level, sti_screening, date_prep_start, date_prep_given, prep_given, other_drugs, hiv_test_result, encounter_date, is_commencement, prep_enrollment_uuid, uuid, regimen_id, regimen_type_id, duration, vital_sign_uuid, person_uuid, visit_uuid, next_appointment, why, dateprepgiven, urinalysis, hepatitis, syphilis, other_tests_done, syndromic_sti_screening, date_initial_adherence_counseling, date_referred, referred, urinalysis_result, pregnant, facility_id, extra, risk_reduction_services, noted_side_effects, created_by, date_created, modified_by, date_modified, archived,prep_distribution_setting, family_planning, date_of_family_planning, date_of_liver_function_test_results, prep_type, population_type, liver_function_test_result, history_of_drug_to_drug_interaction, hiv_test_result_date, months_of_refill, history_of_drug_allergies\r\n"
			+ "	FROM public.prep_clinic where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.prep_clinic(\r\n"
			+ "	weight, height, pulse, temperature, respiratory_rate, systolic, diastolic, adherence_level, sti_screening, date_prep_start, date_prep_given, prep_given, other_drugs, hiv_test_result, encounter_date, is_commencement, prep_enrollment_uuid, uuid, regimen_id, regimen_type_id, duration, vital_sign_uuid, person_uuid, visit_uuid, next_appointment, why, dateprepgiven, urinalysis, hepatitis, syphilis, other_tests_done, syndromic_sti_screening, date_initial_adherence_counseling, date_referred, referred, urinalysis_result, pregnant, facility_id, extra, risk_reduction_services, noted_side_effects, created_by, date_created, modified_by, date_modified, archived,prep_distribution_setting, family_planning, date_of_family_planning, date_of_liver_function_test_results, prep_type, population_type, liver_function_test_result, history_of_drug_to_drug_interaction, hiv_test_result_date, months_of_refill, history_of_drug_allergies)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Prep Clinic Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setDouble(1, rs.getDouble(1));
		         prepstmt.setDouble(2, rs.getDouble(2));
		         prepstmt.setDouble(3, rs.getDouble(3));
		         prepstmt.setDouble(4, rs.getDouble(4));
		         prepstmt.setDouble(5, rs.getDouble(5));
		         prepstmt.setDouble(6, rs.getDouble(6));
		         prepstmt.setDouble(7, rs.getDouble(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setBoolean(9, rs.getBoolean(9));
				 prepstmt.setDate(10, rs.getDate(10));
				 prepstmt.setDate(11, rs.getDate(11));
				 prepstmt.setString(12, rs.getString(12));
		         prepstmt.setString(13, rs.getString(13));
		         prepstmt.setString(14, rs.getString(14));
		         prepstmt.setDate(15, rs.getDate(15));
		         prepstmt.setBoolean(16, rs.getBoolean(16));
		         prepstmt.setString(17, rs.getString(17));
		         prepstmt.setString(18, rs.getString(18));
		         prepstmt.setInt(19, rs.getInt(19));
		         prepstmt.setInt(20, rs.getInt(20));
		         prepstmt.setInt(21, rs.getInt(21));
		         prepstmt.setString(22, rs.getString(22));
		         prepstmt.setString(23, rs.getString(23));
		         prepstmt.setString(24, rs.getString(24));
		         prepstmt.setDate(25, rs.getDate(25));
		         prepstmt.setBoolean(26, rs.getBoolean(26));
		         prepstmt.setDate(27, rs.getDate(27));
		         prepstmt.setObject(28, rs.getObject(28));
		    	 prepstmt.setObject(29, rs.getObject(29));
		    	 prepstmt.setObject(30, rs.getObject(30));
		    	 prepstmt.setObject(31, rs.getObject(31));
		    	 prepstmt.setObject(32, rs.getObject(32));
		    	 prepstmt.setDate(33, rs.getDate(33));
		    	 prepstmt.setDate(34, rs.getDate(34));
		    	 prepstmt.setBoolean(35, rs.getBoolean(35));
		         prepstmt.setString(36, rs.getString(36));
		         prepstmt.setString(37, rs.getString(37));
		         prepstmt.setInt(38, rs.getInt(38));
		         prepstmt.setObject(39, rs.getObject(39));
		         prepstmt.setString(40, rs.getString(40));
		         prepstmt.setString(41, rs.getString(41));
		         prepstmt.setString(42, rs.getString(42));
		         prepstmt.setDate(43, rs.getDate(43));
		         prepstmt.setString(44, rs.getString(44));
		         prepstmt.setDate(45, rs.getDate(45));
		         prepstmt.setInt(46, rs.getInt(46));
		         prepstmt.setString(47, rs.getString(47));
		         prepstmt.setString(48, rs.getString(48));
		         prepstmt.setDate(49, rs.getDate(49));
		    	 prepstmt.setDate(50, rs.getDate(50));
		    	 prepstmt.setString(51, rs.getString(51));
		         prepstmt.setString(52, rs.getString(52));
		         prepstmt.setString(53, rs.getString(53));
		         prepstmt.setString(54, rs.getString(54));
		         prepstmt.setDate(55, rs.getDate(55));
		         prepstmt.setInt(56, rs.getInt(56));
		         prepstmt.setString(57, rs.getString(57));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Prep Clinic Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Prep Clinic Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadPrepEligibilityData()
{
	
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching PrEP Eligibility Data......................");
			
		}});
	String sql = "SELECT uuid, unique_id, score, visit_date, hiv_risk, sti_screening, drug_use_history, personal_hiv_risk_assessment, sex_partner_risk, person_uuid, sex_partner, counseling_type, first_time_visit, num_children_less_than_five, num_wives, target_group, extra, facility_id, archived, created_by, date_created, modified_by, date_modified,assessment_for_pep_indication, assessment_for_acute_hiv_infection, assessment_for_prep_eligibility, services_received_by_client, population_type, visit_type, pregnancy_status\r\n"
			+ "	FROM public.prep_eligibility where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.prep_eligibility(\r\n"
			+ "	uuid, unique_id, score, visit_date, hiv_risk, sti_screening, drug_use_history, personal_hiv_risk_assessment, sex_partner_risk, person_uuid, sex_partner, counseling_type, first_time_visit, num_children_less_than_five, num_wives, target_group, extra, facility_id, archived, created_by, date_created, modified_by, date_modified,assessment_for_pep_indication, assessment_for_acute_hiv_infection, assessment_for_prep_eligibility, services_received_by_client, population_type, visit_type, pregnancy_status)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Prep Eligibility Data......................");
					
				}});
		       while (rs.next()) {
		         prepstmt.setString(1, rs.getString(1));
		    	 prepstmt.setString(2, rs.getString(2));
		    	 prepstmt.setInt(3, rs.getInt(3));
		    	 prepstmt.setDate(4, rs.getDate(4));
		    	 prepstmt.setObject(5, rs.getObject(5));
		    	 prepstmt.setObject(6, rs.getObject(6));
		    	 prepstmt.setObject(7, rs.getObject(7));
		    	 prepstmt.setObject(8, rs.getObject(8));
		    	 prepstmt.setObject(9, rs.getObject(9));
		         prepstmt.setString(10, rs.getString(10));
		         prepstmt.setString(11, rs.getString(11));
		         prepstmt.setString(12, rs.getString(12));
				 prepstmt.setBoolean(13, rs.getBoolean(13));
		         prepstmt.setString(14, rs.getString(14));
		         prepstmt.setInt(15, rs.getInt(15));
		         prepstmt.setString(16, rs.getString(16));
		         prepstmt.setObject(17, rs.getObject(17));
		         prepstmt.setInt(18, rs.getInt(18));
		         prepstmt.setInt(19, rs.getInt(19));
		         prepstmt.setString(20, rs.getString(20));
		         prepstmt.setDate(21, rs.getDate(21));
		         prepstmt.setString(22, rs.getString(22));
		         prepstmt.setDate(23, rs.getDate(23));
		         prepstmt.setObject(24, rs.getObject(24));
		    	 prepstmt.setObject(25, rs.getObject(25));
		    	 prepstmt.setObject(26, rs.getObject(26));
		    	 prepstmt.setObject(27, rs.getObject(27));
		    	 prepstmt.setString(28, rs.getString(28));
		    	 prepstmt.setString(29, rs.getString(29));
		    	 prepstmt.setString(30, rs.getString(30));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Prep Eligibility Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Prep Eligibility Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadPrepEnrollmentData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching PrEP Enrollment Data......................");
			
		}});
	String sql = "SELECT unique_id, target_group, uuid, date_started, person_uuid, visit_uuid, extra, facility_id, prep_eligibility_uuid, status, date_enrolled, date_referred, risk_type, supporter_phone, supporter_name, supporter_relationship_type, anc_unique_art_no, hiv_testing_point, date_last_hiv_negative_test, archived, created_by, date_created, modified_by, date_modified\r\n"
			+ "	FROM public.prep_enrollment where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.prep_enrollment(\r\n"
			+ "	unique_id, target_group, uuid, date_started, person_uuid, visit_uuid, extra, facility_id, prep_eligibility_uuid, status, date_enrolled, date_referred, risk_type, supporter_phone, supporter_name, supporter_relationship_type, anc_unique_art_no, hiv_testing_point, date_last_hiv_negative_test, archived, created_by, date_created, modified_by, date_modified)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Prep Enrollment Data......................");
					
				}});
		       while (rs.next()) {
		         
		         prepstmt.setString(1, rs.getString(1));
				 prepstmt.setString(2, rs.getString(2));
		         prepstmt.setString(3, rs.getString(3));
		         prepstmt.setDate(4, rs.getDate(4));
		         prepstmt.setString(5, rs.getString(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setObject(7, rs.getObject(7));
		         prepstmt.setInt(8, rs.getInt(8));
		         prepstmt.setString(9, rs.getString(9));
		         prepstmt.setString(10, rs.getString(10));
		         prepstmt.setDate(11, rs.getDate(11));
		         prepstmt.setDate(12, rs.getDate(12));
		         prepstmt.setString(13, rs.getString(13));
		         prepstmt.setString(14, rs.getString(14));
		         prepstmt.setString(15, rs.getString(15));
		         prepstmt.setString(16, rs.getString(16));
		         prepstmt.setString(17, rs.getString(17));
		         prepstmt.setString(18, rs.getString(18));
		         prepstmt.setDate(19, rs.getDate(19));
		         prepstmt.setInt(20, rs.getInt(20));
		         prepstmt.setString(21, rs.getString(21));
		         prepstmt.setDate(22, rs.getDate(22));
		         prepstmt.setString(23, rs.getString(23));
		         prepstmt.setDate(24, rs.getDate(24));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Prep Enrollment Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Prep Enrollment Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadPrepInterruptionData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Prep Interruption Data......................");
			
		}});
	String sql = "SELECT uuid, person_uuid, prep_enrollment_uuid, interruption_type, interruption_date, date_client_died, cause_of_death, source_of_death_info, date_client_referred_out, facility_referred_to, interruption_reason, extra, facility_id, archived, created_by, date_created, modified_by, date_sero_converted, date_restart_placed_back_medication, link_to_art, encounter_date, date_modified, reason_stopped, reason_stopped_others\r\n"
			+ "	FROM public.prep_interruption where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.prep_interruption(\r\n"
			+ "	uuid, person_uuid, prep_enrollment_uuid, interruption_type, interruption_date, date_client_died, cause_of_death, source_of_death_info, date_client_referred_out, facility_referred_to, interruption_reason, extra, facility_id, archived, created_by, date_created, modified_by, date_sero_converted, date_restart_placed_back_medication, link_to_art, encounter_date, date_modified, reason_stopped, reason_stopped_others)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Prep Interruption Data......................");
					
				}});
		       while (rs.next()) {
		         prepstmt.setString(1, rs.getString(1));
		         prepstmt.setString(2, rs.getString(2));
		         prepstmt.setString(3, rs.getString(3));
		         prepstmt.setString(4, rs.getString(4));
				 prepstmt.setDate(5, rs.getDate(5));
				 prepstmt.setDate(6, rs.getDate(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setDate(9, rs.getDate(9));
		         prepstmt.setString(10, rs.getString(10));
		         prepstmt.setString(11, rs.getString(11));
		         prepstmt.setObject(12, rs.getObject(12));
		         prepstmt.setInt(13, rs.getInt(13));
		         prepstmt.setInt(14, rs.getInt(14));
		         prepstmt.setString(15, rs.getString(15));
		         prepstmt.setDate(16, rs.getDate(16));
		         prepstmt.setString(17, rs.getString(17));
		         prepstmt.setDate(18, rs.getDate(18));
		         prepstmt.setDate(19, rs.getDate(19));
		         prepstmt.setBoolean(20, rs.getBoolean(20));
		         prepstmt.setDate(21, rs.getDate(21));
		         prepstmt.setDate(22, rs.getDate(22));
		         prepstmt.setString(23, rs.getString(23));
		         prepstmt.setString(24, rs.getString(24));
		         prepstmt.addBatch();
		         
		       }
		       prepstmt.executeBatch();try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Prep Interruption Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Prep Interruption Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadHTSStratificationData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching HTS Stratification Data......................");
			
		}});
	String sql = "SELECT code, person_uuid, age, testing_setting, modality, target_group, entry_point, community_entry_point, visit_date, dob, date_created, created_by, date_modified, modified_by, archived, facility_id, risk_assessment,source\r\n"
			+ "	FROM public.hts_risk_stratification where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hts_risk_stratification(\r\n"
			+ "	code, person_uuid, age, testing_setting, modality, target_group, entry_point, community_entry_point, visit_date, dob, date_created, created_by, date_modified, modified_by, archived, facility_id, risk_assessment,source)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Stratification Data......................");
					
				}});
		       while (rs.next()) {
		         prepstmt.setString(1, rs.getString(1));
		         prepstmt.setString(2, rs.getString(2));
		         prepstmt.setString(3, rs.getString(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setString(5, rs.getString(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setDate(9, rs.getDate(9));
				 prepstmt.setDate(10, rs.getDate(10));
				 prepstmt.setDate(11, rs.getDate(11));
		         prepstmt.setString(12, rs.getString(12));
		         prepstmt.setDate(13, rs.getDate(13));
		         prepstmt.setString(14, rs.getString(14));
		         prepstmt.setInt(15, rs.getInt(15));
		         prepstmt.setInt(16, rs.getInt(16));
		         prepstmt.setObject(17, rs.getObject(17));
		         prepstmt.setString(18, rs.getString(18));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("HTS Stratification  Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("HTS Stratification  Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadHTSElicitationData()
{
	
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching HTS Elicitation Data......................");
			
		}});String sql = "SELECT uuid, dob, is_date_of_birth_estimated, sex, address, last_name, first_name, middle_name, phone_number, alt_phone_number, hang_out_spots, physical_hurt, threaten_to_hurt, notification_method, partner_tested_positive, sexually_uncomfortable, currently_live_with_partner, relationship_with_index_client, date_partner_came_for_testing, facility_id, hts_client_uuid, date_created, created_by, date_modified, modified_by, archived, extra, offered_ins, accepted_ins,source\r\n"
			+ "	FROM public.hts_index_elicitation where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hts_index_elicitation(\r\n"
			+ "	 uuid, dob, is_date_of_birth_estimated, sex, address, last_name, first_name, middle_name, phone_number, alt_phone_number, hang_out_spots, physical_hurt, threaten_to_hurt, notification_method, partner_tested_positive, sexually_uncomfortable, currently_live_with_partner, relationship_with_index_client, date_partner_came_for_testing, facility_id, hts_client_uuid, date_created, created_by, date_modified, modified_by, archived, extra, offered_ins, accepted_ins,source)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading HTS Elicitation Data......................");
					
				}});
		       while (rs.next()) {
		         prepstmt.setString(1, rs.getString(1));
		         prepstmt.setDate(2, rs.getDate(2));
		         prepstmt.setBoolean(3, rs.getBoolean(3));
		         prepstmt.setInt(4, rs.getInt(4));
		         prepstmt.setString(5, rs.getString(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setString(9, rs.getString(9));
		         prepstmt.setString(10, rs.getString(10));
		         prepstmt.setString(11, rs.getString(11));
		         prepstmt.setInt(12, rs.getInt(12));
		         prepstmt.setInt(13, rs.getInt(13));
		         prepstmt.setInt(14, rs.getInt(14));
		         prepstmt.setInt(15, rs.getInt(15));
		         prepstmt.setInt(16, rs.getInt(16));
		         prepstmt.setBoolean(17, rs.getBoolean(17));
		         prepstmt.setInt(18, rs.getInt(18));
		         prepstmt.setDate(19, rs.getDate(19));
		         prepstmt.setInt(20, rs.getInt(20));
		         prepstmt.setString(21, rs.getString(21));
		         prepstmt.setDate(22, rs.getDate(22));
		         prepstmt.setString(23, rs.getString(23));
		         prepstmt.setDate(24, rs.getDate(24));
		         prepstmt.setString(25, rs.getString(25));
		         prepstmt.setInt(26, rs.getInt(26));
		         prepstmt.setObject(27, rs.getObject(27));
		         prepstmt.setString(28, rs.getString(28));
		         prepstmt.setString(29, rs.getString(29));
		         prepstmt.setString(30, rs.getString(30));
		         prepstmt.addBatch();
				 
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("HTS Elicitation Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("HTS Elicitation Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadHTSClientData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching HTS Client Data......................");
			
		}});
	String sql = "SELECT  target_group, client_code, date_visit, referred_from, person_uuid, testing_setting, prep_given, other_drugs, hiv_test_result, first_time_visit, num_children, num_wives, type_counseling, index_client, prep_offered, prep_accepted, previously_tested, extra, pregnant, breast_feeding, relation_with_index_client, test1, confirmatory_test, tie_breaker_test, test2, confirmatory_test2, tie_breaker_test2, hiv_test_result2, knowledge_assessment, risk_assessment, tb_screening, sti_screening, facility_id, captured_by, uuid, hepatitis_testing, recency, syphilis_testing, index_notification_services_elicitation, post_test_counseling, sex_partner_risk_assessment, others, cd4, date_created, created_by, date_modified, modified_by, archived, index_client_code, risk_stratification_code,source, referred_for_sti, comment, offered_pns, accepted_pns\r\n"
			+ "	FROM public.hts_client where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hts_client(\r\n"
			+ "	target_group, client_code, date_visit, referred_from, person_uuid, testing_setting, prep_given, other_drugs, hiv_test_result, first_time_visit, num_children, num_wives, type_counseling, index_client, prep_offered, prep_accepted, previously_tested, extra, pregnant, breast_feeding, relation_with_index_client, test1, confirmatory_test, tie_breaker_test, test2, confirmatory_test2, tie_breaker_test2, hiv_test_result2, knowledge_assessment, risk_assessment, tb_screening, sti_screening, facility_id, captured_by, uuid, hepatitis_testing, recency, syphilis_testing, index_notification_services_elicitation, post_test_counseling, sex_partner_risk_assessment, others, cd4, date_created, created_by, date_modified, modified_by, archived, index_client_code, risk_stratification_code,source, referred_for_sti, comment, offered_pns, accepted_pns)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?)";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading HTS Client Data......................");
					
				}});
		       while (rs.next()) {
		         prepstmt.setString(1, rs.getString(1));
		         prepstmt.setString(2, rs.getString(2));
		         prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setInt(4, rs.getInt(4));
				 prepstmt.setString(5, rs.getString(5));
				 prepstmt.setString(6, rs.getString(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setString(9, rs.getString(9));
		         prepstmt.setBoolean(10, rs.getBoolean(10));
		         prepstmt.setInt(11, rs.getInt(11));
		         prepstmt.setInt(12, rs.getInt(12));
		         prepstmt.setInt(13, rs.getInt(13));
		         prepstmt.setBoolean(14, rs.getBoolean(14));
		         prepstmt.setBoolean(15, rs.getBoolean(15));
		         prepstmt.setBoolean(16, rs.getBoolean(16));
		         prepstmt.setBoolean(17, rs.getBoolean(17));
		         prepstmt.setObject(18, rs.getObject(18));
		         prepstmt.setInt(19, rs.getInt(19));
		         prepstmt.setBoolean(20, rs.getBoolean(20));
		         prepstmt.setInt(21, rs.getInt(21));
		         prepstmt.setObject(22, rs.getObject(22));
		         prepstmt.setObject(23, rs.getObject(23));
		         prepstmt.setObject(24, rs.getObject(24));
		         prepstmt.setObject(25, rs.getObject(25));
		         prepstmt.setObject(26, rs.getObject(26));
		         prepstmt.setObject(27, rs.getObject(27));
		         prepstmt.setString(28, rs.getString(28));
		         prepstmt.setObject(29, rs.getObject(29));
		         prepstmt.setObject(30, rs.getObject(30));
		         prepstmt.setObject(31, rs.getObject(31));
		         prepstmt.setObject(32, rs.getObject(32));
		         prepstmt.setInt(33, rs.getInt(33));
		         prepstmt.setString(34, rs.getString(34));
		         prepstmt.setString(35, rs.getString(35));
		         prepstmt.setObject(36, rs.getObject(36));
		         prepstmt.setObject(37, rs.getObject(37));
		         prepstmt.setObject(38, rs.getObject(38));
		         prepstmt.setObject(39, rs.getObject(39));
		         prepstmt.setObject(40, rs.getObject(40));
		         prepstmt.setObject(41, rs.getObject(41));
		         prepstmt.setObject(42, rs.getObject(42));
		         prepstmt.setObject(43, rs.getObject(43));
		         prepstmt.setDate(44, rs.getDate(44));
		         prepstmt.setString(45, rs.getString(45));
		         prepstmt.setDate(46, rs.getDate(46));
		         prepstmt.setString(47, rs.getString(47));
		         prepstmt.setInt(48, rs.getInt(48));
		         prepstmt.setString(49, rs.getString(49));
		         prepstmt.setString(50, rs.getString(50));
		         prepstmt.setString(51, rs.getString(51));
		         prepstmt.setString(52, rs.getString(52));
		         prepstmt.setString(53, rs.getString(53));
		         prepstmt.setString(54, rs.getString(54));
		         prepstmt.setString(55, rs.getString(55));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("HTS Client Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("HTS Client Data", ex.getMessage());
	this.errorCount++;
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadCaseManagerData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Case Manager Data......................");
			
		}});
	String sql = "select id,uuid,first_name,last_name,created_date,created_by,last_modified_date,last_modified_by,facility_id FROM public.case_manager where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.case_manager(\r\n"
			+ "	id,uuid,first_name,last_name,created_date,created_by,last_modified_date,last_modified_by,facility_id)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Case Manager Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setString(2, rs.getString(2));
		         prepstmt.setString(3, rs.getString(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setDate(5, rs.getDate(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setDate(7, rs.getDate(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setInt(9, rs.getInt(9));
		         //prepstmt.setInt(10, rs.getInt(10));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Case Manager Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());
	errors.put("Case Manager Data", ex.getMessage());
	this.errorCount++;	
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadCaseManagerPatientData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching Case Manager Patient Data......................");
			
		}});
	String sql = "select id,uuid,person_uuid,case_manager_id,created_date,created_by,last_modified_date,last_modified_by,facility_id FROM public.case_manager where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.case_manager(\r\n"
			+ "	id,uuid,person_uuid,case_manager_id,created_date,created_by,last_modified_date,last_modified_by,facility_id)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading Case Manager Data......................");
					
				}});
		       while (rs.next()) {
		         //prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setInt(1, rs.getInt(1));
		         prepstmt.setString(2, rs.getString(2));
		         prepstmt.setString(3, rs.getString(3));
		         prepstmt.setInt(4, rs.getInt(4));
		         prepstmt.setDate(5, rs.getDate(5));
		         prepstmt.setString(6, rs.getString(6));
		         prepstmt.setDate(7, rs.getDate(7));
		         prepstmt.setString(8, rs.getString(8));
		         prepstmt.setInt(9, rs.getInt(9));
		         //prepstmt.setInt(10, rs.getInt(10));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("Case Manager Patient Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());
	errors.put("Case Manager Patient Data", ex.getMessage());
	this.errorCount++;	
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadDsdDevolvementData()
{
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		hbox.setVisible(true);
		description.setText("Fetching DSD Devolvement Data Data......................");
			
		}});
	String sql = "select uuid,person_uuid,date_devolved,dsd_type,archived,date_return_to_site,outlet_name,created_date,created_by,last_modified_date,last_modified_by,facility_id FROM public.case_manager where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.case_manager(\r\n"
			+ "uuid,person_uuid,date_devolved,dsd_type,archived,date_return_to_site,outlet_name,created_date,created_by,last_modified_date,last_modified_by,facility_id)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	try{
		
		Connection con = DriverManager.getConnection
		          (url, user, pass);
		//Connection insertcon = DriverManager.getConnection(server,user,pass);
		       Statement stmt = con.createStatement();
		       PreparedStatement prepstmt = insertcon.prepareStatement(insertsql);
		       ResultSet rs = stmt.executeQuery(sql);
		       Platform.runLater(new Runnable() {

				@Override
				public void run() {
				
				hbox.setVisible(true);
				description.setText("Uploading DSD Devolvement Data......................");
					
				}});
		       while (rs.next()) {
		    	 prepstmt.setString(1, rs.getString(1));
		    	 prepstmt.setString(2, rs.getString(2));
		         prepstmt.setDate(3, rs.getDate(3));
		         prepstmt.setString(4, rs.getString(4));
		         prepstmt.setInt(5, rs.getInt(5));
		         prepstmt.setDate(6, rs.getDate(6));
		         prepstmt.setString(7, rs.getString(7));
		         prepstmt.setDate(8, rs.getDate(8));
		         prepstmt.setString(9, rs.getString(9));
		         prepstmt.setDate(10, rs.getDate(10));
		         prepstmt.setString(11, rs.getString(11));
		         prepstmt.setInt(12, rs.getInt(12));
		         //prepstmt.setInt(10, rs.getInt(10));
		         prepstmt.addBatch();
		         
		       }
		       try {
			       prepstmt.executeBatch();
			       }
			       catch(Exception ex)
			       {
			    	System.out.println(ex.getMessage());	
			    	errors.put("DSD Devolvement Data", ex.getMessage());
			    	this.errorCount++;
			       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());
	errors.put("DSD Devolvement Data", ex.getMessage());
	this.errorCount++;	
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
}
