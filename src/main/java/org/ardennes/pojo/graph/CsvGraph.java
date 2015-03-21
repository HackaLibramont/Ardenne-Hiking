package org.ardennes.pojo.graph;

import org.ardennes.Api;
import org.ardennes.pojo.app.Event;
import org.ardennes.pojo.app.Track;
import org.ardennes.pojo.osm.Feature;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cvasquez on 10.03.15.
 */
public class CsvGraph {

    public static void main(String [] args) throws Exception
    {
        Api api = new Api();
        Track track1 =  api.getTrack("4708248");
        Track track2 =  api.getTrack("4682511");
        Track track3 =  api.getTrack("4708463");

        Set<Edge> edges = new HashSet<>();

        edges = getTrackEdges(api,edges,track1);
        edges = getTrackEdges(api,edges,track2);
        edges = getTrackEdges(api,edges,track3);

        // Quick export to the visualizer
        CsvFileWriter.writeCsvNodesGraphFile(edges, "target/edges.csv");


    }
    
    public static Set<Edge> getTrackEdges(Api api,Set<Edge> previous, Track track) throws Exception{
        
        for(Event current:api.getTrackEvents(track.getId())){

            Edge trackEvent = new Edge(track.getHref(), current.getHref());
            trackEvent.setNumber(1);

            Edge userEvent = new Edge(current.getUserHref(),current.getHref());
            userEvent.setNumber(2);


            previous.add(trackEvent);
            previous.add(userEvent);
        }
        for(Feature feature:track.getFeatures()){
            Edge trackFeature = new Edge(track.getHref(),feature.getId());
            trackFeature.setNumber(3);
            previous.add(trackFeature);
        }
        return previous;
    }
    

    
}
