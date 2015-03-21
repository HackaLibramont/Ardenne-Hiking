import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import export.ESWriter;
import org.apache.log4j.Logger;
import org.ardennes.Constants;
import org.ardennes.pojo.app.Event;
import org.ardennes.pojo.app.EventEnum;
import org.ardennes.pojo.app.Track;
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

        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("3tracks.geojson");
        ObjectMapper objectMapper = getMapper();
        FeatureCollection features = objectMapper.readValue(input, FeatureCollection.class);

        ESWriter writer = new ESWriter();
        /**
         * Clean
         */
        try{
            writer.deleteIndex();
        } catch (Exception e){
            
        }
        /**
         * Add some tracks
         */

        Track track1 = new Track();
        Track track2 = new Track();
        Track track3 = new Track();

        List<Feature> features1 = new ArrayList<>();
        List<Feature> features2 = new ArrayList<>();
        List<Feature> features3 = new ArrayList<>();

        track1.setId("4708248");
        track2.setId("4708463");
        track3.setId("4682511");
        
        for(Feature current:features.getFeatures()) {
            
            String thing = objectMapper.writeValueAsString(current);
            if (thing.contains(track1.getId())){
                features1.add(current);
            }
            if (thing.contains(track2.getId())){
                features2.add(current);
            }
            if (thing.contains(track3.getId())){
                features3.add(current);
            }
        }

        track1.setFeatures(features1);
        track2.setFeatures(features2);
        track3.setFeatures(features3);

        track1.setPois(Arrays.asList(getRandomPOI(), getRandomPOI(), getRandomPOI()));
        track2.setPois(Arrays.asList(getRandomPOI(), getRandomPOI(), getRandomPOI()));
        track3.setPois(Arrays.asList(getRandomPOI(), getRandomPOI(), getRandomPOI()));

        writer.write(Constants.TRACK_TYPE,track1,track1.getId());
        writer.write(Constants.TRACK_TYPE,track2,track2.getId());
        writer.write(Constants.TRACK_TYPE,track3,track3.getId());
        
        /**
         * Add some users
         */
        User alice = new User();
        alice.setUserName("Alice");
        String aliceId = writer.write(Constants.USER_TYPE,alice,alice.getId());

        User bob = new User();
        bob.setUserName("Bob");
        writer.write(Constants.USER_TYPE,bob,bob.getId());
        /**
         * Add some events
         */
        Event aliceStart = new Event();
        aliceStart.setEventType(EventEnum.COMMENTED);
        aliceStart.setEventCreationDate(new Date());
        aliceStart.setEventValue("This is a nice spot");
        aliceStart.setEventTrackId(track1.getId());
        aliceStart.setCoordinates(new Double[]{5.5344497, 49.7075366});
        aliceStart.setEventUserId(aliceId);
        writer.write(Constants.EVENT_TYPE, aliceStart);

    }

    private static Map<String, String>  getRandomPOI(){
        Map<String, String>  result = new HashMap<>();
        result.put("Attr1",new Random().toString());
        result.put("Attr2",new Random().toString());
        return result;
    }
    
}
