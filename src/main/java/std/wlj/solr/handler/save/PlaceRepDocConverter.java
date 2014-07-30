package std.wlj.solr.handler.save;

import org.apache.solr.common.SolrInputDocument;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;


public class PlaceRepDocConverter {

    /**
     * Factory method to convert a {@link PlaceRepDoc} into a {@link SolrInputDocument}
     * so we can pass the latter directly into SOLR.
     * 
     * @param aDoc a PlaceRepDoc instance
     * @return corresponding SolrInputDocument instance
     */
    public static SolrInputDocument makeFromPlaceRepDoc(PlaceRepDoc aDoc) {
        SolrInputDocument sDoc = new SolrInputDocument();

        sDoc.setField("id", aDoc.getId());
        sDoc.setField("repId", aDoc.getRepId());
        sDoc.setField("ownerId", aDoc.getOwnerId());
        sDoc.setField("parentId", aDoc.getParentId());
        sDoc.setField("type", aDoc.getType());
        sDoc.setField("prefLocale", aDoc.getPreferredLocale());
        sDoc.setField("revision", aDoc.getRevision());
        if (aDoc.getCentroid() != null) {
            sDoc.setField("centroid", aDoc.getCentroid());
        }
        sDoc.setField("startYear", valueOrDefault(aDoc.getStartYear(), Integer.MIN_VALUE));
        sDoc.setField("endYear", valueOrDefault(aDoc.getEndYear(), Integer.MAX_VALUE));
        sDoc.setField("ownerStartYear", valueOrDefault(aDoc.getOwnerStartYear(), Integer.MIN_VALUE));
        sDoc.setField("ownerEndYear", valueOrDefault(aDoc.getOwnerEndYear(), Integer.MAX_VALUE));
        sDoc.setField("published", aDoc.getPublished());
        sDoc.setField("validated", aDoc.getValidated());
        sDoc.setField("uuid", aDoc.getUUID());
        if (aDoc.getTypeGroup() != null) {
            sDoc.setField("typeGroup", aDoc.getTypeGroup());
        }
        if (aDoc.getForwardRevision() != null) {
            sDoc.setField("forwardRevision", aDoc.getForwardRevision());
        }
        if (aDoc.getDeleteId() != null) {
            sDoc.setField("deleteId", aDoc.getDeleteId());
        }
        if (aDoc.getPlaceDeleteId() != null) {
            sDoc.setField("placeDeleteId", aDoc.getPlaceDeleteId());
        }

        for (String value : aDoc.getDisplayNames()) {
            sDoc.addField("displayNames", value);
        }
        for (String value : aDoc.getNames()) {
            sDoc.addField("names", value);
        }
        for (String value : aDoc.getVariantNames()) {
            sDoc.addField("variantNames", value);
        }
        for (int value : aDoc.getRepIdChainAsInt()) {
            sDoc.addField("repIdChain", value);
        }
        for (int value : aDoc.getRepIdChainAsInt()) {
            sDoc.addField("repIdChain", value);
        }

        if (aDoc.getAppData() != null  &&  aDoc.getAppData().size() > 0) {
            for (String value : aDoc.getAppData()) {
                sDoc.addField("appData", value);
            }
        }

        if (aDoc.getCitations() != null  &&  aDoc.getCitations().size() > 0) {
            for (String value : aDoc.getCitations()) {
                sDoc.addField("citations", value);
            }
            for (Integer value : aDoc.getCitSourceIds()) {
                sDoc.addField("citSourceId", value);
            }
        }

        if (aDoc.getAttributes() != null  &&  aDoc.getAttributes().size() > 0) {
            for (String value : aDoc.getAttributes()) {
                sDoc.addField("attributes", value);
            }
            for (Integer value : aDoc.getAttrTypeIds()) {
                sDoc.addField("attrTypeId", value);
            }
            for (Integer value : aDoc.getAttrYears()) {
                sDoc.addField("attrYear", value);
            }
            for (String value : aDoc.getAttrValues()) {
                sDoc.addField("attrValue", value);
            }
        }

        if (aDoc.getExtXrefs() != null  &&  aDoc.getExtXrefs().size() > 0) {
            for (String value : aDoc.getExtXrefs()) {
                sDoc.addField("xref", value);
            }
        }

        // TODO what about "_version_" ???
        sDoc.addField("_version_", System.nanoTime());

        // TODO what about "_root_" ???
//        sDoc.addField("_version_", System.nanoTime());

        return sDoc;
    }

    /**
     * Convenience method to return the value of a field or, if null, some given
     * default value instead.
     * 
     * @param value target value
     * @param defaultValue default value to use if the target is null
     * @return value or default value
     */
    private static Integer valueOrDefault(Integer value, Integer defaultValue) {
        return (value == null) ? defaultValue : value;
    }
}
