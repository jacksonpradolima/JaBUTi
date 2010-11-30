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


package br.jabuti.probe;


import java.io.*;
import java.util.*;



/** <p>This class implements a class to create a regular file 
 * to store the registered execution sequence. At 
 * itinialization it registers an object to be notified when the 
 * program exits. At that point, that object call 
 * {@link DefaultProber#dump}.
 */
public class DefaultProber  {
    static public final String delimiter = "**********************";
    static public final String READER_CLASS = "class br.jabuti.probe.DefaultTraceReader";
    static protected Hashtable threadsAndProbs = new Hashtable();
    static private PrintStream fp;
    static private boolean stdout = true, habilitado = false;
    static private String tcName = "";
	
    static {
        // Gets the name of the file where the execution will be dumped
        fp = null;
        String fileProber = System.getProperty("DEFAULT_PROBER");
//		System.out.println("Properties: " + fileProber);

        if (fileProber != null) {
            if (fileProber.length() == 0) {
                fp = System.out;
                stdout = true;
            } else {
                try {
                	init(fileProber);
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ":");
                    System.err.println("\t" + e.getMessage());
                    System.err.println("\tExecution will be dumped to standard output");
                    fp = System.out;
                    stdout = true;
                }
            }
        }
        // register an object that will be notified when the program exits
        Runtime.getRuntime().addShutdownHook(new DefaultProberHook());
    }
	
	static synchronized public void init(String fileProber)
			throws Exception
	{
        RandomAccessFile raf = new RandomAccessFile(fileProber, "rw");

        raf.seek(raf.length());
        FileOutputStream fos = new FileOutputStream(raf.getFD());

        fp = new PrintStream(fos);
        stdout = false;
	}

	static synchronized public void finished()
	throws Exception
	{
		fp.close();
	}

	static synchronized public void startTrace()
	{
		tcName = "";
		habilitado = true;
	}
	
	static synchronized public void startTrace(String name)
	{
		tcName = name;
		habilitado = true;
	}

	static synchronized public void stopTrace()
	{
		habilitado = false;
		dump();
	}

	
    /** This method stores (for example, sending to a file) the 
     * registered execution up to that point */
    static synchronized public void dump() {
        if (fp == null) {
            return;
        }
        Enumeration en = threadsAndProbs.keys();
        if ( ! en.hasMoreElements() ) // se lista esta vazia...
        	return;

        while (en.hasMoreElements()) {
            ProbedNode tr = (ProbedNode) en.nextElement();

            dumpNodes(tr, (ArrayList) threadsAndProbs.get(tr));
        }
        // write a delimiter 
        fp.println(delimiter);
        fp.flush(); // Inseri um flush para descarregar o buffer.
		
        threadsAndProbs = new Hashtable();
    }

    /** This method registers the execution of a given node */
    static synchronized public void probe(Object o, 
    							String clazz, 
    							int metodo, 
    							long nest,
    							Object n) 
    	{    		
    	if ( fp == null || (! habilitado) )
    		return;
        Runnable tr = Thread.currentThread();
        String s = o == null ? "STATIC" : 
        						o.getClass().getName() + System.identityHashCode(o);
        ProbedNode pb = new ProbedNode(tr.toString(), s,
                clazz, metodo, "");
        ArrayList probedNodes;

        if (threadsAndProbs.containsKey(pb)) {
            probedNodes = (ArrayList) threadsAndProbs.get(pb);
        } else {
            probedNodes = new ArrayList();
            threadsAndProbs.put(pb, probedNodes);
        }
        probedNodes.add(nest + ":" + n);
        //System.out.println("Probed: " + threadsAndProbs.size());
    }
	
    /** This method registers the execution of a given node 
     of an static method */
    static public void probe(String clazz, int metodo, long nest, Object n) {
        probe(null, clazz, metodo, nest, n);
    }
	
    synchronized static void dumpNodes(ProbedNode pbdNode, ArrayList probedNodes) {
        if (stdout) // dump in a text mode.
        {
            fp.println("Number of probe sequences: " + threadsAndProbs.size());
            fp.println();
            fp.println("-------------------------------");
            fp.println(pbdNode);
            fp.println("-------------------------------");
            ListIterator li = probedNodes.listIterator();

            while (li.hasNext()) {
                Object o = li.next();

                fp.println(o);
            }
        } else // dump in a binary mode
        {
            try {
            	fp.println(READER_CLASS);
            	fp.println(tcName); // nome do caso de teste
                fp.println(pbdNode.threadCode);
                fp.println(pbdNode.objectCode);
                fp.println(pbdNode.clazz);
                fp.println(pbdNode.metodo);
                ListIterator li = probedNodes.listIterator();

                while (li.hasNext()) {
                    Object o = li.next();

                    fp.println(o);
                }
                fp.println("-1");
            } catch (Exception e) {}
        }
    }
    
    static private long nestlevel = 0;
    
    static synchronized public long getNest()
    {
    	return nestlevel++;
    }
	
}


class DefaultProberHook extends Thread {
    public void run() {
        DefaultProber.dump();
    }
}
