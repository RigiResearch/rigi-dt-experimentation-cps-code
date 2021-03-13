package times.model;
import java.util.Date;


public class SITMLineStop {
	
	private long lineStopId;
	private long stopsequence;
	private long orientation;
	private long lineId;
	private long stopId;
	private long planVersionId;
	private long lineVariant;
	private Date registerDate;
	private long lineVariantType;

	public SITMLineStop () {
		super();
	}
	
	public SITMLineStop(long lineStopId, long stopsequence, long orientation, long lineId, long stopId, long planVersionId, long lineVariant, Date registerDate, long lineVariantType) {
		this.lineStopId = lineStopId;
		this.stopsequence = stopsequence;
		this.orientation = orientation;
		this.lineId = lineId;
		this.stopId = stopId;
		this.planVersionId = planVersionId;
		this.lineVariant = lineVariant;
		this.registerDate = registerDate;
		this.lineVariantType = lineVariantType;
	}

	public long getLineStopId() {
		return lineStopId;
	}

	public void setLineStopId(long lineStopId) {
		this.lineStopId = lineStopId;
	}

	public long getStopsequence() {
		return stopsequence;
	}

	public void setStopsequence(long stopsequence) {
		this.stopsequence = stopsequence;
	}

	public long getOrientation() {
		return orientation;
	}

	public void setOrientation(long orientation) {
		this.orientation = orientation;
	}

	public long getLineId() {
		return lineId;
	}

	public void setLineId(long lineId) {
		this.lineId = lineId;
	}

	public long getStopId() {
		return stopId;
	}

	public void setStopId(long stopId) {
		this.stopId = stopId;
	}

	public long getPlanVersionId() {
		return planVersionId;
	}

	public void setPlanVersionId(long planVersionId) {
		this.planVersionId = planVersionId;
	}

	public long getLineVariant() {
		return lineVariant;
	}

	public void setLineVariant(long lineVariant) {
		this.lineVariant = lineVariant;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public long getLineVariantType() {
		return lineVariantType;
	}

	public void setLineVariantType(long lineVariantType) {
		this.lineVariantType = lineVariantType;
	}
	
	
}
