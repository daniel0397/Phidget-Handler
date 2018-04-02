

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/anySensorServer")

public class anySensorServer extends HttpServlet {
	
// Collects or returns data for a parameter called "distance"
	private static final long serialVersionUID = 1L;
	
	private String lastValidSensorNameStr  = "no sensor";
    private String lastValidSensorValueStr = "invalid";
    private String returnMessage = "";

    public anySensorServer() {
        super();
        // TODO Auto-generated constructor stub
    }
	  public void init(ServletConfig config) throws ServletException {
		  System.out.println("Sensor server is up and running\n");	
		  System.out.println("Upload sensor data with http://localhost:8080/PhidgetServer/sensorServer?sensorname=xxx&sensorvalue=nnn");
		  System.out.println("View last sensor reading at  http://localhost:8080/PhidgetServer/sensorServer?getdata=true \n\n");		  
	  }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setStatus(HttpServletResponse.SC_OK);

	    // Check to see whether the client is requesting data or sending it
	    String getdata = request.getParameter("getdata");

	    // if no getdata parameter, client is sending data
	    if (getdata == null){
	    		// getdata is null, therefore it is receiving data
			String sensorNameStr = request.getParameter("sensorname");
			String sensorValueStr = request.getParameter("sensorvalue");
			if (!(sensorNameStr==null) && !(sensorValueStr==null)) {
				returnMessage = updateSensorValues(sensorNameStr, sensorValueStr);
			}
		} // end if getdata is null
	    else {  // Display current data (JSON format)
	    	   sendJSONString(response);
	    }

	}

	private String updateSensorValues(String sensorNameStr, String sensorValueStr){
		// all ok, update last known values and return
		lastValidSensorNameStr = sensorNameStr;
		lastValidSensorValueStr = sensorValueStr;
		System.out.println("DEBUG : Last sensor was " + sensorNameStr + ", with value "+sensorValueStr);
		return "OK";
	}	
	
	
	private void sendJSONString(HttpServletResponse response) throws IOException{
	      response.setContentType("text/plain");
	      String json = "{\"sensor\": {\"" + lastValidSensorNameStr + 
    		      "\": \"" + lastValidSensorValueStr + "\"}}";
	      PrintWriter out = response.getWriter();
	      System.out.println("DEBUG: sensorServer JSON: "+json);
	      // alter comment to send back plain text or json
	      out.print(json);
	      out.close();
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doGet(request, response);
	}

}
