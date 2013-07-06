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
    public static void main(String[] args) throws IOException {
 
        CosineDocumentSimilarity test = new CosineDocumentSimilarity("hello", "");
        System.out.println(test.getCosineSimilarity());
        
    }
}
