## Resources for Data Pre-Processing

### Required Files

* **`output-02-APR-19-awk-sorted.csv`** or **`datagrams.csv`**: file with the pre-processed datagrams to be used. Columns: *event_type, short_datagram_date, stopId, odometer, latitude, longitude, taskId, lineId, tripId, unkown1, datagramDate, busId*.
* **`linestops.csv`**: file the stops of lines. Columns: *LINESTOPID, STOPSEQUENCE, ORIENTATION, LINEID, STOPID, PLANVERSIONID, LINEVARIANT, REGISTERD, LINEVARIANTTYPE*
*  **`stops.csv`**: file with the list of stops. Columns: *STOPID, PLANVERSIONID, SHORTNAME, LONGNAME, GPS_X, GPS_Y, DECIMALLONGITUDE, DECIMALLATITUDE*

### Generated Files

* **`datagrams_generated.csv`**: datagrams processed and generated by `1-reformat.py` and required by *`times project`* (Eclipse Java project)
* **`interarrivalTimes.csv`**: bus interarrival times (Ai) generated by *`times project`* (Eclipse Java project)


