import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import export.JsonFileWriter;
import org.openthings.pojo.osm.FeatureCollection;

import java.io.InputStream;


public class GeoJson {

    public static void main(String [] args) throws Exception
    {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("osm/3tracks.geojson");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonFileWriter.writeJsonFile(objectMapper.readValue(input, FeatureCollection.class), "target/result.3tracks.geojson");
    }

}
