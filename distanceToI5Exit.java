package googleMaps;

/**
 * @author: Debosmit Ray
 */

import org.json.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

class distanceToI5Exit {

	private static JSONObject jObject = null;
	private static String key = "AIzaSyBC9xyq6Ski13oPPTdwuuqUnDncOP21Bi8";
	private static String dest_coord = "47.972589,-122.190843";
	private static String mode = "driving";
	private static ArrayList<String> coordinates; // longitude, latitude
	private static String csvFile = "/Users/Debosmit/Documents/Eclipse Workspaces/workspace/DataAnalysis/src/googleMaps/craigSeattle.csv";
	
	private static final boolean PRINT_ON = false;		// Set to true to turn on printing to console
	
	public static void main(String[] args) throws JSONException, IOException {
		coordinates = new ArrayList<String>();
		readFile(csvFile);
		int i = 0;
		for(String st: coordinates) {
			run(st);
			i++;
//			if(i == 1) break;
			System.out.println(i);
			try {
			    Thread.sleep(550);                 //500 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
	}
	
	public static void readFile(String filename) {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ", ";
	 
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
			    // use comma as separator
				String[] coordinate = line.split(cvsSplitBy);
				coordinates.add(coordinate[0].substring(1) + "," + coordinate[1].substring(0, coordinate[1].length()-1));
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void run(String orig_coord) throws IOException, JSONException {
		String url = 
				"https://maps.googleapis.com/maps/api/directions/json?origin=" 
					+ orig_coord
					+ "&destination="
					+ dest_coord
					+ "&mode="
					+ mode
					+ "&language=en-EN&sensor=false"
					+ "&key="
					+ key;
		
		String myUrl = getURL(url);
		if(PRINT_ON) System.out.println("START COORDINATES: "+ orig_coord);
		jsonParser(myUrl);
	}
	
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
}
