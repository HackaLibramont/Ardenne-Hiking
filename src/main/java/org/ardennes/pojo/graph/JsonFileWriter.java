package org.ardennes.pojo.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by cvasquez on 14.03.15.
 */
public class JsonFileWriter {
    public static void writeJsonFile(Object object,String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.append(mapper.writeValueAsString(object));
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
