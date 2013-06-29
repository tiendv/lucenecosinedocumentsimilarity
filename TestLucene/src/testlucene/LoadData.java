/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testlucene;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author tiendv
 */
public class LoadData 
{ 
    public static ArrayList loadTextFromFile (String rootPath) throws FileNotFoundException, IOException
    {
        ArrayList result = new ArrayList();
        File mainFolder = new File(rootPath);
       
            System.out.println(mainFolder.getAbsolutePath());
            File[] subFolderList = mainFolder.listFiles();
            for (int i = 0; i < subFolderList.length; i++) {
                    result.add(i, readFile(subFolderList[i]));
                
            }
        return result;
    }
 public static String readFile(File file) throws FileNotFoundException, IOException
 {
      int len;
      char[] chr = new char[4096];
      final StringBuffer buffer = new StringBuffer();
      final FileReader reader = new FileReader(file);
      try {
                try {
                    while ((len = reader.read(chr)) > 0) {
                        buffer.append(chr, 0, len);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LoadData.class.getName()).log(Level.SEVERE, null, ex);
                }
      } finally {
          reader.close();
      }
      return buffer.toString().substring(buffer.toString().indexOf('\n')+1);

}

}
