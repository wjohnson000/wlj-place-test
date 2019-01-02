package std.wlj.jira;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


public class STD9999X {
	private static String inputPath = "C:/temp/place-rep-citn.txt";
	private static String outputPath = "C:/temp/add-citn-bulk.txt";


	private static class CitationX {
		int id;
		int repId;
		int srcId;
		int typeId;
		String date;
		String srcRef;
		String descr;
		String delFlg;
	}


	public static void main(String... args) throws Exception {
		// Read in the raw data file
		Path inPath = Paths.get(inputPath);
		List<String> rawData = Files.readAllLines(inPath, StandardCharsets.UTF_8);

		// Create the two maps ...
		Map<Integer,List<Integer>> newToDelMap = new TreeMap<>();
		Map<Integer,List<CitationX>> repToCitMap = new HashMap<>();

		for (String row : rawData) {
			String[] chunks = row.split("\\|");
			if (chunks.length == 8) {
				CitationX citx = new CitationX();
				citx.id = Integer.parseInt(chunks[0]);
				citx.repId = Integer.parseInt(chunks[1]);
				citx.srcId = Integer.parseInt(chunks[2]);
				citx.typeId = Integer.parseInt(chunks[3]);
				citx.date = chunks[4];
				citx.srcRef = chunks[5];
				citx.descr = chunks[6];
				citx.delFlg = chunks[7];
				List<CitationX> citList = repToCitMap.get(citx.repId);
				if (citList == null) {
					citList = new ArrayList<CitationX>();
					repToCitMap.put(citx.repId, citList);
				}
				citList.add(citx);
			} else if (chunks.length > 1) {
				int repId = Integer.parseInt(chunks[0]);
				List<Integer> delIds = new ArrayList<>();
				newToDelMap.put(repId, delIds);
				for (int i=1;  i<chunks.length;  i++) {
					delIds.add(Integer.parseInt(chunks[i]));
				}
			}
		}

		// See which new REPs need to have citations moved forward
		List<String> cmdAll = new ArrayList<String>();
		for (Map.Entry<Integer,List<Integer>> entry : newToDelMap.entrySet()) {
			List<CitationX> newCitList = (repToCitMap.containsKey(entry.getKey())) ? repToCitMap.get(entry.getKey()) : new ArrayList<CitationX>();
			if (newCitList.size() > 3) {
				continue;
			}

			Set<String> hasCitn = getCmds(newCitList);

			Set<String> needCitn = new TreeSet<>();
			for (Integer delId : entry.getValue()) {
				List<CitationX> delCitList = (repToCitMap.containsKey(delId)) ? repToCitMap.get(delId) : new ArrayList<CitationX>();
				needCitn.addAll(getCmds(delCitList));
			}

			boolean first = true;
			for (String needThis : needCitn) {
				if (! hasCitn.contains(needThis)) {
					if (first) {
						first = false;
						cmdAll.add("search\trepId=" + entry.getKey());
					}
					cmdAll.add(needThis);
				}
			}
		}

		Path outPath = Paths.get(outputPath);
		Files.write(outPath, cmdAll, StandardCharsets.UTF_8);
	}

	/**
	 * Convert the citation details into a list of "add_citation" commands
	 * 
	 * @param citxList
	 * @return
	 */
	private static Set<String> getCmds(List<CitationX> citxList) {
		Set<String> results = new TreeSet<String>();

		for (CitationX citx : citxList) {
			StringBuilder buff = new StringBuilder();
			buff.append("add_citation");
			buff.append("\t").append(citx.srcId);
			buff.append("\t").append(citx.typeId);
			buff.append("\t").append(mungeIt(citx.date));
			buff.append("\t").append(mungeIt(citx.srcRef));
			String descr = mungeIt(citx.descr);
			if (descr.length() > 0) {
				buff.append("\t").append(descr);
			}
			results.add(buff.toString());
		}

		return results;
	}

	private static String mungeIt(String whatOrNull) {
		if (whatOrNull == null) {
			return "";
		} else if (whatOrNull.equals("null")) {
			return "";
		} else {
			return whatOrNull.trim();
		}
	}
}
