package std.wlj.solrload;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.loader.helper.PlaceNoRepDocReader;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;

public class RunPlaceNoRepReader {
	final static String selector = "old";
	final static String outputDir = "D:/tmp/solr-docs/no-rep-" + selector;
	final static File parentDir = new File("D:/tmp/flat-files/one-million-" + selector);

	public static void main(String... args) throws IOException {

		PlaceNoRepDocReader prReader = new PlaceNoRepDocReader(parentDir);

		int docCount = 0;
		Iterator<PlaceRepDoc> iter = prReader.iterator();

		long then = System.nanoTime();
        while (iter.hasNext()) {
            docCount++;
            PlaceRepDoc prDoc = iter.next();
            saveDoc(prDoc);

            if (prDoc.getVariantNames().size() == 0) {
                System.out.println("No Variants!!  " + prDoc.getId() + " . " + prDoc.getOwnerId() + " . " + prDoc.getPlaceDeleteId() + " . " + prDoc.getVariantNames().size());
            } else if (prDoc.getPlaceDeleteId() == null  ||  prDoc.getPlaceDeleteId().intValue() == 0) {
                System.out.println("No Delete-Id!!  " + prDoc.getId() + " . " + prDoc.getOwnerId() + " . " + prDoc.getPlaceDeleteId() + " . " + prDoc.getVariantNames().size());
            } else if (prDoc.getDeleteId() == null  ||  prDoc.getDeleteId().intValue() <= 0) {
                System.out.println("No Rep-Delete-Id!!  " + prDoc.getId() + " . " + prDoc.getOwnerId() + " . " + prDoc.getPlaceDeleteId() + " . " + prDoc.getVariantNames().size());
            }
        }

        long nnow = System.nanoTime();
        System.out.println("DocCount: " + docCount);
        System.out.println("Time: " + ((nnow - then) / 1000000.0));
	}

	private static void saveDoc(PlaceRepDoc doc) throws IOException {
		List<String> data = new ArrayList<>();

		data.add("id: " + doc.getId());
		data.add("parent-id: " + doc.getParentId());
		data.add("rep-id: " + doc.getRepId());
		data.add("type: " + doc.getType());
		data.add("pref-locale: " + doc.getPrefLocale());
		data.add("published: " + doc.getPublished());
		data.add("validated: " + doc.getValidated());
		data.add("revision: " + doc.getRevision());
		data.add("start-year: " + doc.getStartYear());
		data.add("end-year: " + doc.getEndYear());
		data.add("owner-id: " + doc.getOwnerId());
		data.add("owner-start-year: " + doc.getOwnerStartYear());
		data.add("owner-end-year: " + doc.getOwnerEndYear());
		data.add("centroid: " + doc.getCentroid());
		data.add("latitude: " + doc.getLatitude());
		data.add("longitude: " + doc.getLongitude());
//		data.add("uuid: " + doc.getUUID());
		data.add("delete-id: " + doc.getDeleteId());
		data.add("place-delete-id: " + doc.getPlaceDeleteId());

		addList(data, "names", doc.getNames());
		addList(data, "display-names", doc.getDisplayNames());
		addMap(data, "display-name-map", doc.getDisplayNameMap());
		addList(data, "variants", doc.getVariantNames());
		addList(data, "attributes", doc.getAttributes());
		addList(data, "citations", doc.getCitations());
		addList(data, "xref", doc.getExtXrefs());

		Files.write(Paths.get(outputDir, "place-" + doc.getOwnerId() + ".txt"), data, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
	}

	private static void addList(List<String> data, String title, List<String> values) {
		data.add(title+".count: " + (values==null ? 0 : values.size()));
		if (values != null) {
			List<String> temp = new ArrayList<>(values);
			Collections.sort(temp);
			for (String tt : temp) {
				data.add("    " + tt);
			}
		}
	}

	private static void addMap(List<String> data, String title, Map<String, String> values) {
		data.add(title+".count: " + (values==null ? 0 : values.size()));
		if (values != null) {
			Map<String,String> temp = new TreeMap<>(values);
			for (Map.Entry<String, String> entry : temp.entrySet()) {
				data.add("    " + entry.getKey() + " --> " + entry.getValue());
			}
		}
	}
}
