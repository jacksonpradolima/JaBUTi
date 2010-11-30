package br.jabuti.newcmdtool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;

public class CreateProject {

	CommandLine cl;
	String prjFile = null;
	String jar = null;
	String classes = null;
	String temp = "";
	String avoidInstr = null;
	String content = "";
	ConfProjectDAO dao;
	ConfigDefaults label = new ConfigDefaults();
	String confFile = "";
	String dirName = null;
	
	public CreateProject(CommandLine cl) {
		this.cl = cl;
		this.getPrjFile();
		this.getJar();
		this.getClasses();
		this.createProjectFiles();
	}

	private void getPrjFile() {
		prjFile = cl.getOptionValue("create");
	}

	private void getJar() {
		File aux;

		// if is a jar file
		if (cl.hasOption("jar")) {
			aux = new File(cl.getOptionValue("jar"));

			if (aux.exists())
				jar = cl.getOptionValue("jar");
			else {
				System.out.println(cl.getOptionValue("jar")
						+ ": Cannot open: No such file");
				System.exit(0);
			}
		}
	}

	private void getClasses() {
		File aux;

		if (cl.hasOption("cls")) {
			aux = new File(cl.getOptionValue("cls"));

			if (aux.exists())
				classes = cl.getOptionValue("cls");
			else {
				System.out.println(cl.getOptionValue("cls")
						+ ": Cannot open: No such directory");
				System.exit(0);
			}
		}
	}

	public void classesToBeAvoided() {
		File aux;

		if (cl.hasOption("avoid")) {
			aux = new File(cl.getOptionValue("avoid"));

			if (aux.exists())
				avoidInstr = cl.getOptionValue("avoid");
			else {
				System.out.println(cl.getOptionValue("avoid")
						+ ": Cannot open: No such file");
				System.exit(0);
			}
		}
	}

	/**
	 * Create physical location of the Jabuti Project
	 */
	public void createProjectFiles() {
		System.out.println("Writing project files configuration...");
		// Creating Files Structure
		File projectRoot = new File(prjFile);
		File projectConf = new File(prjFile + "/conf");
		File projectReports = new File(prjFile + "/reports");
		File projectFiles = new File(prjFile + "/files");
		File projectTempFiles = new File(prjFile + "/jbtProjectFiles");
		File projectConfFile = new File(prjFile + "/conf/" + prjFile + ".jbt");

		projectRoot.mkdir();
		projectConf.mkdir();
		projectReports.mkdir();
		projectFiles.mkdir();
		projectTempFiles.mkdir();

		content = "********** Project Configuration **********";
		append(label.CURRENT_PROJECT_PATH + "=" + projectRoot.getAbsolutePath());
		append(label.JAR_TO_BE_INSTRUM + "=" + jar);
		append(label.DIR_TO_BE_INSTRUM + "=" + classes);
		append(label.AVOID_INSTRUM + "=" + avoidInstr);

		try {
			FileWriter writer = new FileWriter(projectConfFile);
			writer.write(content);
			writer.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		dao = new ConfProjectDAO(projectConfFile.getAbsoluteFile());
		copyFilesToProjectDir(avoidInstr, prjFile + "/conf/avoidInstrum.txt");
		
		File clsDir = new File(classes);
		dirName = clsDir.getName();
		listDir("", clsDir);
		saveClassesTXT(temp);

		System.out.println("Project " + prjFile + " created in "
				+ projectRoot.getAbsolutePath());
	}

	/**
	 * Add an information in the global variable content that is going to be
	 * recorded in disk
	 */
	public void append(String information) {
		content = content + System.getProperty("line.separator") + information;
	}

	/**
	 * Copy files into the project directory
	 */
	public void copyFilesToProjectDir(String srFile, String dtFile) {
		try {

			if (srFile != null && dtFile != null) {
				File f1 = new File(srFile);
				File f2 = new File(dtFile);
				InputStream in = new FileInputStream(f1);

				OutputStream out = new FileOutputStream(f2);

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public void listDir(String relativeName, File file){
		relativeName += file.getName();

		if(file.isDirectory()){
			relativeName += ".";
			
			File[] files = file.listFiles();
			
			for(int i=0;i<files.length;i++)
				listDir(relativeName,files[i]);	
		}
		
		else
		{
			if(relativeName.endsWith(".class"))
				temp += relativeName.substring(dirName.length()+1,relativeName.length()-6) + 
				System.getProperty("line.separator");
			
		}
	}

	
	public void saveClassesTXT(String content) {

		try {
			FileWriter writer = new FileWriter(new File(prjFile
					+ "/files/classes.txt"));
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
