package org.openthings;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.openthings.pojo.graph.Node;
import org.openthings.pojo.osm.FeatureCollection;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;

@Component
@RequestMapping("/search")
public class SearchController   {

	private static final Logger logger = Logger.getLogger(SearchController.class);
	@RequestMapping(method = RequestMethod.GET, value = "" )
	public @ResponseBody
    FeatureCollection search( @RequestParam(required = false) String limit, @RequestParam(required = false) String query ) throws Exception{
		logger.debug("search limit:"+limit+" uri:"+query);
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("3tracks.geojson");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		return objectMapper.readValue(input, FeatureCollection.class);
	}

}