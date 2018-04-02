import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import java.io.*;
import java.sql.*;


/**
 * Servlet implementation class sensorToDB
 */
@WebServlet("/sensorToDB2")
public class sensorTDB2 extends HttpServlet {
	
		private static final long serialVersionUID = 1L;

		private String lastValidSensorNameStr  = "no sensor";
        private String lastValidSensorValueStr = "invalid";
        private String returnMessage = "";
        
       Connection conn = null;
	  Statement stmt;

	  public void init(ServletConfig config) throws ServletException {
	  // init method is run once at the start of the servlet loading
	  // This will load the driver and establish a connection
	    super.init(config);
		String user = "smitdani";
	    String password = "Crofghat4";
	    String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:3306/"+user;

		// Load the database driver
		try {  Class.forName("com.mysql.jdbc.Driver").newInstance();
	        } catch (Exception e) {
	            System.out.println(e);
	        }

			// get a connection with the user/pass
	        try {
	            conn = DriverManager.getConnection(url, user, password);
	            // System.out.println("DEBUG: Connection to database successful.");
	            stmt = conn.createStatement();
	        } catch (SQLException se) {
	            System.out.println(se);
	        }
	  } // init()

	  public void destroy() {
	        try { conn.close(); } catch (SQLException se) {
	            System.out.println(se);
	        }
	  } // destroy()
	  
	
	
    public sensorTDB2() {
        super();
        // TODO Auto-generated constructor stub
    }


public void doGet(HttpServletRequest request,
           		  HttpServletResponse response)	throws ServletException, IOException {


	response.setStatus(HttpServletResponse.SC_OK);
	String info = request.getParameter("getdata");

	// Do we want info or to enter data on current sensor?
	// if request for info isn't here, record the current sensor name/value from the parameters
	if (info == null){
		String sensorNameStr = request.getParameter("sensorname");
		String sensorValueStr = request.getParameter("sensorvalue");
		if (!(sensorNameStr==null) && !(sensorValueStr==null)) {
			returnMessage = updateSensorTable(sensorNameStr, sensorValueStr);
		}
		else returnMessage = "bad data";

		PrintWriter out = response.getWriter();
		System.out.println("DEBUG: Return response for receiving data "+ returnMessage);
		out.print(returnMessage);
		out.close();

	} // endif not requesting info

	else {  // send info as json
		String sensorNameStr = request.getParameter("sensorname");
		String sensorValueStr = "No sensor data";
		if (!(sensorNameStr==null)) {
			sensorValueStr = getSensorData(sensorNameStr);
		}
	
//		response.setContentType("application/json");
		response.setContentType("text/html");
		
	    String json = "{\"sensor\": {\"" + sensorNameStr + 
	    		      "\": \"" + sensorValueStr + "\"}}";
	
		PrintWriter out = response.getWriter();
		System.out.println("DEBUG: json return: "+json);
		//out.print(json);
		out.print(sensorValueStr);
		out.close();
	}
}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // Post is same as Get, so pass on parameters and do same
	    doGet(request, response);
	}

private String updateSensorTable(String sensorNameStr, String sensorValueStr){
	try {
		// Create the INSERT statement from the parameters
		// set time inserted to be the current time on database server
		String updateSQL = 
	     	"insert into sensorUsage(SensorName, SensorValue, TimeInserted) "+
	      	"values('"+sensorNameStr+"','"+sensorValueStr+"', now());";
	        System.out.println("DEBUG: Update: " + updateSQL);
	        stmt.executeUpdate(updateSQL);
	} catch (SQLException se) {
		// Problem with update, return failure message
	    System.out.println(se);
	    return("Invalid");
	}

	// all ok, update last known values and return
	lastValidSensorNameStr = sensorNameStr;
	lastValidSensorValueStr = sensorValueStr;	
	return "OK";
}	
private String getSensorData(String sensorname){
	// create select statement to retrieve most recent sensor value for
	// a given sensor name.
	
	PreparedStatement pst;
	ResultSet rs;
	String noDataAvailable = "No data for sensor name "+sensorname;
	// select data for this sensor, limit results to 1 record
	String selectSQL = "select SensorValue from sensorUsage where "+
	                   "SensorName = '" + sensorname + 
	                   "' order by TimeInserted desc limit 1";
	System.out.println("DEBUG: Sensor retrieval SQL is : "+ selectSQL);
	String retrievedSensorData = noDataAvailable;
      try { 
        pst = conn.prepareStatement(selectSQL);
        rs = pst.executeQuery();
        // iterate the result set to get the value
        while (rs.next()) {
        	retrievedSensorData = rs.getString(1);
            System.out.println("DEBUG: Retrieved : "+ retrievedSensorData);
        }

    } catch (SQLException ex) {
            System.out.println("Error in SQL " + ex.getMessage());
    }
	
    System.out.println("DEBUG: Final retrieved value : " + retrievedSensorData);

    return retrievedSensorData;
}

	
	
}
