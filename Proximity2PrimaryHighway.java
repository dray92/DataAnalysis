package googleMaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Proximity2PrimaryHighway {
	
	private static String mode = "driving";
	private static String key = "AIzaSyBC9xyq6Ski13oPPTdwuuqUnDncOP21Bi8";
	private static JSONObject jObject;
	private static final boolean PRINT_ON = false;		// Set to true to turn on printing to console
	
	public static void main(String[] args) throws IOException {
		String coordinate = "47.610248, -122.325607";
		coordinate = coordinate.replace(" ", "");
		State state = new State(coordinate);
		String myState = state.getState();
		
		HighwayStateMapping h2s = new HighwayStateMapping();
		
		ArrayList<String> highways = h2s.getState2Highway().get(myState);
		System.out.println("Primary Highways: " + highways);
		
		printProximities(highways, coordinate);
		
	}

	public static void printProximities(ArrayList<String> highways,
			String coordinate) throws IOException {
		// TODO Auto-generated method stub
		for(String highway: highways)
			run(highway, coordinate);
	}

	public static void run(String highway, String coordinate) throws IOException {
		// TODO Auto-generated method stub
		String url = 
				"https://maps.googleapis.com/maps/api/directions/json?origin=" 
					+ coordinate
					+ "&destination="
					+ highway
					+ "&mode="
					+ mode
					+ "&language=en-EN&sensor=false"
					+ "&key="
					+ key;
		
		String myUrl = getURL(url);
		if(PRINT_ON) System.out.println("START COORDINATES: "+ coordinate);
		try {
			jsonParser(myUrl);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void jsonParser(String jString) throws JSONException {
		jObject = new JSONObject(jString);
		JSONArray routesObj = jObject.getJSONArray("routes");
		
		final JSONObject containedObj = routesObj.getJSONObject(0);
		
		// unused
		final JSONObject bounds[] = {containedObj.getJSONObject("bounds").getJSONObject("northeast"), 
				containedObj.getJSONObject("bounds").getJSONObject("southwest")};
		
		String copyrights = "Copyright: " + containedObj.getString("copyrights");
		if(PRINT_ON) System.out.println(copyrights);
		
		JSONArray legs = containedObj.getJSONArray("legs");
		
		final JSONObject legsArray = legs.getJSONObject(0);
		
		JSONObject distance = legsArray.getJSONObject("distance");
		String distanceText = "Text: " + distance.getString("text") + " | Value: " + distance.getInt("value");
		if(PRINT_ON) System.out.println("DISTANCE -> " + distanceText);
		
		JSONObject duration = legsArray.getJSONObject("duration");
		String durationText = "Text: " + duration.getString("text") + " | Value: " + duration.getInt("value");
		if(PRINT_ON) System.out.println("DURATION -> " + durationText);
		
		String startAddress = legsArray.getString("start_address");
		if(PRINT_ON) System.out.println("START ADDRESS -> " + startAddress);
		
		String endAddress = legsArray.getString("end_address");
		if(PRINT_ON) System.out.println("END ADDRESS -> " + endAddress);
		
		JSONArray steps = legsArray.getJSONArray("steps");
		
		Double sum = 0.0d;
		String[] arr;
		for(int i = 0; i < steps.length() ; i++) {
			final JSONObject step = steps.getJSONObject(i);
			
			JSONObject stepDistance = step.getJSONObject("distance");
			String stepDistanceText = "Text: " + stepDistance.getString("text") + " | Value: " + stepDistance.getInt("value");
			if(PRINT_ON) System.out.println("\tSTEP NUMBER: " + (i+1));
			if(PRINT_ON) System.out.println("\t\tSTEP DISTANCE -> " + stepDistanceText);
			
			JSONObject stepDuration = step.getJSONObject("duration");
			String stepDurationText = "Text: " + stepDuration.getString("text") + " | Value: " + stepDuration.getInt("value");
			if(PRINT_ON) System.out.println("\t\tSTEP DURATION -> " + stepDurationText);
			
			String instructions = step.getString("html_instructions");
			if(PRINT_ON) System.out.println("\t\tInstructions: " + instructions);
			
			if(i != steps.length()-1) {
				arr = stepDistance.getString("text").split(" ");
				if(arr[1].contains("ft"))
					sum += (0.000189394 * Double.parseDouble(arr[0]));
				else
					sum += Double.parseDouble(arr[0]);
			}
		}
		
		System.out.println("\tDISTANCE TO I-5: " + sum + " miles \n\n");
	}

	private static String getURL(String url) throws IOException {
		// TODO Auto-generated method stub
		URL oracle = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
        String myUrl = "";
        String inputLine;
        while ((inputLine = in.readLine()) != null)
           myUrl += inputLine;
        in.close();
        return myUrl;
	}
}
