package times.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DataSort {

	public final static String ORIGINAL = "data/30-APR-19.csv";
	public final static String SORTED = "data/30-APR-19-sorted.csv";
	
	public static void main(String[] args) {
		sortDatagrams();
	}

	public static void sortDatagrams() {

		File sourceFile = DataSource.getSourceFile(ORIGINAL);
		File sortFile = DataSource.getSourceFile(SORTED);
		ArrayList<String> datagrams = new ArrayList<>();
		BufferedReader br;
		BufferedWriter bw;
		String text = "";

		try {

			br = new BufferedReader(new FileReader(sourceFile));
			bw = new BufferedWriter(new FileWriter(sortFile));

			text = br.readLine();

			System.out.println("read datgrams");
			while (text != null && !text.equals("")) {
				
				String[] data = text.split(",");
				if(data.length>1) {
					datagrams.add(text);
				}
				text = br.readLine();
			}

			System.out.println("strat sort");
			sortArray(datagrams);
			
			System.out.println("start write");
			for (int i = 0; i < datagrams.size(); i++) {
				bw.write(datagrams.get(i) + "\n");
			}

			System.out.println("End sort");
			br.close();
			bw.close();

		} catch (Exception e) {
			System.out.println(text);
			e.printStackTrace();
		}

	}

	public static void sortArray(ArrayList<String> datagrams) {

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH.mm.ss");

		Collections.sort(datagrams, new Comparator<String>() {
			public int compare(String o1, String o2) {
				String data1 = o1.split(",")[10];
				String data2 = o2.split(",")[10];

				try {
					data1 = changeFormat(data1);
					Long date_1 = dateFormat.parse(data1).getTime() / 1000;
					
					data2 = changeFormat(data2);
					Long date_2 = dateFormat.parse(data2).getTime() / 1000;

					return date_1.compareTo(date_2);

				} catch (Exception e) {
					e.printStackTrace();
				}

				return 0;
			}
		});
	}
	
	public static String changeFormat(String date) {
		String day = date.substring(0, 3) + "04" + date.substring(6, 9);
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
}
