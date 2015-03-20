import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import export.ESWriter;
import org.apache.log4j.Logger;
import org.ardennes.pojo.osm.Feature;
import org.ardennes.pojo.osm.FeatureCollection;

import java.io.InputStream;

/**
 * Created by cvasquez on 20.03.15.
 */
public class InitializeDatabase {
    private static final Logger log = Logger.getLogger(InitializeDatabase.class);

    public static void main(String [] args) throws Exception
    {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("osm/3tracks.geojson");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        FeatureCollection features = objectMapper.readValue(input, org.ardennes.pojo.osm.FeatureCollection.class);
        ESWriter writer = new ESWriter();
        for(Feature current:features.getFeatures()) {
            writer.write(current,"/entity/feature");
        }
    }
}
