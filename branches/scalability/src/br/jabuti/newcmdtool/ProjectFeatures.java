/*  Copyright 2009  Auri Marcelo Rizzo Vicenzi, Marcio Eduardo Delamaro, 			    Jose Carlos Maldonado

    This file is part of Jabuti.

    Jabuti is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as 
    published by the Free Software Foundation, either version 3 of the      
    License, or (at your option) any later version.

    Jabuti is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Jabuti.  If not, see <http://www.gnu.org/licenses/>.
 */

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

import br.jabuti.graph.CFG;
import br.jabuti.probe.ProberInstrum;
import br.jabuti.project.JabutiProject;

/**
 * 
 * Command line program to create a JaBUTi Project
 * 
 * @version: 0.00001
 * @author: Felipe Besson Auri Marcelo Rizzo Vincenzi Marcio Eduardo Delamaro
 */
public class ProjectFeatures {

	String prjFile = null;
	String jarOriginal = null;
	String trcDir = null;
	String dirOriginal = null;
	String clsDir = null;
	String avoidInstr = null;
	int cfgOption = CFG.NO_CALL_NODE;
	boolean isMobility = false;
	boolean isMissing = false;
	String executionMode = null;
	String content = null;
	ConfigDefaults label;
	ConfProjectDAO dao;
	ProberInstrum instrum;
	//TestSession testSession;
	
	/**
	 * Constructor
	 * 
	 * @param isMobility
	 * @param cfgOption
	 * @param avoidTests
	 * @param dirTests
	 * @param dirOriginal
	 * @param jarTests
	 * @param jarOriginal
	 * @param prjFile
	 * @param opt
	 *            Options command line options
	 */
	public ProjectFeatures(String prjFile, String jarOriginal, 
			String dirOriginal, String avoidInstr,
			int cfgOption, boolean isMobility,
			String executionMode, String trcDir, String clsDir) {

		this.prjFile = prjFile;
		this.jarOriginal = jarOriginal;
		this.dirOriginal = dirOriginal;
		this.avoidInstr = avoidInstr;
		this.cfgOption = cfgOption;
		this.isMobility = isMobility;
		this.executionMode = executionMode;
		this.trcDir = trcDir;
		this.clsDir = clsDir;
		label = new ConfigDefaults();
	
		buildProjectWorkspace();
	}

	/**
	 * Build the Project Workspace, creating, updating its information or just
	 * running a preconfiguration defined
	 */
	public void buildProjectWorkspace() {

		if (executionMode.equals("create")){
			createProjectFiles();
			instrum = new ProberInstrum(dao);
			instrum.startIntrum();
			listClassesInstrumented(instrum.classesList);
		}
		
		else{
			
			if(!(new File(prjFile).exists())){
				System.out.println("The project "+prjFile+ "does not exist");
				System.exit(0);}
			
			else{
			//	dao = new ConfProjectDAO(new File(confFile).getAbsoluteFile());

				if (executionMode.equals("update")||executionMode.equals("run")){
					updateProjectFiles();

					instrum = new ProberInstrum(dao);
					instrum.startIntrum();
				}
				
				//if(executionMode.equals("collect"))
					// testSession = new TestSession(prjFile, clsDir, trcDir);
			}			
		}	
	}

	
	private void updateProjectFiles() {
		
		
		if(!jarOriginal.equals(null))
			dao.setJarPathToBeInstrum(jarOriginal);	
		
		if(!dirOriginal.equals(null))
			dao.setDirPathToBeInstrum(dirOriginal);
		
		if(!avoidInstr.equals(null))
			dao.setAvoidInstrumPath(avoidInstr);	
		
		dao.recordNewFile();
	
	}

	/**
	 * Create physical location of the Jabuti Project
	 */
	public void createProjectFiles() {
		
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
		append(label.JAR_TO_BE_INSTRUM + "=" + jarOriginal);
		append(label.DIR_TO_BE_INSTRUM + "=" + dirOriginal);
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
	
	
	public void listClassesInstrumented(ArrayList<String> classes){
		
		String aux = "";
		
		Iterator<String> it = classes.iterator();
		
		while(it.hasNext())
			aux += it.next() + System.getProperty("line.separator");
		
		try {
			FileWriter writer = new FileWriter(new File(prjFile + "/conf/classes.txt"));
			writer.write(aux);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}