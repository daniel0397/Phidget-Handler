

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/sensorServer")

public class sensorServer extends HttpServlet {
	
// Collects or returns data for a parameter called "slider"
	private static final long serialVersionUID = 1L;

	private String sliderSensorValueStr = "0";
	private int sliderSensorValue = 0;
	
    public sensorServer() {
        super();
    }
	  public void init(ServletConfig config) throws ServletException {
		  System.out.println("Sensor server is up and running\n");	
		  System.out.println("Upload slider data with http://localhost:8080/PhidgetServer/sensorServer?slider=nnn");
		  System.out.println("View last sensor reading at  http://localhost:8080/PhidgetServer/sensorServer?getdata=true \n\n");		  
	  }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setStatus(HttpServletResponse.SC_OK);

	    // Check to see whether the client is requesting data or sending it
	    String getdata = request.getParameter("getdata");

	    // if no getdata parameter, client is sending data
	    if (getdata == null){
	    		// getdata is null, therefore it is receiving data
	    		// see if there is a parameter called "slider". If so, get its value
	    		if (request.getParameter("slider") != null) {
	    			  // parameter values always treated as strings
		  		  sliderSensorValueStr = request.getParameter("slider");
		  		  System.out.println("DEBUG: Slider Value: New: "+sliderSensorValueStr);
		  		  // optional return string of data to confirm
		  		  // sendJSONString(response);
		  		  
		  		  try {
					// optional - convert string value to int (not always needed)
					sliderSensorValue = Integer
							.parseInt(sliderSensorValueStr);
		  		  	} catch (Exception e) {	}
		  		  } // end if slider not null
		  	  } // end if getdata is null
	    else {  // Display current data (JSON format)
	    	   sendJSONString(response);
	    }

	}

	private void sendJSONString(HttpServletResponse response) throws IOException{
	      response.setContentType("text/plain");
	      String json = "{\"sensor\": {\"slider\": \"" + sliderSensorValueStr + "\"}}";
	      PrintWriter out = response.getWriter();
	      System.out.println("sensorServer JSON: "+json);
	      // alter comment to send back plain text or json
	      out.print(json);
	      //out.print(sliderSensorValueStr);
	      out.close();
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doGet(request, response);
	}

}
