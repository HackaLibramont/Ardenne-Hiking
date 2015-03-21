import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import export.ESWriter;
import org.apache.log4j.Logger;
import org.ardennes.Common;
import org.ardennes.pojo.app.Event;
import org.ardennes.pojo.app.EventEnum;
import org.ardennes.pojo.app.User;
import org.ardennes.pojo.osm.Feature;
import org.ardennes.pojo.osm.FeatureCollection;

import java.io.InputStream;
import java.util.*;

/**
 * Created by cvasquez on 20.03.15.
 */
public class InitializeDatabase {
    private static final Logger log = Logger.getLogger(InitializeDatabase.class);

    private static ObjectMapper getMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return objectMapper;
    }
    
    public static void main(String [] args) throws Exception
    {
        final String EXAMPLE_TRACK_ID= "169308891";
        
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("3tracks.geojson");
        ObjectMapper objectMapper = getMapper();
        FeatureCollection features = objectMapper.readValue(input, FeatureCollection.class);

        ESWriter writer = new ESWriter();
        /**
         * Clean
         */
        writer.deleteIndex();
        /**
         * Add some tracks
         */
        for(Feature current:features.getFeatures()) {
            current.setId(current.getId().replaceAll("way/",""));
            current.setPois(Arrays.asList(getRandomPOI(),getRandomPOI(),getRandomPOI()));
            writer.write(Common.FEATURE_TYPE,current,current.getId());
        }
        /**
         * Add some users
         */
        User alice = new User();
        alice.setUserName("Alice");
        String aliceId = writer.write(Common.USER_TYPE,alice,alice.getId());

        User bob = new User();
        bob.setUserName("Bob");
        writer.write(Common.USER_TYPE,bob,bob.getId());
        /**
         * Add some events
         */
        Event aliceStart = new Event();
        aliceStart.setEventType(EventEnum.COMMENTED);
        aliceStart.setEventCreationDate(new Date());
        aliceStart.setEventValue("This is a nice spot");
        aliceStart.setEventFeatureId(EXAMPLE_TRACK_ID);
        aliceStart.setCoordinates(new Double[]{5.5344497, 49.7075366});
        aliceStart.setEventUserId(aliceId);
        writer.write(Common.EVENT_TYPE, aliceStart);

    }

    private static Map<String, String>  getRandomPOI(){
        Map<String, String>  result = new HashMap<>();
        result.put("Attr1",new Random().toString());
        result.put("Attr2",new Random().toString());
        return result;
    }
    
}
