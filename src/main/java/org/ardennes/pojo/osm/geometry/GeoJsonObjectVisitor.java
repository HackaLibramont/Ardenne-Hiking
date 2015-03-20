package org.ardennes.pojo.osm.geometry;

import org.ardennes.pojo.osm.Feature;
import org.ardennes.pojo.osm.FeatureCollection;

public interface GeoJsonObjectVisitor<T> {

	T visit(GeometryCollection geoJsonObject);

	T visit(FeatureCollection geoJsonObject);

	T visit(Point geoJsonObject);

	T visit(Feature geoJsonObject);

	T visit(MultiLineString geoJsonObject);

	T visit(Polygon geoJsonObject);

	T visit(MultiPolygon geoJsonObject);

	T visit(MultiPoint geoJsonObject);

	T visit(org.ardennes.pojo.osm.geometry.LineString geoJsonObject);
}
