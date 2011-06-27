package net.i2p.itoopie.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Manage the configuration of itoopie.
 * @author mathias
 * modified: hottuna
 *
 */
public class ConfigurationManager {
	
	private static ConfigurationManager instance;
	//Configurations with a String as value
	private Map<String, String> stringConfigurations = new HashMap<String, String>();
	//Configurations with a Boolean as value
	private Map<String, Boolean> booleanConfigurations = new HashMap<String, Boolean>();
	//Configurations with an Integer as value
	private Map<String, Integer> integerConfigurations = new HashMap<String, Integer>();

	private ConfigurationManager() {}
	
	public synchronized static ConfigurationManager getInstance() {
		if(instance == null) {
			instance = new ConfigurationManager();
		}
		return instance;
	}
	
	/**
	 * Collects arguments of the form --word, --word=otherword and -blah
	 * to determine user parameters.
	 * @param args Command line arguments to the application
	 */
	public void loadArguments(String[] args) {
		for(int i=0; i<args.length; i++) {
			String arg = args[i];
			if(arg.startsWith("--")) {
				parseConfigStr(arg.substring(2));
			}
		}
	}
	
	public void parseConfigStr(String str){
		int eqIndex = str.indexOf('=');
		if (eqIndex != -1){
			String key = str.substring(0, eqIndex).trim().toLowerCase();
			String value = str.substring(eqIndex+1, str.length()).trim();
			System.out.println("Key:Value, " + key + ":" + value);
			//Try parse as integer.
			try {
				int i = Integer.parseInt(value);
				integerConfigurations.put(key, i);
				return;
			} catch (NumberFormatException e){}
			//Check if value is a bool
			if (value.toLowerCase().equals("true")){
				booleanConfigurations.put(key, Boolean.TRUE);
				return;
			} else if (value.toLowerCase().equals("false")){
				booleanConfigurations.put(key, Boolean.FALSE);
				return;
			}
			stringConfigurations.put(key, value);
		}
	}

	
	/**
	 * Check if a specific boolean configuration exists.
	 * @param arg The key for the configuration.
	 * @param defaultValue If the configuration is not found, we use a default value.
	 * @return The value of a configuration: true if found, defaultValue if not found.
	 */
	public boolean getConf(String arg, boolean defaultValue) {
		Boolean value = ((Boolean) booleanConfigurations.get(arg));
		//System.out.println(value);
		if(value != null) {
			return value;
		}
		return defaultValue;
	}
	
	
	/**
	 * Check if a specific boolean configuration exists.
	 * @param arg The key for the configuration.
	 * @param defaultValue If the configuration is not found, we use a default value.
	 * @return The value of a configuration: true if found, defaultValue if not found.
	 */
	public int getConf(String arg, int i) {
		Integer value = integerConfigurations.get(arg);
		//System.out.println(value);
		if(value != null) {
			return value;
		}
		return i;
	}
	
	/**
	 * Get a specific String configuration.
	 * @param arg The key for the configuration.
	 * @param defaultValue If the configuration is not found, we use a default value.
	 * @return The value of the configuration, or the defaultValue.
	 */
	public String getConf(String arg, String defaultValue) {
		String value = stringConfigurations.get(arg);
		//System.out.println(value);
		if(value != null) {
			return value;
		}
		return defaultValue;
	}
}
