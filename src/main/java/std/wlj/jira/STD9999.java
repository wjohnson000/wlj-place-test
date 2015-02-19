package std.wlj.jira;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;


public class STD9999 {
	private static String inputPath = "C:/temp/place-rep-deleted.csv";
	private static String outputPath = "C:/temp/place-rep-citn.txt";


	private static class CitationX {
		long id;
		long repId;
		long srcId;
		long typeId;
		String date;
		String srcRef;
		String descr;
		String delFlg;
	}

	private static class AttributeX {
		
	}

	public static void main(String... args) throws Exception {
		Connection conn = getConn();
		List<String> output = new ArrayList<>();
		Map<Integer,List<Integer>> newToDelMap = getRepIds();

		int count = 0;
		for (Map.Entry<Integer,List<Integer>> entry : newToDelMap.entrySet()) {
			if (++count % 50 == 0) System.out.println("Processing " + count);

			int ndx = 0;
			Integer[] repIds = new Integer[entry.getValue().size()+1];
			repIds[ndx++] = entry.getKey();
			String hdrRow = "" + entry.getKey();

			for (Integer repId : entry.getValue()) {
				repIds[ndx++] = repId;
				hdrRow += "|" + repId;
			}

			output.add("");
			output.add(hdrRow);

			List<CitationX> citxList = getCitationData(conn, repIds);
			for (CitationX citx : citxList) {
				StringBuilder buff = new StringBuilder(64);
				buff.append(citx.id);
				buff.append("|").append(citx.repId);
				buff.append("|").append(citx.srcId);
				buff.append("|").append(citx.typeId);
				buff.append("|").append(citx.date);
				buff.append("|").append(citx.srcRef);
				buff.append("|").append(citx.descr);
				buff.append("|").append(citx.delFlg);
				output.add(buff.toString());
			}
		}

		Path prDetails = Paths.get(outputPath);
		Files.write(prDetails, output, Charset.forName("UTF-8"));

		conn.close();
		System.exit(0);
	}

	/**
	 * Prompt for a username, password ... get the connection
	 * @return
	 * @throws Exception
	 */
	private static Connection getConn() throws Exception {
		String username = JOptionPane.showInputDialog("Username:");
		String password = JOptionPane.showInputDialog("Password:");

		Class.forName("org.postgresql.Driver");
		return DriverManager.getConnection("jdbc:postgresql://localhost:22/sams_place", username, password);
	}

	/**
	 * Read the file containing the deleted place-rep identifiers
	 * 
	 * @return Map of new rep-id --> list of old rep ids
	 * @throws Exception
	 */
	private static Map<Integer,List<Integer>> getRepIds() throws Exception {
		Map<Integer,List<Integer>> results = new TreeMap<>();

		Path prDeleted = Paths.get(inputPath);
		for (String row : Files.readAllLines(prDeleted, Charset.forName("UTF-8"))) {
			// Read past the first row
			if (row.startsWith("RepID")) {
				continue;
			}

			String[] chunks = row.split(",");
			if (chunks.length > 3) {
				Integer repId = Integer.parseInt(chunks[0]);
				Integer newId = Integer.parseInt(chunks[3]);
				List<Integer> repIds = results.get(newId);
				if (repIds == null) {
					repIds = new ArrayList<Integer>();
					results.put(newId, repIds);
				}
				repIds.add(repId);
			}
		}

		return results;
	}

	@SuppressWarnings("deprecation")
	private static List<CitationX> getCitationData(Connection conn, Integer... repIds) throws Exception {
		List<CitationX> results = new ArrayList<>();

		boolean first = true;
		String query = "SELECT * FROM citation WHERE rep_id IN (";
		for (Integer repId : repIds) {
			if (first) {
				first = false;
			} else {
				query += ", ";
			}
			query += repId;
		}
		query += ")";

		try(Statement stmt=conn.createStatement();
			ResultSet rset=stmt.executeQuery(query)) {
			while (rset.next()) {
				Date citDate = rset.getDate("citation_date");
				String sDate = "";
				if (citDate != null) {
					int yr = citDate.getYear() + 1900;
					int mo = citDate.getMonth() + 1;
					int dy = citDate.getDate();
					sDate = yr + "-" + (mo<10 ? ("0" + mo) : mo) + "-" + (dy<10 ? ("0" + dy) : dy);
				}

				CitationX cx = new CitationX();
				cx.id     = rset.getInt("citation_id");
				cx.repId  = rset.getInt("rep_id");
				cx.srcId  = rset.getInt("source_id");
				cx.typeId = rset.getInt("type_id");
				cx.date   = sDate;
				cx.srcRef = rset.getString("source_ref");
				cx.descr  = rset.getString("description");
				cx.delFlg = String.valueOf(rset.getBoolean("delete_flag"));
				results.add(cx);
			}
		}

		return results;
	}
}
