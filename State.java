package googleMaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// uses Google Maps Geocode API
public class State {

	private static String key = "AIzaSyCJCShCf78cw1AT-SLURI8bQ0zNvGUZn1A";
	private boolean PRINT_ON = false;
	private JSONObject jObject;
	private Set<String> myStates;
	private String state;
	
	// takes lat,long in a string
	public State(String coordinates) throws IOException {
		jObject = null;
		
		HighwayStateMapping h2s = new HighwayStateMapping();
		
		myStates = h2s.getState2Highway().keySet();
		
		System.out.println("My states: " + myStates.toString());
		try {
			processJSON(coordinates);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// fabricates url string and generates request
	private void processJSON(String coordinates) throws JSONException {
		// TODO Auto-generated method stub
		coordinates = coordinates.replace(" ", "");
		String url = 
				"https://maps.googleapis.com/maps/api/geocode/json?address=" 
					+ coordinates
					+ "&region=es&key="
					+ key;
		
		String myUrl = null;
		try {
			myUrl = getURL(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(PRINT_ON ) System.out.println(url);
		if(PRINT_ON ) System.out.println("START COORDINATES: "+ coordinates);
		jsonParser(myUrl);
	}
	
	// receives json encoded string and processes it to get the state name
	// from teh array containing address_componnets
	private void jsonParser(String myUrl) throws JSONException {
		// TODO Auto-generated method stub
		jObject = new JSONObject(myUrl);
		
		// opening results array
		JSONArray resultsObj = jObject.getJSONArray("results");
		
		JSONObject containedObj = resultsObj.getJSONObject(0);
		if(PRINT_ON) System.out.println("Contents of \"results\": " + containedObj.toString());
		
		JSONArray addressComponents = containedObj.getJSONArray("address_components");
		if(PRINT_ON) System.out.println("Contents of \"address_components\": " +  addressComponents.toString());
		
		JSONObject curObj;
		int addressComponentsLength = addressComponents.length();
		for(int i = 0 ; i < addressComponentsLength ; i++) {
			curObj = addressComponents.getJSONObject(i);
			String potentialStateName = curObj.getString("short_name");
			if(myStates.contains(potentialStateName)) {
				setState(potentialStateName);
			}
		}
		
		
	}

	private void setState(String potentialStateName) {
		// TODO Auto-generated method stub
		this.state = potentialStateName;
	}

	// return string containing state name, null if something went wrong
	public String getState() {
		return state;
	}

	// opens url and gets response
	public static String getURL(String url) throws IOException {
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
