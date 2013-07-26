/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestLage;

/**
 *
 * @author tiendv
 */
import java.io.IOException;
import java.util.*;

import org.apache.commons.math3.linear.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.*;
import org.apache.lucene.search.DocIdSetIterator;

public class DocumentSimilarityTFIDF {

    public static final String CONTENT = "Content";
    public static final int N = 2;//Total number of documents
    Directory _directory;
    private Object lock = new Object();
    private final Set<String> terms = new HashSet<>();
    private final Set<String> allterms = new HashSet<>();
    
    public DocumentSimilarityTFIDF() {
    }
    /*
     * Indexed, tokenized, stored.
     */
    public static final FieldType TYPE_STORED = new FieldType();

    static {
        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setStored(true);
        TYPE_STORED.setStoreTermVectors(true);
        TYPE_STORED.setStoreTermVectorPositions(true);
        TYPE_STORED.freeze();
    }

    void addDocument(IndexWriter writer, String content) throws IOException {
        Document doc = new Document();
        Field field = new Field(CONTENT, content, TYPE_STORED);
        doc.add(field);
        writer.addDocument(doc);
    }

    public double getCosineSimilarityWhenIndexAllDocument(int authorOneID, int authorTwoID) throws IOException {
        IndexReader reader = DirectoryReader.open(_directory);
        Map<String, Double> f1 = getWieghts(reader, authorOneID);
        Map<String, Double> f2 = getWieghts(reader, authorTwoID);
        reader.close();
        RealVector v1 = toRealVector(f1);
        System.out.println("V1: " + v1);
        RealVector v2 = toRealVector(f2);
        System.out.println("V2: " + v2);

        if (v1 != null && v2 != null) {
            System.out.println("Result similarity" + (v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm()));
            return (v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm());

        } else {
            System.out.println("Result similarity: 0");
            return 0;
        }
    }

    public void indexAllDocument(HashMap<Integer, String> allDocument) throws IOException {
        _directory = new RAMDirectory();
        Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT,
                analyzer);
        System.out.println("======START INDEX ALL DOCUMENTS=====");
        IndexWriter writer = new IndexWriter(_directory, iwc);
        for (int i = 0; i < allDocument.size(); i++) {
            addDocument(writer, allDocument.get(i));
            allterms.add(allDocument.get(i));
        }
        writer.close();
        System.out.println("====== End INDEX ALL =====");
    }

    Map<String, Double> getWieghts(IndexReader reader, int docId)
            throws IOException {
        Terms vector = reader.getTermVector(docId, CONTENT);
        Map<String, Integer> docFrequencies = new HashMap<>();
        Map<String, Integer> termFrequencies = new HashMap<>();
        Map<String, Double> tf_Idf_Weights = new HashMap<>();
        TermsEnum termsEnum = null;
        DocsEnum docsEnum = null;

        termsEnum = vector.iterator(termsEnum);
        BytesRef text = null;
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            int docFreq = termsEnum.docFreq();
            docFrequencies.put(term, reader.docFreq(new Term(CONTENT, term)));

            docsEnum = termsEnum.docs(null, null);
            while (docsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                termFrequencies.put(term, docsEnum.freq());
            }

            terms.add(term);
        }

        for (String term : docFrequencies.keySet()) {
            int tf = termFrequencies.get(term);
            int df = docFrequencies.get(term);
            
           // Code cua Tien 
            
           double idf = 1+ ( Math.log(N) - Math.log(df));
           double w = tf * idf;
            
           // code theo cong thuc của prof
            
//            double idf = (Math.log(N)- Math.log(df));  
//            double temp =1;
//            if(tf!=0)
//                temp =  (1+ (Math.log( tf)));
//            double w = temp * idf;         
                   
            tf_Idf_Weights.put(term, w);
            System.out.printf("Term: %s - tf: %d, df: %d, idf: %f, w: %f\n", term, tf, df, idf, w);
        }

       // System.out.println( "Printing docFrequencies:" );
     //   printMap(docFrequencies);

     //   System.out.println( "Printing termFrequencies:" );
     //   printMap(termFrequencies);

     //   System.out.println( "Printing if/idf weights:" );
     //        printMapDouble(tf_Idf_Weights);
        return tf_Idf_Weights;
    }

    RealVector toRealVector(Map<String, Double> map) {
        RealVector vector = new ArrayRealVector(terms.size());
        int i = 0;
        double value = 0;
        synchronized (lock) {

            for (String term : terms) {
                if (map.containsKey(term)) {
                    value = map.get(term);
                } else {
                    value = 0;
                }
                vector.setEntry(i++, value);
            }
        }
        return vector;
    }

    public static void printMap(Map<String, Integer> map) {
        for (String key : map.keySet()) {
            System.out.println("Term: " + key + ", value: " + map.get(key));
        }
    }
    public static void printMapDouble(Map<String, Double> map) {
        for ( String key : map.keySet() ) {
            System.out.println( "Term: " + key + ", value: " + map.get(key) );
        }
    }
}