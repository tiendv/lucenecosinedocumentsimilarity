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

//    public static void main(String[] args) {
//        
//            DocumentSimilarityTFIDF cosSim = new DocumentSimilarityTFIDF();
//
//            System.out.println( cosSim.getCosineSimilarity() );
//    }

    public static final String CONTENT = "Content";
    public static final int N = 2;//Total number of documents
    Directory _directory;

    private final Set<String> terms = new HashSet<>();
    private  RealVector v1;
    private  RealVector v2;

    public DocumentSimilarityTFIDF() {
    }
    

    DocumentSimilarityTFIDF(String s1, String s2) throws IOException {
        Directory directory = createIndex(s1, s2);
        IndexReader reader = DirectoryReader.open(directory);
        Map<String, Double> f1 = getWieghts(reader, 0);
        Map<String, Double> f2 = getWieghts(reader, 1);
        reader.close();
        v1 = toRealVector(f1);
        System.out.println( "V1: " +v1 );
        v2 = toRealVector(f2);
        System.out.println( "V2: " +v2 );
    }

    Directory createIndex(String s1, String s2) throws IOException {
        Directory directory = new RAMDirectory();
        Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT,
                analyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);
        addDocument(writer, s1);
        addDocument(writer, s2);
        writer.close();
        return directory;
    }

    /* Indexed, tokenized, stored. */
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

    double getCosineSimilarity() {
        double dotProduct = v1.dotProduct(v2);
//        System.out.println( "Dot: " + dotProduct);
//        System.out.println( "V1_norm: " + v1.getNorm() + ", V2_norm: " + v2.getNorm() );
        double normalization = (v1.getNorm() * v2.getNorm());
//        System.out.println( "Norm: " + normalization);
        return dotProduct / normalization;
    }
     public double getCosineSimilarityWhenIndexAllDocument( int authorOneID, int authorTwoID) throws IOException {
        IndexReader reader = DirectoryReader.open(_directory);
        Map<String, Double> f1 = getWieghts(reader, authorOneID);
        Map<String, Double> f2 = getWieghts(reader, authorTwoID);
        reader.close();
        v1 = toRealVector(f1);
     //   System.out.println( "V1: " +v1 );
        v2 = toRealVector(f2);
      //  System.out.println( "V2: " +v2 );
        double dotProduct = v1.dotProduct(v2);
//        System.out.println( "Dot: " + dotProduct);
//        System.out.println( "V1_norm: " + v1.getNorm() + ", V2_norm: " + v2.getNorm() );
        double normalization = (v1.getNorm() * v2.getNorm());
//        System.out.println( "Norm: " + normalization);
        return dotProduct / normalization;
    }
    
       public void indexAllDocument(HashMap<Integer, String> allDocument) throws IOException {
        _directory = new RAMDirectory();
        Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT,
                analyzer);
        System.out.println("======START INDEX ALL DOCUMENTS=====");
        IndexWriter writer = new IndexWriter(_directory, iwc);
        for (int i=0; i<allDocument.size();i++)
        {
            addDocument(writer, allDocument.get(i));
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
            docFrequencies.put(term, reader.docFreq( new Term( CONTENT, term ) ));

            docsEnum = termsEnum.docs(null, null);
            while (docsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                termFrequencies.put(term, docsEnum.freq());
            }

            terms.add(term);
        }

        for ( String term : docFrequencies.keySet() ) {
            int tf = termFrequencies.get(term);
            int df = docFrequencies.get(term);
            double idf = ( 1 + Math.log(N) - Math.log(df) );
            double w = tf * idf;
            tf_Idf_Weights.put(term, w);
           // System.out.printf("Term: %s - tf: %d, df: %d, idf: %f, w: %f\n", term, tf, df, idf, w);
        }
//
//        System.out.println( "Printing docFrequencies:" );
//        printMap(docFrequencies);
//
//        System.out.println( "Printing termFrequencies:" );
//        printMap(termFrequencies);
//
//        System.out.println( "Printing if/idf weights:" );
//        printMapDouble(tf_Idf_Weights);
        return tf_Idf_Weights;
    }

    RealVector toRealVector(Map<String, Double> map) {
        RealVector vector = new ArrayRealVector(terms.size());
        int i = 0;
        double value = 0;
        for (String term : terms) {

            if ( map.containsKey(term) ) {
                value = map.get(term);
            }
            else {
                value = 0;
            }
            vector.setEntry(i++, value);
        }
        return vector;
    }

    public static void printMap(Map<String, Integer> map) {
        for ( String key : map.keySet() ) {
            System.out.println( "Term: " + key + ", value: " + map.get(key) );
        }
    }

    public static void printMapDouble(Map<String, Double> map) {
        for ( String key : map.keySet() ) {
            System.out.println( "Term: " + key + ", value: " + map.get(key) );
        }
    }

}