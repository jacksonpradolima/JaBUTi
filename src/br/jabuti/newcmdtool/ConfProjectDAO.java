package br.jabuti.newcmdtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * 
 * Data Access Object for the Jabuti configuration file
 * 
 * @version: 0.00001
 * @author: Felipe Besson Auri Marcelo Rizzo Vincenzi Marcio Eduardo Delamaro
 */
public class ConfProjectDAO {
	
	//.jbt File
	private File confFile = null;
	private HashMap<String,String> confMap;
	ConfigDefaults label;
	String content = "";
	
	
	/**
	 * Constructor
	 * @param confFile .jbt File
	 */
	public ConfProjectDAO (File confFile){
		this.setConfFile(confFile);
		this.setConfMap(new HashMap<String,String>());
		label = new ConfigDefaults();
		mapFile2Table();
	}
	
	
	/**
	 * Extracts information from configuration file and persists it in a HashMap
	 */
	public void mapFile2Table(){
		try {
			FileReader reader = new FileReader(confFile);
			BufferedReader buffer = new BufferedReader(reader);
			
			//Ignore the reader
			String data = buffer.readLine();
			
			data = buffer.readLine();
			while(data != null){
				StringTokenizer tokens = new StringTokenizer(data,"=");
				String variable = tokens.nextToken();
				String value = tokens.nextToken();
				
				confMap.put(variable, value);
				data = buffer.readLine();
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getJarPathToBeInstrum(){
		return confMap.get(label.JAR_TO_BE_INSTRUM);
	}
	
	
	public void setJarPathToBeInstrum(String value){
		confMap.remove(label.JAR_TO_BE_INSTRUM);
		confMap.put(label.JAR_TO_BE_INSTRUM, value);
	}
	
	
	public String getDirPathToBeInstrum(){
		return confMap.get(label.DIR_TO_BE_INSTRUM);
	}
	
	
	public void setDirPathToBeInstrum(String value){
		confMap.remove(label.DIR_TO_BE_INSTRUM);
		confMap.put(label.DIR_TO_BE_INSTRUM, value);
	}
	
	
	public String getJarPathToBeTested(){
		return confMap.get(label.JAR_TO_BE_TESTED);
	}
	
	
	public void setJarPathToBeTested(String value){
		confMap.remove(label.JAR_TO_BE_TESTED);
		confMap.put(label.JAR_TO_BE_TESTED,value);
	}
	
	
	public String getDirPathToBeTested(){
		return confMap.get(label.DIR_TO_BE_TESTED);
	}
	
	
	public void setDirPathToBeTested(String value){
		confMap.remove(label.DIR_TO_BE_TESTED);
		confMap.put(label.DIR_TO_BE_TESTED, value);
	}
	
	
	public boolean toAvoidInstrum(){
		if(confMap.get(label.AVOID_INSTRUM).equals("null"))
			return false;
		return true;
	}
	
	public boolean toAvoidTest(){
		if(confMap.get(label.AVOID_TESTED).equals("null"))
			return false;
		return true;
	}
	
	public String getAvoidInstrum(){
		return readList(label.AVOID_INSTRUM);
	}
	
	public String getAvoidInstrumPath(){
		return confMap.get(label.AVOID_INSTRUM);
	}
	
	public void setAvoidInstrumPath(String value){
		confMap.remove(label.AVOID_INSTRUM);
		confMap.put(label.AVOID_INSTRUM,value);
	}
	
	public String getAvoidTest(){
		return readList(label.AVOID_TESTED);
	}
	
	public String getAvoidTestPath(){
		return confMap.get(label.AVOID_TESTED);
	}
	
	public void setAvoidTestPath(String value){
		confMap.remove(label.AVOID_TESTED);
		confMap.put(label.AVOID_TESTED,value);
	}
	
	public String getCurrentProject(){
		return confMap.get(label.CURRENT_PROJECT_PATH);
	}

	public void setConfFile(File confFile) {
		this.confFile = confFile;
	}

	
	public File getConfFile() {
		return confFile;
	}

	
	public void setConfMap(HashMap<String,String> confMap) {
		this.confMap = confMap;
	}

	
	public HashMap<String,String> getConfMap() {
		return confMap;
	}
	
	
	public String readList(String path){
		String output = "";
		String lista = confMap.get(path);
		
		try {
			FileReader reader = new FileReader(lista);
			BufferedReader buffer = new BufferedReader(reader);
			
			String data = buffer.readLine();
			
			while(data != null){
				output += data + " ";
				data = buffer.readLine();
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return output.substring(0,output.length()-1);
	}
	
	
	/**
	 * Add an information in the global variable content that is going to be 
	 * recorded in disk
	 */
	public void append(String information) {
		content = content + System.getProperty("line.separator") + information;
	}
	
	
	public void recordNewFile(){
		System.out.println("Projeto:");
		content = "********** Project Configuration **********";
		append(label.CURRENT_PROJECT_PATH + "=" + getCurrentProject());
		append(label.JAR_TO_BE_INSTRUM + "=" + getJarPathToBeInstrum());
		append(label.DIR_TO_BE_INSTRUM + "=" + getDirPathToBeInstrum());
		append(label.AVOID_INSTRUM + "=" + getAvoidInstrumPath());
		append(label.JAR_TO_BE_TESTED + "=" + getJarPathToBeTested());
		append(label.DIR_TO_BE_TESTED + "=" + getDirPathToBeTested());
		append(label.AVOID_TESTED + "=" + getAvoidTestPath());

		try {
			FileWriter writer = new FileWriter(confFile);
			writer.write(content);
			writer.close();
		} 
		
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
