package org.ardennes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.ardennes.pojo.app.Event;
import org.ardennes.pojo.app.Track;
import org.ardennes.pojo.app.User;
import org.ardennes.pojo.osm.FeatureCollection;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        ESWriter writer = new ESWriter();
        /**
         * Clean
         */
        try{
            writer.deleteIndex();
        } catch (Exception e){

        }
        loadUser(writer, "Alice","alice");
        loadUser(writer,"Bob","bob");

        loadTrack("track1", writer, "4708248");
        loadTrack("track2",writer,"4682511");
        loadTrack("track3", writer, "4708463");

        loadEvent(writer, "events/01_aliceStarts.json");
        loadEvent(writer, "events/02_bobStarts.json");
        loadEvent(writer, "events/03_aliceTakeAPhoto.json");
        loadEvent(writer, "events/04_alicecommented.json");
        loadEvent(writer, "events/05_bobcommented.json");

    }
    
    private static String loadUser(ESWriter writer, String userName,String userId)  throws Exception {
        User alice = new User();
        alice.setUserName(userName);
        return writer.write(Constants.USER_TYPE,alice,userId);
    }

    private static void loadEvent(ESWriter writer, String file)  throws Exception {
        ObjectMapper objectMapper = getMapper();
        InputStream trackFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        Event event = objectMapper.readValue(trackFile, Event.class);
        writer.write(Constants.EVENT_TYPE, event);
    }


    private static void loadTrack(String directory, ESWriter writer, String trackId)  throws Exception {
        ObjectMapper objectMapper = getMapper();

        InputStream trackFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(directory + "/track.json");
        FeatureCollection featureCollection = objectMapper.readValue(trackFile, FeatureCollection.class);

        Track track = new Track();
        track.setFeatures(featureCollection.getFeatures());
        track.setPois(Arrays.asList(getRandomPOI(), getRandomPOI(), getRandomPOI()));


        InputStream bookFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(directory + "/book.json");
        Map<String,String> info = objectMapper.readValue(bookFile, Map.class);
        track.setInfo(info);


        writer.write(Constants.TRACK_TYPE,track,trackId);
    }

    private static Map<String, String>  getRandomPOI(){
        Map<String, String>  result = new HashMap<>();
        result.put("Attr1",new Random().toString());
        result.put("Attr2",new Random().toString());
        return result;
    }
    
}
