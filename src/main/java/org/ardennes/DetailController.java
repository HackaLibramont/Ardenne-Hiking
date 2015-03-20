package org.openthings;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.openthings.pojo.osm.Feature;
import org.openthings.pojo.osm.FeatureCollection;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;

@Component
@RequestMapping("/detail")
public class DetailController {

	private static final Logger logger = Logger.getLogger(DetailController.class);

	@RequestMapping(method = RequestMethod.GET, value = "/{a}/{b}" )
	@ResponseBody
    public Feature feature(@PathVariable("a") String a,@PathVariable("b") String b) throws Exception{
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("3tracks.geojson");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        FeatureCollection collection = objectMapper.readValue(input, FeatureCollection.class);
        for(Feature current: collection.getFeatures()){
            if (current.getId().equalsIgnoreCase(a+"/"+b)){
                return current;
            }
        }
		return null;
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