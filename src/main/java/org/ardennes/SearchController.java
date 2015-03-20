package org.ardennes;

import org.apache.log4j.Logger;
import org.ardennes.pojo.osm.FeatureCollection;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@RequestMapping("/search")
public class SearchController   {

	private static final Logger logger = Logger.getLogger(SearchController.class);
	@RequestMapping(method = RequestMethod.GET, value = "" )
	public @ResponseBody
    FeatureCollection search( @RequestParam(required = false) String limit, @RequestParam(required = false) String query ) throws Exception{
		return Mock.getAll();
	}

}