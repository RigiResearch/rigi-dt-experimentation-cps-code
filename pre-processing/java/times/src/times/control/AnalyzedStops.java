package times.control;

public enum AnalyzedStops {
	// North -> South
	PASO_COMERCIO_A2(604009),
	CHIMINANGOS_A2(500200),
	FLORA_A1(500250),
	SALOMIA_A1(500300),
	POPULAR_A1(500350),
	UNIDAD_DEP_A2(501852),
	// South -> North
	UNIVERSIDADES_A1(502300),
	UNIVALLE_B1(502251),
	BUITRERA_B1(502201),
	POPULAR_B2(500353),
	SALOMIA_B1(500301),
	FLORA_B1(500251),
	CHIMINANGOS_B1(500201),
	PASO_COMERCIO_B3(504016);
	
	private final long stopId;
	
	private AnalyzedStops(long stopId) {
		this.stopId = stopId;
	}
	
	public long getStopId() {
		return stopId;
	}
	
}
