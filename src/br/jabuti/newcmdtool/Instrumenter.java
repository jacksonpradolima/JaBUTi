package br.jabuti.newcmdtool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.cli.CommandLine;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;

import br.jabuti.probe.ProberInstrum;



public class Instrumenter {
	
	String prjFile = null;
	String jar = null;
	String classes = null;
	String avoid = null;
	ConfProjectDAO dao;
	public static int BUFFER_SIZE = 10240;

	
	public Instrumenter(CommandLine cl){
		this.prjFile = cl.getOptionValue("instrument");
		String confFile = prjFile + "/conf/" + prjFile + ".jbt";
		dao = new ConfProjectDAO(new File(confFile));
		instrument();
	}
	
	public void getInputs(){
		jar = dao.getJarPathToBeInstrum();
		classes = dao.getDirPathToBeInstrum();
		//avoid = dao.getAvoidInstrum();
	}
	
	public void instrument(){
		getInputs();
		File out = new File(prjFile + "/files/"+prjFile+"_instrum.jar");
		String args[] = new String[4];
		args[0] = "-jar";
		args[2] = "-o";
		args[3] = out.getAbsolutePath();
		
		if(!jar.equals("null")){
			System.out.println("Instrumenting the jar package "+jar+ " ...");
			args[1] = jar;
		}
		
		//Generate a package for original classes directory
		else
		{
			File clsDir = new File(classes);
			String orginalCls = prjFile+"/files/classes.jar";
			
			System.out.println("Instrumenting the classes of "+clsDir);

			
			File temp[] = clsDir.listFiles();
			getClasses(clsDir);
			listDir("", clsDir);
			ArrayList<File> packages = cls;
			
			HashSet rs = new HashSet();
			
			Iterator<File> it = packages.iterator();
			Iterator<String> it1 = temp1.iterator();
			
			File outFile = new File(orginalCls);
			FileOutputStream fos = null;
			JarOutputStream outJar = null;
			FileInputStream f = null;


			try {
				fos = new FileOutputStream(outFile);
				outJar = new JarOutputStream(fos);
				while(it.hasNext()){
					File clName = it.next();
					byte b[] = new byte[(int) clName.length()];
					JarEntry jarEntry = new JarEntry(it1.next());
					outJar.putNextEntry(jarEntry);
					f = new FileInputStream(clName);
					f.read(b);
					outJar.write(b);
				}
				outJar.close();
				args[1] = orginalCls;

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		addPath(classes);
		
		ProberInstrum instrum = new ProberInstrum();
		try {
			instrum.main(args);
			System.out.println("A jar package containing the classes instruments was created in "+args[3]);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	ArrayList<File> cls = new ArrayList<File>();
	public void getClasses(File file){

		if(file.isDirectory()){
			
			File[] files = file.listFiles();
			
			for(int i=0;i<files.length;i++)
				getClasses(files[i]);	
		}
		
		else
		{
			if(file.getName().endsWith(".class"))
				cls.add(file.getAbsoluteFile());
				
		}
	}
	
	  public void createJarArchive(File archiveFile, File[] tobeJared) {
		  
	    try {
	      byte buffer[] = new byte[BUFFER_SIZE];
	      // Open archive file
	      FileOutputStream stream = new FileOutputStream(archiveFile);
	      JarOutputStream out = new JarOutputStream(stream, new Manifest());

	      for (int i = 0; i < tobeJared.length; i++) {
	        if (tobeJared[i] == null || !tobeJared[i].exists()
	            || tobeJared[i].isDirectory())
	          continue; // Just in case...
	        System.out.println("Adding " + tobeJared[i].getName());

	        // Add archive entry
	        JarEntry jarAdd = new JarEntry(tobeJared[i].getName());
	        jarAdd.setTime(tobeJared[i].lastModified());
	        out.putNextEntry(jarAdd);

	        // Write file to archive
	        FileInputStream in = new FileInputStream(tobeJared[i]);
	        while (true) {
	          int nRead = in.read(buffer, 0, buffer.length);
	          if (nRead <= 0)
	            break;
	          out.write(buffer, 0, nRead);
	        }
	        in.close();
	      }

	      out.close();
	      stream.close();
	      System.out.println("Adding completed OK");
	    } catch (Exception ex) {
	      ex.printStackTrace();
	      System.out.println("Error: " + ex.getMessage());
	    }
	  }
	  
	  String dirName = "classes";
	  ArrayList<String> temp1 = new ArrayList<String>();
	  public void listDir(String relativeName, File file){
			relativeName += file.getName();

			if(file.isDirectory()){
				relativeName += "/";
				
				File[] files = file.listFiles();
				
				for(int i=0;i<files.length;i++)
					listDir(relativeName,files[i]);	
			}
			
			else
			{
				if(relativeName.endsWith(".class"))
					temp1.add(relativeName.substring(dirName.length()+1,relativeName.length()));				
			}
		}
	  
	  
	  public static void addPath(String s) {
		  File f = new File(s);
		  URL u = null;
		  Method method = null;

		try {
			u = f.toURL();
			URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			  Class urlClass = URLClassLoader.class;
				method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
				  method.setAccessible(true);
					method.invoke(urlClassLoader, new Object[]{u});

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
}
