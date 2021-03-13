package times.model;

import java.sql.Date;

public class Datagram {
	
	private long datagramDateLong;
	private String datagramDateString;
	private Date datagramDate;
	private long busId;
	private long stopId;
	private long odometer;
	private double longitude;
	private double latitude;
	private long taskId;
	private long lineId;
	private long tripId;
	
	public Datagram(Date dataDate, long datagramDateTime, String datagramDate, long busId, long stopId, long odometer, double longitude, double latitude, long taskId, long lineId, long tripId) {
		this.datagramDate = dataDate;
		this.datagramDateLong = datagramDateTime;
		this.datagramDateString = datagramDate;
		this.busId = busId;
		this.stopId = stopId;
		this.odometer = odometer;
		this.longitude = longitude;
		this.latitude = latitude;
		this.taskId = taskId;
		this.lineId = lineId;
		this.tripId = tripId;
	}

	@Override
	public String toString() {
		return "Datagram [datagramDateLong=" + datagramDateLong + ", datagramDateString=" + datagramDateString
				+ ", datagramDate=" + datagramDate + ", busId=" + busId + ", stopId=" + stopId + ", odometer="
				+ odometer + ", longitude=" + longitude + ", latitude=" + latitude + ", taskId=" + taskId + ", lineId="
				+ lineId + ", tripId=" + tripId + "]";
	}

	public long getDatagramDateLong() {
		return datagramDateLong;
	}

	public void setDatagramDateLong(long datagramDateLong) {
		this.datagramDateLong = datagramDateLong;
	}

	public String getDatagramDateString() {
		return datagramDateString;
	}

	public void setDatagramDateString(String datagramDateString) {
		this.datagramDateString = datagramDateString;
	}

	public Date getDatagramDate() {
		return datagramDate;
	}

	public void setDatagramDate(Date datagramDate) {
		this.datagramDate = datagramDate;
	}

	public long getBusId() {
		return busId;
	}

	public void setBusId(long busId) {
		this.busId = busId;
	}

	public long getStopId() {
		return stopId;
	}

	public void setStopId(long stopId) {
		this.stopId = stopId;
	}

	public long getOdometer() {
		return odometer;
	}

	public void setOdometer(long odometer) {
		this.odometer = odometer;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public long getLineId() {
		return lineId;
	}

	public void setLineId(long lineId) {
		this.lineId = lineId;
	}

	public long getTripId() {
		return tripId;
	}

	public void setTripId(long tripId) {
		this.tripId = tripId;
	}

	
	
}
