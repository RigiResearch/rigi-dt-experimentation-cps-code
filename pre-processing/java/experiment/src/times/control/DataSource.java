package times.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import times.model.Datagram;
import times.model.SITMLineStop;
import times.model.SITMStop;

public class DataSource {
	
	public final static String LINESTOPS_PATH = "data/linestops.csv";

	public static File getSourceFile(String path) {
		return new File(path);
	}

	public static ArrayList<Datagram> readDatagrams1(long lineId,String path) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		File sourceFile = DataSource.getSourceFile(path);
		ArrayList<Datagram> datagrams = new ArrayList<>();
		BufferedReader br;
		String text = "";

		try {

			br = new BufferedReader(new FileReader(sourceFile));
			text = br.readLine();
			text = br.readLine();

			while (text != null && !text.equals("")) {

				String[] data = text.split(",");

				if (data[7].equals(lineId + "")) {

					String datagramData = data[0];
					long datagramDateTime = dateFormat.parse(datagramData).getTime()/1000;
					Date datagramDate = new Date(dateFormat.parse(data[0]).getTime());
					long busId = Long.parseLong(data[1]);
					long stopId = Long.parseLong(data[2]);
					long odometer = Long.parseLong(data[3]);
					double longitude = Long.parseLong(data[4]);
					double latitude = Long.parseLong(data[5]);
					long taskId = Long.parseLong(data[6]);
					long tripId = Long.parseLong(data[8]);

					if (longitude != -1 && latitude != -1) {
						Datagram datagram = new Datagram(datagramDate,datagramDateTime, datagramData, busId, stopId, odometer,
								longitude / 10000000, latitude / 10000000, taskId, lineId, tripId);
						datagrams.add(datagram);
					}
				}

				text = br.readLine();
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datagrams;
	}

	public static ArrayList<Datagram> readDatagrams2(long lineId, String path) {

		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH.mm.ss",Locale.ENGLISH);
		File sourceFile = DataSource.getSourceFile(path);
		ArrayList<Datagram> datagrams = new ArrayList<>();
		BufferedReader br;
		String text = "";

		try {

			br = new BufferedReader(new FileReader(sourceFile));
			text = br.readLine();
			text = br.readLine();

			while (text != null && !text.equals("")) {

				String[] data = text.split(",");

				if (data.length > 1 && data[7].equals(lineId + "")) {

					String datagramData = changeFormat(data[10]);
					Date datagramDate = new Date(dateFormat.parse(data[10]).getTime());
					long datagramDateTime = dateFormat.parse(datagramData).getTime()/1000;
					long busId = Long.parseLong(data[11]);
					long stopId = Long.parseLong(data[2]);
					long odometer = Long.parseLong(data[3]);
					double longitude = Long.parseLong(data[5]);
					double latitude = Long.parseLong(data[4]);
					long taskId = Long.parseLong(data[6]);
					long tripId = Long.parseLong(data[8]);

					if (longitude != -1 && latitude != -1) {
						Datagram datagram = new Datagram(datagramDate,datagramDateTime, datagramData, busId, stopId, odometer, longitude / 10000000, latitude / 10000000, taskId, lineId, tripId);
						datagrams.add(datagram);
					}
				}

				text = br.readLine();
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datagrams;
	}

	public static ArrayList<Datagram> readDatagrams3(long lineId, String path, boolean printDate) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
		File sourceFile = DataSource.getSourceFile(path);
		ArrayList<Datagram> datagrams = new ArrayList<>();

		BufferedReader br;
		String text = "";

		try {

			br = new BufferedReader(new FileReader(sourceFile));
			text = br.readLine();
			text = br.readLine();

			while (text != null && !text.equals("")) {

				String[] data = text.split(",");

				if (data.length > 1 && data[7].equals(lineId + "")) {

					String datagramDateString = data[0];
					if(printDate) {
						System.out.println(datagramDateString);
					}
					long datagramDateLong = dateFormat.parse(data[0]).getTime()/1000;
					Date datagramDate = new Date(datagramDateLong);
					long busId = Long.parseLong(data[1]);	
					long stopId = Long.parseLong(data[2]);
					long odometer = Long.parseLong(data[3]);
					double longitude = Long.parseLong(data[4]);
					double latitude = Long.parseLong(data[5]);
					long taskId = Long.parseLong(data[6]);
					long tripId = Long.parseLong(data[8]);

					if (longitude != -1 && latitude != -1) {
						Datagram datagram = new Datagram(datagramDate,datagramDateLong, datagramDateString, busId, stopId, odometer, longitude / 10000000, latitude / 10000000, taskId, lineId, tripId);
						datagrams.add(datagram);
					}
				}

				text = br.readLine();
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datagrams;
	}
	
	public static String changeFormat(String date) {
		String day = date.substring(0,9);
		String hour = date.substring(10, 12);
		String minSec = date.substring(12, 18);
		String meridians = date.substring(26, 28);
		
		if(meridians.equals("PM") && !hour.equals("12")) {
			int hourNumber = Integer.parseInt(hour);
			hourNumber += 12;
			hour = hourNumber+"";
		}
		return day+" "+hour+minSec;
	}
	
	public static ArrayList<SITMLineStop> findAllLineStopByPlanVersion(long planVersionId) {

		String path = new File("data/linestops.csv").getAbsolutePath();
		ArrayList<SITMLineStop> lineStops = new ArrayList<>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String[] columns = null;
			String line = br.readLine();
			line = br.readLine();

			while (line != null) {
				columns = line.split(";");

				if (!columns[0].isEmpty() && columns[5].equals(planVersionId + "")) {

					long lineStopid = Long.parseLong(columns[0]);
					long stopsequence = Long.parseLong(columns[1]);
					long orientation = Long.parseLong(columns[2]);
					long lineid = Long.parseLong(columns[3]);
					long stopid = Long.parseLong(columns[4]);
					long planVersionid = Long.parseLong(columns[5]);
					long lineVariant = Long.parseLong(columns[6]);
					DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.US);
					Date registerDate = new Date(dateFormat.parse(columns[7]).getTime());
					long lineVariantType = Long.parseLong(columns[8]);

					lineStops.add(new SITMLineStop(lineStopid, stopsequence, orientation, lineid, stopid, planVersionid,
							lineVariant, registerDate, lineVariantType));
				}

				line = br.readLine();
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lineStops;
	}

	public static ArrayList<SITMStop> findAllStopsByPlanVersion(long planVersionId) {

		ArrayList<SITMStop> stops = new ArrayList<>();
		String path = new File("data/stops.csv").getAbsolutePath();

		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(path));
			String[] columns = null;
			String line = br.readLine();
			line = br.readLine();

			while (line != null) {
				columns = line.split(";");

				if (!columns[0].isEmpty() && columns[1].equals(planVersionId + "") && !columns[6].contains("#")
						&& !columns[6].equals("0") && !columns[7].contains("#") && !columns[7].equals("0")) {

					String longName = columns[3];
					String shortName = columns[2];
					long stopId = Long.parseLong(columns[0]);

					double gPSX = 0;
					double gPSY = 0;
					double decimalLongitude = 0;
					double decimalLactitude = 0;

					if (!columns[4].isEmpty()) {
						gPSX = Double.parseDouble(columns[4]) / 10000000;
					}
					if (!columns[5].isEmpty()) {
						gPSY = Double.parseDouble(columns[5]) / 10000000;
					}
					if (!columns[6].isEmpty()) {
						String origi = columns[6].replace(".", "");
						StringBuffer str = new StringBuffer(origi);
						str.insert(3, ".");
						decimalLongitude = Double.parseDouble(str.toString());
					}
					if (!columns[7].isEmpty()) {
						String origi = columns[7].replace(".", "");
						StringBuffer str = new StringBuffer(origi);
						str.insert(1, ".");
						decimalLactitude = Double.parseDouble(str.toString());
					}

					stops.add(new SITMStop(stopId, shortName, longName, gPSX, gPSY, decimalLongitude, decimalLactitude,
							planVersionId));
				}

				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return stops;
	}

	public static ArrayList<SITMStop> findAllStopsByLine(long planVersionId, long lineId) {

		ArrayList<SITMStop> stopsByLine = new ArrayList<>();
		ArrayList<SITMStop> stops = findAllStopsByPlanVersion(planVersionId);
		ArrayList<SITMLineStop> lineStops = findAllLineStopByPlanVersion(planVersionId);

		for (int i = 0; i < lineStops.size(); i++) {
			SITMLineStop lineStop = (SITMLineStop) lineStops.get(i);

			if (lineStop.getLineId() == lineId) {

				for (int j = 0; j < stops.size(); j++) {
					SITMStop stop = (SITMStop) stops.get(j);
					if (stop.getStopId() == lineStop.getStopId()) {
						stopsByLine.add(stop);
					}
				}
			}
		}

		return stopsByLine;
	}
}
