package org.ardennes.pojo.osm.geometry;

public class LineString extends MultiPoint {

	public LineString() {
	}

	public LineString(org.ardennes.pojo.osm.geometry.LngLatAlt... points) {
		super(points);
	}

	@Override
	public <T> T accept(GeoJsonObjectVisitor<T> geoJsonObjectVisitor) {
		return geoJsonObjectVisitor.visit(this);
	}

	@Override
	public String toString() {
		return "LineString{} " + super.toString();
	}
}
