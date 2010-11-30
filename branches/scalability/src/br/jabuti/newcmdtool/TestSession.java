package br.jabuti.newcmdtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.security.auth.login.Configuration;

import org.apache.commons.cli.CommandLine;

import br.jabuti.cmdtool.ImportTestCase;
import br.jabuti.cmdtool.JabutiReport;
import br.jabuti.metrics.Metrics;

public class TestSession {

	CommandLine cl;
	String trcDir = null;
	ArrayList<String> classes;
	String prjFile = null;
	ConfProjectDAO dao;
	
	public TestSession(CommandLine cl) {
		this.cl = cl;
	}
	
	
	private void getInputs(){
		File aux;
		
		if (cl.hasOption("trc")) {
			aux = new File(cl.getOptionValue("trc"));

			if (aux.exists())
				trcDir = cl.getOptionValue("trc");
			else {
				System.out.println(cl.getOptionValue("trc")
						+ ": Cannot open: No such file");
				System.exit(0);
			}
		}
		
		prjFile = cl.getOptionValue("collect");
		
		File confFile = new File(prjFile+"/conf/"+prjFile+".jbt");
		dao = new ConfProjectDAO(confFile);
	}
	
	
	
	
	public void getClasses (){
		this.getInputs();
		File file = new File(prjFile+"/files/classes.txt");
		try {
			FileReader reader = new FileReader(file);
			BufferedReader buffer = new BufferedReader(reader);
			
			String data = buffer.readLine();
			
			while(data != null){
				classes.add(data);
				data = buffer.readLine();
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void collectCoverageData(){
		getClasses();		
		
		String clsDir = dao.getDirPathToBeInstrum();
		String clsList = prjFile+"/files/classes.txt";
		for(Iterator<String> it = classes.iterator(); it.hasNext();){
			String currentClass = it.next();
			String instFile = clsDir+"/"+currentClass;
			String trcPath = trcDir+"/"+currentClass+".trc";
			String workspace = prjFile+"/jbtProjectFiles/";
			File jbtProj = new File(workspace+currentClass+".jbt");
			String reports = prjFile+"/reports/";

			
			String args_1[] = {"-b",instFile,"-p",workspace+currentClass+".jbt","-i",clsList};
			
			br.jabuti.cmdtool.CreateProject proj = new br.jabuti.cmdtool.CreateProject();
			try {
				proj.main(args_1);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(new File(trcPath).exists()){
				String args_2[] = {"-p",jbtProj.getAbsoluteFile().toString()};
				copyFilesToProjectDir(trcPath, workspace+currentClass+".trc");
				ImportTestCase test = new ImportTestCase();
				try {
					test.main(args_2);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			if(jbtProj.exists()){
				JabutiReport report = new JabutiReport();
				
				
				//by criterion
				String args_3[] = {"-pr","-p",jbtProj.getAbsoluteFile().toString(),
						           "-o",reports+currentClass+"-report-by-criterion.xml"};
				
				//by class
				String args_4[] = {"-cl","-p",jbtProj.getAbsoluteFile().toString(),
						           "-o",reports+currentClass+"-report-by-class.xml"};

				//by method
				String args_5[] = {"-me","-p",jbtProj.getAbsoluteFile().toString(),
						           "-o",reports+currentClass+"-report-by-method.xml"};
				
				//metrics
				
				//Metrics metrics = new Metrics(reports, null, args_6);
				
				
				String args_6[] = {"-all","-p",jbtProj.getAbsoluteFile().toString(),
				           "-o",reports+currentClass+"-report-by-full-report.xml"};
				
				
				try {
					report.main(args_3);
					report.main(args_4);
					report.main(args_5);
					report.main(args_6);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
			}
			
		}
	
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
	
	
	

	
}
