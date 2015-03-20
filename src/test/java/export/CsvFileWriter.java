package export;


import org.ardennes.pojo.graph.Edge;
import org.ardennes.pojo.graph.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvFileWriter {

    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    public static void writeCsvEdgesFile(List<Edge> edges,String fileName) {
        final String FILE_HEADER = "source,target,id";
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.append(FILE_HEADER.toString());
            fileWriter.append(NEW_LINE_SEPARATOR);
            for (Edge current : edges) {
                fileWriter.append(current.getSource());
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(current.getTarget());
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(current.getId()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }
            System.out.println(fileName+" created");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeCsvNodesFile(List<Node> nodes,String fileName) {
        final String FILE_HEADER = "id,label";
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.append(FILE_HEADER.toString());
            fileWriter.append(NEW_LINE_SEPARATOR);
            for (Node current : nodes) {
                fileWriter.append(current.getId());
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(current.getLabel());
                fileWriter.append(NEW_LINE_SEPARATOR);
            }
            System.out.println(fileName+" created");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
