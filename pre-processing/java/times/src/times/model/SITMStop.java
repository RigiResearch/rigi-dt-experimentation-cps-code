package times.model;

public class SITMStop{
	
	private long stopId;
	private String shortName;
	private String longName;
	private double GPSX;
	private double GPSY;
	private double decimalLongitude;
	private double decimalLatitude;
	private long planVersionId;
	
	public SITMStop () {
		super();
	}

	public SITMStop(long stopId, String shortName, String longName, double gPSX, double gPSY, double decimalLongitude, double decimalLatitude, long planVersionId) {
		this.stopId = stopId;
		this.shortName = shortName;
		this.longName = longName;
		this.GPSX = gPSX;
		this.GPSY = gPSY;
		this.decimalLongitude = decimalLongitude;
		this.decimalLatitude = decimalLatitude;
		this.planVersionId = planVersionId;
	}

	public long getStopId() {
		return stopId;
	}

	public void setStopId(long stopId) {
		this.stopId = stopId;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public double getGPSX() {
		return GPSX;
	}

	public void setGPSX(double gPSX) {
		GPSX = gPSX;
	}

	public double getGPSY() {
		return GPSY;
	}

	public void setGPSY(double gPSY) {
		GPSY = gPSY;
	}

	public double getDecimalLongitude() {
		return decimalLongitude;
	}

	public void setDecimalLongitude(double decimalLongitude) {
		this.decimalLongitude = decimalLongitude;
	}

	public double getDecimalLatitude() {
		return decimalLatitude;
	}

	public void setDecimalLatitude(double decimalLatitude) {
		this.decimalLatitude = decimalLatitude;
	}

	public long getPlanVersionId() {
		return planVersionId;
	}

	public void setPlanVersionId(long planVersionId) {
		this.planVersionId = planVersionId;
	}
	
	
	
}