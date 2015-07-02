package googleMaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class State {

	private static String key = "AIzaSyANwoRCJLC2BWXpQWyagzlGMHPzaTuLnso";
	private boolean PRINT_ON = true;
	
	// takes lat,long or address in a string
	public State(String address) {
		processJSON(address);
	}
	

	private void processJSON(String address) {
		// TODO Auto-generated method stub
		String url = 
				"https://maps.googleapis.com/maps/api/geocode/json?address=" 
					+ address
					+ "&region=es&key="
					+ key;
		
		String myUrl = null;
		try {
			myUrl = getURL(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(PRINT_ON ) System.out.println("START COORDINATES: "+ address);
		jsonParser(myUrl);
	}
	
	private void jsonParser(String myUrl) {
		// TODO Auto-generated method stub
		
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
}
