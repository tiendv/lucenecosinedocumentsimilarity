/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testlucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tiendv
 */
public class TestLucene {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ArrayList result = new ArrayList();
            ArrayList listAuthor = LoadData.loadTextFromFile("C:\\Data\\");
            for(int i=0 ; i< listAuthor.size();i++)
            {
                for(int j=i+1; j<listAuthor.size();j++)
                {
                    CosineDocumentSimilarity cosinSmilar = new CosineDocumentSimilarity (listAuthor.get(i).toString(),listAuthor.get(j).toString());
                     result.add(cosinSmilar.getCosineSimilarity());
                }
               
                
            }
            for(int j =0; j< result.size();j++)
            {
                System.out.println(result.get(j).toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(TestLucene.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
