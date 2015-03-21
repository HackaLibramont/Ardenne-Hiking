package org.ardennes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.ardennes.pojo.app.Event;
import org.ardennes.pojo.app.User;
import org.ardennes.pojo.osm.Feature;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequestMapping("/")
public class Api {

	private static final Logger log = Logger.getLogger(Api.class);
    private final TransportClient client;
    private final ObjectMapper mapper;
    
    public Api() {
        this.mapper = Common.getMapper();
        log.info("Starting up Elastic Search connection");
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", Common.CLUSTER)
                .put("client.transport.sniff", true)
                .build();
        this.client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(
                        "localhost", 9300));
    }

    /**
     root
     method: GET
     path: /
     returns: object containing store metadata, including API version
     Example:

     $ curl http://example.com/api/
     {
     "name": "Server prototype API",
     "version": "1.0.0"
     }
     */
    @RequestMapping(method = RequestMethod.GET, value = "" )
    public @ResponseBody Map<String, String> root(){
        log.debug("GET: root");
        Map<String, String> result = new HashMap<String, String>();
        result.put("name", "Server prototype API");
        result.put("version", "1.0.0");
        return result;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "search" )
    public @ResponseBody
    List<Feature> search( @RequestParam(required = false) String limit, @RequestParam(required = false) String query ) throws Exception {
        /**
         * This can be done directly to elastic search, but I want to add some stuff after
         */
        SearchResponse response = client.prepareSearch(Common.INDEX)
                .setTypes(Common.FEATURE_TYPE)
                .execute()
                .actionGet();
        List<Feature> result = new ArrayList<>();
        for(SearchHit current:response.getHits()){
            result.add(mapper.readValue(current.getSourceAsString(), org.ardennes.pojo.osm.Feature.class));
        }
        return result;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "feature/{featureId}" )
	@ResponseBody
    public Feature way(@PathVariable("featureId") String featureId) throws Exception{
        GetResponse response = client.prepareGet(Common.INDEX, Common.FEATURE_TYPE, featureId)
                .setOperationThreaded(false)
                .execute()
                .actionGet();
        Feature current = mapper.readValue(response.getSourceAsString(), Feature.class);
        current.setEvents(getFeatureEvents(featureId));
        return current;
	}

    
    @RequestMapping(method = RequestMethod.GET, value = "user/{userId}" )
    @ResponseBody
    public User user(@PathVariable("userId") String userId) throws Exception{
        GetResponse response = client.prepareGet(Common.INDEX, Common.USER_TYPE, userId)
                .setOperationThreaded(false)
                .execute()
                .actionGet();
        User current = mapper.readValue(response.getSourceAsString(), User.class);
        current.setEvents(getUserEvents(userId));
        return current;
    }

    @RequestMapping(method = RequestMethod.GET, value = "poi/{poiId}" )
    @ResponseBody
    public Map poi(@PathVariable("userId") String userId) throws Exception{
        GetResponse response = client.prepareGet(Common.INDEX, Common.POI_TYPE, userId)
                .setOperationThreaded(false)
                .execute()
                .actionGet();
        Map current = mapper.readValue(response.getSourceAsString(), Map.class);
        return current;
    }


    @RequestMapping(method = RequestMethod.POST, value = "" )
	public ResponseEntity<Void> addEvent( @RequestBody Event input ) throws Exception {
        String id = client.prepareIndex(Common.INDEX, Common.EVENT_TYPE)
                .setSource(mapper.writeValueAsString(input))
                .execute()
                .actionGet().getId();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Location", Common.getEventURL(id));
		return new ResponseEntity<>(responseHeaders, HttpStatus.SEE_OTHER);
	}


    public List<Event> getFeatureEvents(String id) throws Exception{
        QueryBuilder qb = QueryBuilders.matchQuery("eventFeatureId", id);
        SearchResponse response = client.prepareSearch(Common.INDEX)
                .setTypes(Common.EVENT_TYPE)
                .setQuery(qb)
                .execute()
                .actionGet();
        List<Event> result = new ArrayList<>();
        for(SearchHit current:response.getHits()){
            result.add(mapper.readValue(current.getSourceAsString(), Event.class));
        }
        return result;
    }
    
    public List<Event> getUserEvents(String id) throws Exception{
        QueryBuilder qb = QueryBuilders.matchQuery("eventUserId", id);
        SearchResponse response = client.prepareSearch(Common.INDEX)
                .setTypes(Common.EVENT_TYPE)
                .setQuery(qb)
                .execute()
                .actionGet();
        List<Event> result = new ArrayList<>();
        for(SearchHit current:response.getHits()){
            result.add(mapper.readValue(current.getSourceAsString(), Event.class));
        }
        return result;
    }

}