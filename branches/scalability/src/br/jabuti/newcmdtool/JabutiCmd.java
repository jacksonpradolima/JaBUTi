package br.jabuti.newcmdtool;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import br.jabuti.graph.CFG;

public class JabutiCmd {

	String prjFile = null;


	public static void main(String args[]) {
		Options opt = new Options();

		// Programs Operations
		
		opt.addOption("h", "help", false, 
		"Print help for this application");
		
		opt.addOption("create", true, 
		"Create JaBUTi project\n" +
		"ex: create <project_name> -cls <original_classes> -jar <original_classes>[optional] -avoid <txt_file_with classes_ignored>[optional]");
		
		opt.addOption("update", true, 
		"Update JaBUTi project");
		
		opt.addOption("instrument", true, 
		"Instrument the classes of the application under development (AUT)");
		
		opt.addOption("collect", true, 
		"Collect Coverage Data\n" +
		"ex: collect -trc <.trc_dir>");
		
		opt.addOption("spago", true, 
		"Generate the Spago XML\n" +
		"ex: spago -name <id_of_xml_spago_file>");
	
		//Program variables
		
		opt.addOption("cls", true, 
				"Original classes directory");
		
		opt.addOption("jar", true, 
		"Jar Package with original classes");
		
		opt.addOption("trc", true, 
		".trc files directoty");
		
		opt.addOption("name", true, 
		"Name for XML Spago file");
		
		JabutiCmd cmdTool = new JabutiCmd();

		try {
			cmdTool.parseOptions(opt, args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
	

	/**
	 * For each option received, an action is triggered
	 * 
	 * @throws ParseException
	 */
	public void parseOptions(Options opt, String args[]) throws ParseException {

		CommandLineParser parser = new PosixParser();
		CommandLine cl = null;
	

		try {
			cl = parser.parse(opt, args);
		} catch (Exception e) {
			System.out.println("Some arguments are missing \n Try -h or --help for help");
		}

		if (cl.hasOption('h')) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp("java -cp <classpath> JabutiCmd", opt);
		}

		// Set Values for project parameters
		else 
			executionMode(cl);
	}
	

	public void executionMode(CommandLine cl) {
		if (cl.hasOption("create")) {
			projectName(cl, "create");
			//Call Create Project class
			CreateProject newProject = new CreateProject(cl);
		}

		else if (cl.hasOption("update")) {
			projectName(cl, "update");
			//Call Update Project class
		}
		
		else if (cl.hasOption("instrument")) {
			projectName(cl, "instrument");
			//Call Instrument class
			Instrumenter instrum = new Instrumenter(cl);
		}
		
		else if (cl.hasOption("collect")) {
			projectName(cl, "collect");
			TestSession session = new TestSession(cl);
		}
		
		else if (cl.hasOption("spago")) {
			projectName(cl, "spago");
			//Call Spago class
		}

		else {
			System.out.println("Execution mode arg is missing \n");
			System.exit(0);
		}
	}

	
	public void projectName(CommandLine cl,String arg) {
	
		if (cl.getOptionValue(arg).equals(null)){
			System.out.println("The project name must be specified \n");
			System.exit(0);
		}		
	}



}