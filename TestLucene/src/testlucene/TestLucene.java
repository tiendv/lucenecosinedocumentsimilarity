/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testlucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
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
            double result;;
            Map listAuthor = LoadData.loadFileFromForder("C:\\Data\\");
            for(int i=0 ; i< listAuthor.size();i++)
            {             
                Author authorOne = (Author) listAuthor.get(i);
                for(int j=i+1; j<listAuthor.size();j++)
                    
                {    
                    Author authorTwo = (Author) listAuthor.get(j);
                    CosineDocumentSimilarity cosinSmilar = new CosineDocumentSimilarity (authorOne.paperContent,authorTwo.paperContent);
                    result = cosinSmilar.getCosineSimilarity();   
                    System.out.println(" Cosin similarity of Author"+ authorOne.getName()+"and"+ authorTwo.getName()+":"+result);
                     
                }
               
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(TestLucene.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
