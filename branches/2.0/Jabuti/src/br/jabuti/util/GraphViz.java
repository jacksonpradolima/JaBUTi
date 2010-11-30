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




/**
 * This class store general configuration information
 * for using GraphViz Tool inside our application
 */

package br.jabuti.util;


import java.io.*;


public class GraphViz {
    static public String EXEC = "dot";
    static public String BIN = "C:\\Program Files\\ATT\\Graphviz\\bin\\";
    static public String FLAGS = "-Tgif";
		
    static public int callGraphViz(String dotFile, String targetFile) {
        Runtime command;
        // Criando um objeto RunTim
        command = Runtime.getRuntime();
		
        try {
            command.exec(EXEC + " " + FLAGS + " " + dotFile + " -o " + targetFile);
        } catch (IOException ioe) {
            System.err.println("Error executing GraphViz:\n" + ioe.getMessage() + "\n");
            return -1;
        }
        return 0;
    }
}
