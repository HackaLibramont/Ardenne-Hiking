package org.ardennes.pojo.osm;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ardennes.Common;
import org.ardennes.pojo.app.Event;
import org.ardennes.pojo.app.User;
import org.ardennes.pojo.osm.geometry.GeoJsonObjectVisitor;

import java.util.List;
import java.util.Map;

public class Feature extends org.ardennes.pojo.osm.GeoJsonObject {

    @JsonProperty("URL")
    private String getHref(){
        return Common.getFeatureURL(getId());
    }

    private List<Event> events;
    public List<Event> getEvents() {
        return events;
    }
    public List<Map<String,String>> pois;

    public List<Map<String, String>> getPois() {
        return pois;
    }

    public void setPois(List<Map<String, String>> pois) {
        this.pois = pois;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    private GeoJsonObject geometry;
	private String id;

	public GeoJsonObject getGeometry() {
		return geometry;
	}

	public void setGeometry(GeoJsonObject geometry) {
		this.geometry = geometry;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public <T> T accept(GeoJsonObjectVisitor<T> geoJsonObjectVisitor) {
		return geoJsonObjectVisitor.visit(this);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feature)) return false;
        if (!super.equals(o)) return false;

        Feature feature = (Feature) o;

        if (id != null ? !id.equals(feature.id) : feature.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return "Feature{" + "geometry=" + geometry + ", id='" + id + "'}";
	}
}
