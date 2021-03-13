package times.control;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import times.model.Datagram;
import times.model.SITMStop;

class TimeDatagrams {
	private int numDatagrams;
	private int hour;
	private int minutes;

	public TimeDatagrams(int numDatagrams, int hour, int minutes) {
		super();
		this.numDatagrams = numDatagrams;
		this.hour = hour;
		this.minutes = minutes;
	}

	public int getNumDatagrams() {
		return numDatagrams;
	}

	public void setNumDatagrams(int numDatagrams) {
		this.numDatagrams = numDatagrams;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public String getDate() {
		return hour + ":" + minutes;
	}
}

public class MainDatagrams {

	public static HashMap<Long, SITMStop> stops; // HashMap with the stops
	public static HashMap<Long, ArrayList<TimeDatagrams>> stopsDatagrams; // HashMap with list of datagrams
	public static HashMap<Long, Integer> stopsTime; // HashMap with time

	public static int totalDatagrams;

	public static void main(String[] args) throws ParseException {
		init(131);
		readDatagrams(131, 500300, false);
		results();
	}

	/*
	 * This method initialize the hash maps with the necessaries stop ids and arrays
	 */
	public static void init(long lineId) {

		ArrayList<SITMStop> stopsQuery = DataSource.findAllStopsByLine(261, lineId);
		stops = new HashMap<>();
		stopsDatagrams = new HashMap<>();
		stopsTime = new HashMap<>();

		for (int i = 0; i < stopsQuery.size(); i++) {
			if (!stops.containsKey(stopsQuery.get(i).getStopId())) {
				stops.put(stopsQuery.get(i).getStopId(), stopsQuery.get(i));
				stopsTime.put(stopsQuery.get(i).getStopId(), 0);

				ArrayList<TimeDatagrams> td = new ArrayList<TimeDatagrams>();
				for (int j = 4; j < 24; j++) {
					td.add(new TimeDatagrams(0, j, 0));
					td.add(new TimeDatagrams(0, j, 10));
					td.add(new TimeDatagrams(0, j, 20));
					td.add(new TimeDatagrams(0, j, 30));
					td.add(new TimeDatagrams(0, j, 40));
					td.add(new TimeDatagrams(0, j, 50));
				}

				stopsDatagrams.put(stopsQuery.get(i).getStopId(), td);
			}
		}
	}

	/*
	 * This method read the datagrams file
	 */
	public static void readDatagrams(long lineId, long observerStop, boolean printDate) throws ParseException {

		String path = "data/output-30-APR-19-awk-sorted.csv";
		ArrayList<Datagram> datagrams = DataSource.readDatagrams3(lineId, path, printDate);

		for (int n = 0; n < datagrams.size(); n++) {

			Datagram datagram = datagrams.get(n);
			long stopId = datagram.getStopId();

			if (stops.containsKey(stopId)) {
				analysis(datagram);
			}
		}
	}

	/*
	 * This method analyze one datagram
	 */
	public static void analysis(Datagram datagram) throws ParseException {

		long stopId = datagram.getStopId();

		totalDatagrams++;
		datagram.getDatagramDate();

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(datagram.getDatagramDate());

		int currentMinute = calendar.get(Calendar.MINUTE);
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

		ArrayList<TimeDatagrams> datagramsList = stopsDatagrams.get(stopId);
		
		int position = (currentHour*6)-24;
		
		if(position>=0) {
			
			if (currentMinute >= 0 && currentMinute < 10) {
				//System.out.println("[00-10) - "+currentHour+":"+currentMinute+" "+datagramsList.get(position).getDate());
				datagramsList.get(position).setNumDatagrams(datagramsList.get(position).getNumDatagrams()+1);
				
			}
			
			if (currentMinute >= 10 && currentMinute < 20) {
				//System.out.println("[10-20) - "+currentHour+":"+currentMinute+" "+datagramsList.get(position+1).getDate());
				datagramsList.get(position+1).setNumDatagrams(datagramsList.get(position+1).getNumDatagrams()+1);
			}
			
			if (currentMinute >= 20 && currentMinute < 30) {
				//System.out.println("[20-30) - "+currentHour+":"+currentMinute+" "+datagramsList.get(position+2).getDate());
				datagramsList.get(position+2).setNumDatagrams(datagramsList.get(position+2).getNumDatagrams()+1);
			}
			
			if (currentMinute >= 30 && currentMinute < 40) {
				//System.out.println("[30-40) - "+currentHour+":"+currentMinute+" "+datagramsList.get(position+3).getDate());
				datagramsList.get(position+3).setNumDatagrams(datagramsList.get(position+3).getNumDatagrams()+1);	
			}
			
			if (currentMinute >= 40 && currentMinute < 50) {
				//System.out.println("[40-50) - "+currentHour+":"+currentMinute+" "+datagramsList.get(position+4).getDate());
				datagramsList.get(position+4).setNumDatagrams(datagramsList.get(position+4).getNumDatagrams()+1);
			}
			
			if (currentMinute >= 50 && currentMinute < 60) {
				//System.out.println("[50-60) - "+currentHour+":"+currentMinute+" "+datagramsList.get(position+5).getDate());
				datagramsList.get(position+5).setNumDatagrams(datagramsList.get(position+5).getNumDatagrams()+1);
			}
			
		}

	}

	/*
	 * This method print the results
	 */
	public static void results() {

		long[] studiedStops = { 504009, 500200, 500250, 500300, 500601 };
		System.out.println("total datagramas T31 " + totalDatagrams);
		System.out.println("Total detagramas del sistema: 2495586");
		System.out.println(" ");
		for (long id : studiedStops) {

			ArrayList<TimeDatagrams> datagramsList = stopsDatagrams.get(id);
			System.out.println("StopId: " + id);

			for (TimeDatagrams timeDatagrams : datagramsList) {

				double users = (double) timeDatagrams.getNumDatagrams() / 2495586 * 450000;
				int numUsers = (int) users;
//				System.out.println("Date: " + timeDatagrams.getDate() + " | Datagramas: "+ timeDatagrams.getNumDatagrams() + " | Usuarios: " + numUsers);
				System.out.println(timeDatagrams.getDate()+","+timeDatagrams.getNumDatagrams()+","+numUsers);
			}

			System.out.println();

		}

	}
}
