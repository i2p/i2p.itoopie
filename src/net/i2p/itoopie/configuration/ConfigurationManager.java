package net.i2p.itoopie.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Manage the configuration of I2PControl.
 * @author MathiasDM
 * modified: hottuna
 *
 */
public class ConfigurationManager {
	private static final String DEFAULT_CONFIG_NAME = "itoopie.conf";
	private static final Log _log = LogFactory.getLog(ConfigurationManager.class);
	private static final String APP_DIR_NAME = "itoopie";
	/**
	 * For plugin
	 */
	public static final String PROP_CONF_DIR = "itoopie.confdir";

	
	private static ConfigurationManager instance;
	//Configurations with a String as value
	private static Map<String, String> stringConfigurations = new HashMap<String, String>();
	//Configurations with a Boolean as value
	private static Map<String, Boolean> booleanConfigurations = new HashMap<String, Boolean>();
	//Configurations with an Integer as value
	private static Map<String, Integer> integerConfigurations = new HashMap<String, Integer>();

	private ConfigurationManager() {
		readConfFile();
	}
	
	public synchronized static ConfigurationManager getInstance() {
		if(instance == null) {
			instance = new ConfigurationManager();
		}
		return instance;
	}
	
	/**
	 * Collects settingNameuments of the form --word, --word=otherword and -blah
	 * to determine user parameters.
	 * @param settingNames Command line settingNameuments to the application
	 */
	public void loadsettingNameuments(String[] settingNames) {
		for(int i=0; i<settingNames.length; i++) {
			String settingName = settingNames[i];
			if(settingName.startsWith("--")) {
				parseConfigStr(settingName.substring(2));
			}
		}
	}
	
	/**
	 * Reads configuration from file itoopie.conf, every line is parsed as key=value.
	 */
	public static void readConfFile(){
		File f = new File(getAppConfDir(), DEFAULT_CONFIG_NAME);
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String input;
			while ((input = br.readLine()) != null){
				parseConfigStr(input);
			}
			br.close();
		} catch (FileNotFoundException e) {
			_log.info("Unable to find config file " + f);
		} catch (IOException e) {
			_log.error("Unable to read from config file " + f);
		}
	}
	
	/**
	 * Write configuration into default config file.
	 */
	public static void writeConfFile(){
		TreeMap<String,String> tree = new TreeMap<String,String>();
		for (Entry<String,String> e : stringConfigurations.entrySet()){
			tree.put(e.getKey(), e.getValue());
		}
		for (Entry<String,Integer> e : integerConfigurations.entrySet()){
			tree.put(e.getKey(), e.getValue().toString());
		}
		for (Entry<String,Boolean> e : booleanConfigurations.entrySet()){
			tree.put(e.getKey(), e.getValue().toString());
		}
		File f = new File(getAppConfDir(), DEFAULT_CONFIG_NAME);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (Entry<String,String> e : tree.entrySet()){
				bw.write(e.getKey() + "=" + e.getValue() + "\r\n");
			}
			bw.close();
		} catch (IOException e1) {
			_log.error("Couldn't open file " + f + " for writing config.");
		}
	}
	
	/**
	 * Try to parse the given line as 'key=value', 
	 * where value will (in order) be parsed as integer/boolean/string. 
	 * @param str
	 */
	public static void parseConfigStr(String str){
		int eqIndex = str.indexOf('=');
		if (eqIndex != -1){
			String key = str.substring(0, eqIndex).trim().toLowerCase();
			String value = str.substring(eqIndex+1, str.length()).trim();
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
	 * @param settingName The key for the configuration.
	 * @param defaultValue If the configuration is not found, we use a default value.
	 * @return The value of a configuration: true if found, defaultValue if not found.
	 */
	public boolean getConf(String settingName, boolean defaultValue) {
		Boolean value = booleanConfigurations.get(settingName);
		if(value != null) {
			return value;
		} else {
			booleanConfigurations.put(settingName, defaultValue);
			return defaultValue;
		}
	}
	
	
	/**
	 * Check if a specific boolean configuration exists.
	 * @param settingName The key for the configuration.
	 * @param defaultValue If the configuration is not found, we use a default value.
	 * @return The value of a configuration: true if found, defaultValue if not found.
	 */
	public int getConf(String settingName, int defaultValue) {
		Integer value = integerConfigurations.get(settingName);
		if(value != null) {
			return value;
		} else {
			integerConfigurations.put(settingName, defaultValue);
			return defaultValue;
		}
	}
	
	/**
	 * Get a specific String configuration.
	 * @param settingName The key for the configuration.
	 * @param defaultValue If the configuration is not found, we use a default value.
	 * @return The value of the configuration, or the defaultValue.
	 */
	public String getConf(String settingName, String defaultValue) {
		String value = stringConfigurations.get(settingName);
		if(value != null) {
			return value;
		} else {
			stringConfigurations.put(settingName, defaultValue);
			return defaultValue;
		}
	}
	
	/**
	 * Set a specific int setting
	 * @param settingName
	 * @param nbr
	 */
	public void setConf(String settingName, int nbr){
		integerConfigurations.put(settingName, nbr);
	}
	
	/**
	 * Set a specific string setting
	 * @param settingName
	 * @param string
	 */
	public void setConf(String settingName, String str){
		stringConfigurations.put(settingName, str);
	}
	
	/**
	 * Set a specific boolean setting
	 * @param settingName
	 * @param boolean
	 */
	public void setConf(String settingName, boolean bool){
		booleanConfigurations.put(settingName, bool);
	}
	
	/**
	 * Get the file path to the configuration directory. If the directory does not yet exist, creates it.
	 * @return Application configuration directory.
	 */
	public static File getAppConfDir() {
		String dir;
		// for plugin
		String override = System.getProperty(PROP_CONF_DIR);
		if (override != null) {
			dir = override;
		} else if (System.getenv("APPDATA") != null && !System.getenv("APPDATA").equals("")) {
			dir = System.getenv("APPDATA")+ File.separator + APP_DIR_NAME;   // Windows path
		} else {
			dir = System.getProperty("user.home") + File.separator + "." + APP_DIR_NAME; // Linux/mac path
		}
		File dirFile = new File(dir);
		if (!dirFile.exists()){
			dirFile.mkdirs();
		}
		return dirFile;
	}
}
