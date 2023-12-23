package application;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.io.*;
import java.util.Properties;

public class FacilityDAO implements DAO{
	
public List<Facility> getFacilities()
{

	String sql = "select bou.name as Name,organisation_unit_id as ID from base_application_user_organisation_unit bauou inner join base_organisation_unit\r\n"
			+ "bou on bou.id = bauou.organisation_unit_id;";
    List<Facility> list = new ArrayList<>();
    String url,user,pass;
    
    try {
    	try(InputStream is = new FileInputStream("application.properties"))
        {
    	Properties prop = new Properties();
    	prop.load(is);
    	url = prop.getProperty("db.url");
    	user = prop.getProperty("db.user");
    	pass = prop.getProperty("db.pass");
    		    	
        }
       Connection con = DriverManager.getConnection
          (url, user, pass);
       Statement stmt = con.createStatement();
       ResultSet rs = stmt.executeQuery(sql);
       while (rs.next()) {
          Facility f = createFacility(rs);
          list.add(f);
       }
       rs.close();
       con.close();
    } catch (Exception ex) {
    	
    	System.out.println(ex.getMessage());
    }
    return list;

}

private Facility createFacility(ResultSet rs)
{
	Facility facility = new Facility();
	try
   {
   facility.setFacilityId(rs.getInt("ID"));
   facility.setFacilityName(rs.getString("Name"));
   }
   catch(SQLException ex)
   {
	   
   }
   catch(Exception ex)
   {
	   
   }
   return facility;
}
}
