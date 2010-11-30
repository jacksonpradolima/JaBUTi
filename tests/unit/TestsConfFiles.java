package unit;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.*;

import br.jabuti.newcmdtool.ConfProjectDAO;

public class TestsConfFiles{
	
	ConfProjectDAO confFile;
	
	// BAD TESTE :(  
	//The project have to created before and its paths have to be configured in whole code
	@Before
	public void setUp(){
		File file = new File("/home/besson/temp/testes/JabutiProj/conf/JabutiProj.jbt");
		
		confFile = new ConfProjectDAO(file);
	}
	
	@Test
	public void DirInstrum(){
		assertEquals("null",confFile.getDirPathToBeInstrum());
	}
	
	@Test
	public void DirTest(){
		assertEquals("null",confFile.getDirPathToBeTested());
	}
	
	@Test
	public void JarInstrum(){
		assertEquals("/home/besson/temp/testes/classes.jar",confFile.getJarPathToBeInstrum());
	}
	
	@Test
	public void JarTest(){
		assertEquals("/home/besson/temp/testes/testes.jar",confFile.getJarPathToBeTested());
	}
	
	@Test
	public void AvoidInstrum(){
		String expected = "";
		expected += "classeInstrumentada1" + " ";
		expected += "classeInstrumentada2";
		
		String real = confFile.getAvoidInstrum();
		
		assertEquals(expected,real);
	}
	
	@Test
	public void AvoidTest(){
		String expected = "";
		expected += "classeTeste1" + " ";
		expected += "classeTeste2";
		
		String real = confFile.getAvoidTest();
		
		assertEquals(expected,real);
		
	}
	
}
