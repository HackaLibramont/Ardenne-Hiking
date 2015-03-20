package org.openthings.pojo.osm;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openthings.UrlResolver;
import org.openthings.pojo.osm.geometry.GeoJsonObjectVisitor;

public class Feature extends GeoJsonObject {

    @JsonProperty("href")
    private String getHref(){
        return UrlResolver.getOsmEntityURL(getId());
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
