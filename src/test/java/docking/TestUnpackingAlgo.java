package docking;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.junit.Test;

/**
 * Test speed of unpacking and packing a list of files between two search algorithms (tree search versus hash search)
 * 
 * @author Allard
 *
 */
public class TestUnpackingAlgo {
	
	@Test 
	public void testTar() {
		try {
	    	TarArchiveInputStream tin = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(new File("../../Downloads/output.tar.gz"))));
	
	        TarArchiveEntry entry;
	        
	        while ((entry = tin.getNextTarEntry()) != null) {
	        	if (entry.getName().equals("best_output.csv")) {
	        		// create a file with the same name as the entry
		        	File destPath = new File("../../Downloads/test", entry.getName());
		        	
	        		destPath.createNewFile();
	                
	                byte [] bytes = new byte[1024];
	                
	                BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
	                int len = 0;
	
	                while((len = tin.read(bytes)) != -1) {
	                    bout.write(bytes, 0, len);
	                }
	                
	                bout.close();
	                
	                return;
	        	}
	        }
	        
	        tin.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}
	
    /*@Test
    public void testTree() throws IOException {
    	long startTime = System.nanoTime();
    	
    	TreeSet<String> tree = new TreeSet<String>();
    	
    	BufferedReader br = new BufferedReader(new FileReader("../../Downloads/3LFM/ZNP/best_output.csv"));
        String line = br.readLine();
        
        int fileCount = 0;
        
        while ((line = br.readLine()) != null && fileCount < fileCountMax) {
             String[] b = line.split(",");
             
             tree.add(b[0]);
             
             fileCount++;
        }
        
        br.close();
        
        TarArchiveInputStream tin = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(new File("../../Downloads/3LFM/ZNP/output.tar.gz"))));
        TarArchiveOutputStream tout = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(new File("../../Downloads/test.tar.gz"))));
        
        ArchiveEntry entry = null;
        
        long writeStartTime = System.nanoTime();
        while((entry = tin.getNextEntry()) != null) {
        	if (tree.contains(entry.getName().replace("_out.pdbqt", ""))) {
        		tout.putArchiveEntry(entry);
        		IOUtils.copy(tin, tout);
        		tout.closeArchiveEntry();
        	}
        }
        
        tout.finish();
        tout.close();
        tin.close();
    	
    	long endTime = System.nanoTime();
    	
    	System.out.println("tree");
    	
    	System.out.println(String.format("writing: %4f seconds", (endTime - writeStartTime) / 1000000000.0));
    	System.out.println(String.format("whole: %4f seconds", (endTime - startTime) / 1000000000.0));
    	
    	System.out.println(new File("../../Downloads/test.zip").exists());
    	
        assertEquals(true, true);
    }
    
    @Test
    public void testHash() throws IOException {
    	long startTime = System.nanoTime();
    	
    	HashSet<String> hash = new HashSet<String>();
    	
    	BufferedReader br = new BufferedReader(new FileReader("../../Downloads/3LFM/ZNP/best_output.csv"));
        String line = br.readLine();
        
        int fileCount = 0;
        
        while ((line = br.readLine()) != null && fileCount < fileCountMax) {
             String[] b = line.split(",");
             
             hash.add(b[0]);
             
             fileCount++;
        }
        
        br.close();

        TarArchiveInputStream tin = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(new File("../../Downloads/3LFM/ZNP/output.tar.gz"))));
        TarArchiveOutputStream tout = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(new File("../../Downloads/test.tar.gz"))));
        
        ArchiveEntry entry = null;
                
        long writeStartTime = System.nanoTime();
        
        while((entry = tin.getNextEntry()) != null) {
        	if (hash.contains(entry.getName().replace("_out.pdbqt", ""))) {
        		tout.putArchiveEntry(entry);
        		IOUtils.copy(tin, tout);
        		tout.closeArchiveEntry();
        	}
        }
        
        tout.finish();
        tout.close();
        tin.close();
    	
    	long endTime = System.nanoTime();
    	
    	System.out.println("hash");
    	
    	System.out.println(String.format("writing: %4f seconds", (endTime - writeStartTime) / 1000000000.0));
    	System.out.println(String.format("whole: %4f seconds", (endTime - startTime) / 1000000000.0));
    	
    	System.out.println(new File("../../Downloads/test.zip").exists());
    	
        assertEquals(true, true);
    }*/
}