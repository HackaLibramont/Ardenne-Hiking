package org.ardennes;

import org.apache.log4j.Logger;
import org.ardennes.pojo.osm.Feature;
import org.ardennes.pojo.app.User;
import org.ardennes.pojo.app.Event;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@RequestMapping("/entity")
public class DetailController {

	private static final Logger logger = Logger.getLogger(DetailController.class);

	@RequestMapping(method = RequestMethod.GET, value = "feature/way/{id}" )
	@ResponseBody
    public Feature way(@PathVariable("id") String id) throws Exception{
        return Mock.getFeature(id);
	}

    @RequestMapping(method = RequestMethod.GET, value = "user/{id}" )
    @ResponseBody
    public User user(@PathVariable("id") String id) throws Exception{
        return Mock.getUser(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "entity/user/{featureId}" )
    @ResponseBody
    public Event getEventsForFeature(@PathVariable("featureId") String featureId) throws Exception{
        return Mock.getEvent(featureId);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "entity/event/{featureId}/{userId}" )
    @ResponseBody
    public Event getEventsForUserInCertainFeature(@PathVariable("featureId") String featureId,@PathVariable("userId") String userId) throws Exception{
        return Mock.getEvent(featureId,userId);
    }
    
//	@RequestMapping(method = RequestMethod.POST, value = "" )
//	public ResponseEntity<Void> addRelation( @RequestBody String input ) {
//	    logger.debug("POST relation");
//	    ObjectMapper mapper = new ObjectMapper();
//        Edge annotation;
//		try {
//			annotation = mapper.readValue(new StringReader(input), Edge.class);
//		    logger.debug("In:"+ToStringBuilder.reflectionToString(annotation));
//		} catch (Exception e) {
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		HttpHeaders responseHeaders = new HttpHeaders();
//		responseHeaders.set("Location", Root.URIQA+"/ontology/"+"mockId");
//		return new ResponseEntity<>(responseHeaders,HttpStatus.SEE_OTHER);
//	}

}