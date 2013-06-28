/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testlucene;

import java.io.IOException;
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
            CosineDocumentSimilarity test = new CosineDocumentSimilarity("I have car", "you have test");
            double result =test.getCosineSimilarity();
            
            System.out.println(result);
        } catch (IOException ex) {
            Logger.getLogger(TestLucene.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
