
from lxml import etree
import json


namespace = 'http://www.topografix.com/GPX/1/1'
input_file = 'track1.gpx'
output_file = 'track1.json'

f = open(input_file, 'r')
gpx = etree.parse(f)
f.close()

coordinates = []

for point in gpx.xpath('//ns:trkseg/ns:trkpt',namespaces={'ns':namespace}):
    (lat, lng) = (point.get('lat'), point.get('lon'))
    coordinates.append([float(lat), float(lng)])

result = {
  "type" : "FeatureCollection",
  "generator" : "gpsies",
  "copyright": "The data included in this document is from www.openstreetmap.org. The data is made available under ODbL.",
  "timestamp": "2015-03-20T14:34:02Z",
  "features": [
    {
      "type": "Feature",
      "id": "way/169308891",
      "properties": {
        "@id": "way/169308891",
        "@relations": [
          {
            "role": "",
            "rel": 4708463,
            "reltags": {
              "name": "Promenade Maurice Grevisse",
              "network": "lwn",
              "osmc:symbol": "blue:white:cross",
              "route": "hiking",
              "type": "route"
            }
          }
        ]
      },
      "geometry" : {
        "type": "LineString",
        "coordinates": coordinates
        }
    }
  ]
}


f = open(output_file, 'w')
f.write(json.dumps(result))
