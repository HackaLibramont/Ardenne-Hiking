import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import export.JsonFileWriter;

import java.io.InputStream;


public class GeoJson {

    public static void main(String [] args) throws Exception
    {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("3tracks.geojson");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonFileWriter.writeJsonFile(objectMapper.readValue(input, org.ardennes.pojo.osm.FeatureCollection.class), "target/result.3tracks.geojson");
    }

}
