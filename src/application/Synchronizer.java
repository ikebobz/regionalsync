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
Hashtable<String,String> errors = new Hashtable<>();

	@Override
	public void run() {
		// TODO Auto-generated method stub
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
		this.alertOnComplete();
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		    }
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Patient Data", ex.getMessage());
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}

public void uploadVisitData()
{
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());
	errors.put("Visit Data", ex.getMessage());
		
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadTriageData()
{
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Triage Data", ex.getMessage());
	}
	//this.updatePropertyFile();
	this.updateProgressBar();
}
public void uploadEnrollmentData()
{
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Enrollment Data", ex.getMessage());
	}
	//this.updatePropertyFile();
	this.updateProgressBar();
}
public void uploadEncounterData()
{
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, visit_date, cd_4, cd_4_percentage, is_commencement, functional_status_id, clinical_stage_id, clinical_note, uuid, hiv_enrollment_uuid, regimen_id, regimen_type_id, art_status_id, archived, vital_sign_uuid, who_staging_id, person_uuid, visit_id, oi_screened, sti_ids, pregnancy_status, sti_treated, opportunistic_infections, adr_screened, adverse_drug_reactions, adherence_level, adheres, next_appointment, lmp_date, tb_screen, is_viral_load_at_start_of_art, viral_load_at_start_of_art, date_of_viral_load_at_start_of_art, cryptococcal_screening_status, cervical_cancer_screening_status, cervical_cancer_treatment_provided, hepatitis_screening_result, family_planing, on_family_planing, level_of_adherence, tb_status, tb_prevention, arvdrugs_regimen, viral_load_order, cd4_count, cd4_semi_quantitative, cd4_flow_cytometry, extra, cd4_type\r\n"
			+ "	FROM public.hiv_art_clinical where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hiv_art_clinical(\r\n"
			+ "	created_date, created_by, last_modified_date, last_modified_by, facility_id, visit_date, cd_4, cd_4_percentage, is_commencement, functional_status_id, clinical_stage_id, clinical_note, uuid, hiv_enrollment_uuid, regimen_id, regimen_type_id, art_status_id, archived, vital_sign_uuid, who_staging_id, person_uuid, visit_id, oi_screened, sti_ids, pregnancy_status, sti_treated, opportunistic_infections, adr_screened, adverse_drug_reactions, adherence_level, adheres, next_appointment, lmp_date, tb_screen, is_viral_load_at_start_of_art, viral_load_at_start_of_art, date_of_viral_load_at_start_of_art, cryptococcal_screening_status, cervical_cancer_screening_status, cervical_cancer_treatment_provided, hepatitis_screening_result, family_planing, on_family_planing, level_of_adherence, tb_status, tb_prevention, arvdrugs_regimen, viral_load_order, cd4_count, cd4_semi_quantitative, cd4_flow_cytometry, extra, cd4_type)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		      // insertcon.close();
		      
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Clinic Data", ex.getMessage());	
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadPharmacyData()
{
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Pharmacy Data", ex.getMessage());
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
private void alertOnComplete()
{
	
	Platform.runLater(new Runnable() {

		@Override
		public void run() {
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Uploads Completed");
		alert.show();
			
		}});


}

public void uploadObservationData()
{
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Observation Data", ex.getMessage());	
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadBiometricData()
{
	String sql = "SELECT  id,created_date, created_by, last_modified_date, last_modified_by, facility_id, person_uuid, template, biometric_type, template_type, enrollment_date, archived, iso, extra, device_name, reason, version_iso_20, image_quality, recapture, recapture_message, hashed, count\r\n"
			+ "	FROM public.biometric where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.biometric(\r\n"
			+ "	id, created_date, created_by, last_modified_date, last_modified_by, facility_id, person_uuid, template, biometric_type, template_type, enrollment_date, archived, iso, extra, device_name, reason, version_iso_20, image_quality, recapture, recapture_message, hashed, count)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Biometric Data", ex.getMessage());
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadEacData()
{
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("eac Data", ex.getMessage());	
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadEacSessionData()
{
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, eac_id, person_uuid, visit_id, barriers, intervention, barriers_others, intervention_others, comment, follow_up_date, eac_session_date, referral, adherence, uuid, status, archived\r\n"
			+ "	FROM public.hiv_eac_session where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("eac_session Data", ex.getMessage());
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadSampleData()
{
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());
	errors.put("Test sample Data", ex.getMessage());
		
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadTestData()
{
	String sql = "SELECT uuid, patient_id, lab_test_id, description, lab_number, lab_test_group_id, order_priority, unit_measurement, lab_test_order_status, viral_load_indication, patient_uuid, facility_id, lab_order_id, created_by, date_created, modified_by, date_modified, archived\r\n"
			+ "	FROM public.laboratory_test where date_modified >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.laboratory_test(\r\n"
			+ "	 uuid, patient_id, lab_test_id, description, lab_number, lab_test_group_id, order_priority, unit_measurement, lab_test_order_status, viral_load_indication, patient_uuid, facility_id, lab_order_id, created_by, date_created, modified_by, date_modified, archived)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());
	errors.put("Test Data", ex.getMessage());
		
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadResultData()
{
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Test Result Data", ex.getMessage());
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
public void uploadStatusData()
{
	String sql = "SELECT created_date, created_by, last_modified_date, last_modified_by, facility_id, hiv_status, status_date, person_id, visit_id, tracking_outcome, track_date, agreed_date, reason_for_interruption, cause_of_death, auto, uuid, archived, va_cause_of_death, va_cause_of_death_type\r\n"
			+ "	FROM public.hiv_status_tracker where last_modified_date >= '"+this.lastUpdate+"'"+" and facility_id="+this.facility_id;
	String insertsql = "INSERT INTO public.hiv_status_tracker(\r\n"
			+ " created_date, created_by, last_modified_date, last_modified_by, facility_id, hiv_status, status_date, person_id, visit_id, tracking_outcome, track_date, agreed_date, reason_for_interruption, cause_of_death, auto, uuid, archived, va_cause_of_death, va_cause_of_death_type)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
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
		         prepstmt.executeUpdate();
		         
		       }
		       rs.close();
		       con.close();
		       //insertcon.close();
		       
		       
	}
	catch(Exception ex)
	{
	System.out.println(ex.getMessage());	
	errors.put("Status Tracker Data", ex.getMessage());
	}
	this.updateProgressBar();
	//this.updatePropertyFile();
}
}
