package TestLage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author TinHuynh
 */
public class TF {

    HashMap<Integer, Integer> _AuthorInstanceHM = new HashMap<>();
    HashMap<Integer, Integer> _InstanceAuthorHM = new HashMap<>();
    private static HashMap<Integer, HashMap<Integer, Float>> _tfidfHM = new HashMap<>();
    private HashMap<Integer, String> _InstancePublicationHM = new HashMap<>();
//    DocumentSimilarityTF similarityUsingTF;
    CollectionDocument indexAllDocument;
    private void Run(int inputAuthorID) {
        try {
            int currentAuthorID;
            System.out.println("CURRENT INSTANCE IS:" + inputAuthorID);
            int instanceID = getInstanceFromAuthorID(inputAuthorID);
            HashMap<Integer, Float> similarityHM = new HashMap<Integer, Float>();
                for (int otherInstanceID = 0; otherInstanceID < _InstancePublicationHM.size(); otherInstanceID++) {
                    if (instanceID != otherInstanceID) {
                        // float simValue = (float) similarityUsingTF.getCosineSimilarityWhenIndexAllDocument(instanceID, otherInstanceID);
                        currentAuthorID = getAuthorIDFromInstanceID(otherInstanceID);
                        System.out.println("Doc: " + instanceID + " Doc : " + otherInstanceID );
                         SimilarityTF similarityUsingTF = new SimilarityTF();
                        float simValue = (float) similarityUsingTF.getCosineSimilarityWhenIndexAllDocument(indexAllDocument.getTermWithAuthorID(instanceID), indexAllDocument.getTermWithAuthorID(otherInstanceID)); 
//                        float simValue = (float) similarityUsingTF.getCosineSimilarityWhenIndexAllDocument(instanceID, otherInstanceID);
                        System.out.println(simValue);
                        similarityHM.put(currentAuthorID, simValue);
                    }
                }
            _tfidfHM.put(inputAuthorID, similarityHM);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public HashMap<Integer, HashMap<Integer, Float>> process(String inputFile, ArrayList<Integer> listAuthorID) {
        System.out.println("START PROCESSING TFIDF");
        try {
            loadInstancePublication(inputFile);
            String pathFile = (new File(inputFile)).getParent();
            loadMappingInstanceIDAuthorID(pathFile + "/CRS-AuthorIDAndInstance.txt");
//            similarityUsingTF = new DocumentSimilarityTF();
//            similarityUsingTF.indexAllDocument(_InstancePublicationHM);
            indexAllDocument = new CollectionDocument();
            indexAllDocument.indexAllDocument(_InstancePublicationHM);
            indexAllDocument.openReader();

            Runtime runtime = Runtime.getRuntime();
            int numOfProcessors = runtime.availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 2);
            for (final int authorId : listAuthorID) {
                executor.submit(new Runnable() {

                    @Override
                    public void run() {
                        Run(authorId);
                    }
                });
            }

            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            indexAllDocument.closeReader();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("FINISH PROCESSING TFIDF");
        return _tfidfHM;
    }

    private int getInstanceFromAuthorID(int authorID) {
        Iterator it = _AuthorInstanceHM.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            if (pairs.getKey() == authorID) {
                return (int) pairs.getValue();
            }
            it.remove();
        }
        return 0;
    }

    private int getAuthorIDFromInstanceID(int instanceID) {
        //return _InstanceAuthorHM.get(instanceID);
        Iterator it = _InstanceAuthorHM.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            if (pairs.getKey() == instanceID) {
                return (int) pairs.getValue();
            }
            it.remove();
        }
        return 0;

    }

    private void loadMappingInstanceIDAuthorID(String mapFile) {
        try {
            FileInputStream fis = new FileInputStream(mapFile);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            bufferReader.readLine(); // skip the header line
            String line = null;
            String[] tokens;
            while ((line = bufferReader.readLine()) != null) {
                tokens = line.split("\t");
                if (tokens.length != 2) {
                    continue;
                }

                int authorID = Integer.parseInt(tokens[0]);
                int instanceID = Integer.parseInt(tokens[1]);
                _AuthorInstanceHM.put(authorID, instanceID);
                _InstanceAuthorHM.put(instanceID, authorID);
            }
            bufferReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadInstancePublication(String inputFile) {
        try {
            FileInputStream fis = new FileInputStream(inputFile);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            String line = null;
            String[] tokens;
            int instanceID = 0;
            while ((line = bufferReader.readLine()) != null) {
                _InstancePublicationHM.put(instanceID, line);
                instanceID++;
            }
            bufferReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ArrayList<Integer> ListAuthor = new ArrayList<Integer>();
        ListAuthor.add(1);
        ListAuthor.add(10);

        TF test = new TF();
        test.process(".//Data//AuthorPaper.txt", ListAuthor);

    }
}