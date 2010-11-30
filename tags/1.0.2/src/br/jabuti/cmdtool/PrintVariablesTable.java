/*  Copyright 2003  Auri Marcelo Rizzo Vicenzi, Marcio Eduardo Delamaro, 			    Jose Carlos Maldonado

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


package br.jabuti.cmdtool;


import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.apache.bcel.generic.LocalVariableGen;

import br.jabuti.graph.CFG;
import br.jabuti.graph.CFGNode;
import br.jabuti.graph.GraphNode;
import br.jabuti.project.ClassFile;
import br.jabuti.project.ClassMethod;
import br.jabuti.project.JabutiProject;
import br.jabuti.util.ToolConstants;


/**
 This application program reports bytecode name and the
 corresponding real name of the variable in the source 
 code when such information is available in the bytecode, 
 i.e., when the bytecode was compiled using the depuration
 option (-g).

 For example, by running such a program on the project test.jbt:
	$ java -cp ".;lib\BCEL.jar;lib\jviewsall.jar" cmdtool.PrintVariablesTable -P test.jbt

 The resultant output is:
 
	1       0       0       0       L@0     this
	1       1       4       4       L@4      -
	1       1       3       3       L@3      -
	1       1       2       2       L@2      -
	1       1       1       1       L@1      -
	1       1       0       0       L@0     arg0
	1       1       5       5       L@5      -
	1       1       6       6       L@6      -
	1       1       7       7       L@7      -
	1       2       0       0       L@0     arg0

 where:
		1 column is the class_id;
		2 column is the method_id, inside a given class;
		3 column is the varaible index inside the variable table;
		4 column is the local variable number;
		5 column is the bytecode variable name;
		6 column is the variable real name. An minus (-) represents
		  that the real name of such a variable is not available in the
		  bytecode.

<P>
		
Observe that information about the class_id and method_id can
be obtained by running {@link PrintClassMethodTable}.
		
 @version: 0.00001
 @author: Auri Marcelo Rizzo Vincenzi

*/
public class PrintVariablesTable {
    public static void usage() {
        System.out.println(ToolConstants.toolName + " v. " + ToolConstants.toolVersion);
        System.out.println("\nPrintVariablesTable usage:");
        System.out.println("-------------------\n");
        System.out.println("java cmdtool.PrintVariablesTable [-d <DIR>] -p <PROJECT_NAME>\n");
        System.out.println("      [-d <DIR>]              Optional parameter. Specify the directory where the project");
        System.out.println("                              is located. If not specified, the current directory is assumed.");
        System.out.println("      -p <PROJECT_NAME>       Specify the name of the project to be used. The");
        System.out.println("                              project must be a valid project file (.jba) generated by");
        System.out.println("                              instrument the base class.");
        System.out.println("\nCopyright (c) 2002\n");
    }

    public static void main(String args[]) throws Throwable {
		
        String workDir = null;
        String projectName = null;
						
        JabutiProject project = null;
        if (args.length > 0) {

            int argc = 0;
			
            while ((argc < args.length) && (args[argc].startsWith("-"))) {
				// -d: work directory
                if (("-d".equals(args[argc])) && (argc < args.length - 1)) {
                    argc++;
                    workDir = args[argc];
                } // -p: project name
                else if (("-p".equals(args[argc])) && (argc < args.length - 1)) {
                    argc++;
                    projectName = args[argc];
                } else {
                    System.out.println("Error: Unrecognized option: " + args[argc]);
                    System.exit(0);
                }
                argc++;
            }

            // Checking if all essential parameters are not null
            if (projectName == null) {
                System.out.println("Error: Missing parameter!!!");
                usage();
                System.exit(0);
            }
			
            // Creating the absolute path to a given project
            String absoluteName = projectName;

            if (workDir != null) {
                absoluteName = workDir + File.separator + projectName;
            }

            try {
                File theFile = new File(absoluteName);

                if (!theFile.isFile()) // verifica se existe
                {
                    System.out.println("File " + theFile.getName() + " not found");
                    System.exit(0);
                }
	          	
                project = JabutiProject.reloadProj( theFile.toString(), true );
 				
                String output = new String();
				
                // For each class
                String[] classFileNames = project.getAllClassFileNames();

                for (int k = 0; k < classFileNames.length; k++) {
                    ClassFile cf = project.getClassFile(classFileNames[k]);
                    // For each method
                    String[] methodsNames = cf.getAllMethodsNames();
                    
                    if ( methodsNames == null )
                    	continue;
                	
                    for (int j = 0; j < methodsNames.length; j++) {
                        ClassMethod cm = cf.getMethod(methodsNames[j]);

                        // Getting the local variable table of a given method...
                        LocalVariableGen[] localVar = cm.getLocalVariables();

                        // This table store the index of the variables already
                        // printed
                        HashSet printedIndex = new HashSet();
		          		
                        Hashtable systemVar = new Hashtable();
                        int countSysVar = localVar.length;
		
                        CFG cfg = cm.getCFG();
                        GraphNode[] fdt = cfg.findDFT(true);
                        for (int x = 0; x < fdt.length; x++) {
                            GraphNode gn = fdt[x];
		            		
                            Hashtable definitions = ((CFGNode) gn).getDefinitions();

                            if (definitions.size() > 0) {
                                Enumeration it = definitions.keys();

                                while (it.hasMoreElements()) {
                                    String s = (String) it.nextElement();

                                    if (((!s.startsWith("L@"))
                                                    && (!systemVar.containsKey(s)))
                                            || (s.startsWith("L@")
                                                    && (s.indexOf(".") > 0)
                                                    && (!systemVar.containsKey(s)))
                                            ) {
                                        systemVar.put(s, new Integer(countSysVar++));
                                    }
                                }
                            }
							
                            Hashtable uses = ((CFGNode) gn).getUses();					            		

                            if (uses.size() > 0) {
                                Enumeration it = uses.keys();

                                while (it.hasMoreElements()) {
                                    String s = (String) it.nextElement();
							   		
                                    if (((!s.startsWith("L@"))
                                                    && (!systemVar.containsKey(s)))
                                            || (s.startsWith("L@")
                                                    && (s.indexOf(".") > 0)
                                                    && (!systemVar.containsKey(s)))
                                            ) {
                                        systemVar.put(s, new Integer(countSysVar++));
                                    }
                                }
                            }
                        }

                        for (int z = 0; z < fdt.length; z++) {
                            GraphNode gn = fdt[z];
		
                            Hashtable definitions = ((CFGNode) gn).getDefinitions();
                            String realName = new String();
		
                            if (definitions.size() > 0) {
                                Enumeration it = definitions.keys();

                                while (it.hasMoreElements()) {
                                    String s = (String) it.nextElement();
                                    int byteOffset = ((Integer) definitions.get(s)).intValue();
                                    int localVarIndex = -1;
                                    int tabVarIndex = -1;
									
                                    realName = new String(" - ");
						   										
                                    if (s.startsWith("L@")
                                            && (s.indexOf(".") < 0)) {
                                    	
                                        String numVar = s.substring(2, s.length());

                                        int pos = numVar.indexOf("[");
                                        if (pos != -1) 
                                        	numVar = numVar.substring(0, pos);
                                        
                                        localVarIndex = Integer.parseInt(numVar);
                                        tabVarIndex = localVarIndex;
										
                                        int currentDiff = -1;				   								   		

                                        for (int i = 0; i < localVar.length; i++) {
                                            if (localVar[i].getIndex()
                                                    == localVarIndex) {
                                                if ((byteOffset
                                                        <= localVar[i].getEnd().getPosition())) {
                                                    int diff = Math.abs(localVar[i].getStart().getPosition() - byteOffset);

                                                    if ((currentDiff == -1)
                                                            || (diff
                                                                    <= currentDiff)) {
                                                        realName = localVar[i].getName();
                                                        currentDiff = diff;
                                                        tabVarIndex = i;
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        realName = s;
                                        localVarIndex = ((Integer) systemVar.get(s)).intValue();
                                        tabVarIndex = localVarIndex;
                                    }
		
                                    if (!(printedIndex.contains(new Integer(tabVarIndex)))) {
										
                                        printedIndex.add(new Integer(tabVarIndex));
		
                                        output = output
                                                + new String(cf.getClassId() + "\t" + cm.getMethodId() + "\t" + tabVarIndex + "\t" + localVarIndex + "\t" + s + "\t" + realName + "\n");
								   		
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println(output);
            } catch (Exception e) {
                ToolConstants.reportException(e, ToolConstants.STDERR);
                System.exit(0);
            }
        } else {
            usage();
        }
    }
}
