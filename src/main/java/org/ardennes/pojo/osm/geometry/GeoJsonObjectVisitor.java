package org.openthings.pojo.osm.geometry;

public interface GeoJsonObjectVisitor<T> {

	T visit(GeometryCollection geoJsonObject);

	T visit(org.openthings.pojo.osm.FeatureCollection geoJsonObject);

	T visit(Point geoJsonObject);

	T visit(org.openthings.pojo.osm.Feature geoJsonObject);

	T visit(MultiLineString geoJsonObject);

	T visit(Polygon geoJsonObject);

	T visit(MultiPolygon geoJsonObject);

	T visit(MultiPoint geoJsonObject);

	T visit(LineString geoJsonObject);
}
