package googleMaps;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HighwayStateMapping {

	private static final String dataFile = "/Users/Debosmit/Documents/Eclipse Workspaces/workspace/DataAnalysis/src/googleMaps/highway.csv";
	private static final String state2AbbrFile = "/Users/Debosmit/Documents/Eclipse Workspaces/workspace/DataAnalysis/src/googleMaps/states.csv";
	private static ArrayList<String> highways;
	private static Map<String, ArrayList<String>> highway2State;
	private static Map<String, ArrayList<String>> state2Highway;
	private static Map<String, String> state2Abbr;
	
	private static final String csvSplitBy = ",";
	
	public HighwayStateMapping() throws IOException {
		highways = new ArrayList<String>();
		highway2State = new HashMap<String, ArrayList<String>>();
		state2Highway = new HashMap<String, ArrayList<String>>();
		state2Abbr = new HashMap<String, String>();
		readFile(dataFile);
		readFile(state2AbbrFile);
		cleanLines();
//		printMappings(highway2State);
//		printMappings(state2Highway);
	}
	
	public Map<String, ArrayList<String>> getHighway2State() {
		return highway2State;
	}
	
	public Map<String, ArrayList<String>> getState2Highway() {
		return state2Highway;
	}
	
	public static void printMappings(Map map) {
//		System.out.println(highway2State.toString());
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
		    String key = (String) iterator.next();
		    System.out.println(key + " -> " + map.get(key));
		}
	}

	private static void readFile(String myFile) {
		if(myFile.contains("highway"))
			readDataFile();
		else
			readStatesFile();
	}

	private static void readStatesFile() {
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(state2AbbrFile));
			while ((line = br.readLine()) != null) {
			    // use comma as separator
				String[] contents = line.split(csvSplitBy);
				state2Abbr.put(contents[0], contents[1]);
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

	private static void readDataFile() {
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(dataFile));
			while ((line = br.readLine()) != null) {
			    // use comma as separator
				String[] contents = line.split("\n");
				if(contents[0].startsWith("I")) {
					highways.add(line);
				}
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
	
	private static void cleanLines() {
		String[] lineContents;
		for(String highway: highways) {
			lineContents = highway.split(csvSplitBy);
			String retVal = cleanHighwayName(lineContents[0]);
			String highwayName;
			if(retVal!=null) {
				highwayName = retVal;
			} else continue;
			ArrayList<String> states = cleanStates(highwayName, lineContents[lineContents.length - 3] + " " + 
						lineContents[lineContents.length - 2] + " " + 
						lineContents[lineContents.length - 1]);
			highway2State.put(highwayName, states);
			Set<String> statesSet = state2Highway.keySet();
			for(String state: states) {
				ArrayList<String> highways;
				if(statesSet.contains(state)) 
					highways = state2Highway.get(state);
				else 
					highways = new ArrayList<String>();
				highways.add(highwayName);
				state2Highway.put(state, highways);
			}
		}
	}

	private static ArrayList<String> cleanStates(String highway, String string) {
		// TODO Auto-generated method stub
		string = string.replace(" only", "");
		string = string.replaceAll("[\\-\\+\\.\\^\":,]","");
		String[] contents = string.split(" ");
		ArrayList<String> associatedStates = new ArrayList<String>();
		ArrayList<String> associatedStatesFull = new ArrayList<String>();
		
		Set<String> fullStateName = state2Abbr.keySet();
		Set<String> abbrStateName = new HashSet<String>();
		for(String stateName: fullStateName)
			abbrStateName.add(state2Abbr.get(stateName));
//		System.out.println(highway + "  " + string);
		
		for(int i = 0 ; i < contents.length ; i++) {
			String word = contents[i];
			if(fullStateName.contains(word) || abbrStateName.contains(word)) {
				if(fullStateName.contains(word)) {
					associatedStates.add(state2Abbr.get(word));
					associatedStatesFull.add(word);
				} else {
					associatedStates.add(word);
					associatedStatesFull.add(state2Abbr.get(word));
				}
			}
			if(i < contents.length - 1) {
				word = contents[i] + " " + contents[i+1];
				if(fullStateName.contains(word) || abbrStateName.contains(word)) {
					if(fullStateName.contains(word)) {
						associatedStates.add(state2Abbr.get(word));
						associatedStatesFull.add(word);
					} else {
						associatedStates.add(word);
						associatedStatesFull.add(state2Abbr.get(word));
					}
				}
			}
		}
		return associatedStates;
	}

	private static String cleanHighwayName(String string) {
		String myString = string.substring(string.indexOf('!') + 1).split(",")[0].replaceAll("[\\+\\.\\^\":,]","");
		if(myString.length() <= 4) return myString;
		return null;
		
	}
 }
