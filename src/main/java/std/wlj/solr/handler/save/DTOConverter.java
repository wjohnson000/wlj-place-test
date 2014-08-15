package std.wlj.solr.handler.save;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.familysearch.standards.place.data.AttributeDTO;
import org.familysearch.standards.place.data.CitationDTO;
import org.familysearch.standards.place.data.GroupDTO;
import org.familysearch.standards.place.data.PlaceDTO;
import org.familysearch.standards.place.data.PlaceRepresentationDTO;
import org.familysearch.standards.place.data.SourceDTO;
import org.familysearch.standards.place.data.TypeDTO;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;


/**
 * Factory class for creating various "DTO" types from a {@link SolrInputDocument}
 * instance, which contains a set of key-->value fields.  The keys are field names
 * as defined in the {@link PlaceRepDoc.SolrField} enumeration.
 * 
 * @author wjohnson000
 *
 */
public class DTOConverter {

    /**
     * Create a list of {@link AttributeDTO} instances from a {@link SolrInputDocument}
     * instance.
     * 
     * @param sDoc SolrInputDocument
     * @return list of attribute definitions
     */
    public List<AttributeDTO> makeAttributeFromSolr(SolrInputDocument sDoc) {
        List<AttributeDTO> results = new ArrayList<>();

        int repId = getInt(sDoc, "id");
        Collection<Object> attrs = sDoc.getFieldValues("attributes");
        if (attrs != null) {
            for (Object attr: attrs) {
                String[] tokens = String.valueOf(attr).split("\\|");
                int attrId = tokens[0].length() == 0 ? 0 : Integer.parseInt(tokens[0]);
                int typeId = tokens[1].length() == 0 ? 0 : Integer.parseInt(tokens[1]);
                int year   = tokens[2].length() == 0 ? 0 : Integer.parseInt(tokens[2]);
                String locale = (tokens.length > 4) ? tokens[4] : null;
                AttributeDTO attrDTO = new AttributeDTO(attrId, repId, typeId, year, tokens[3], locale, 0);
                results.add(attrDTO);
            }
        }

        return results;
    }

    /**
     * Create a list of {@link CitationDTO} instances from a {@link SolrInputDocument}
     * instance.
     * 
     * @param sDoc SolrInputDocument
     * @return list of citation definitions
     */
    public List<CitationDTO> makeCitationFromSolr(SolrInputDocument sDoc) {
        List<CitationDTO> results = new ArrayList<>();

        int repId = getInt(sDoc, "id");
        Collection<Object> citns = sDoc.getFieldValues("citations");
        if (citns != null) {
            for (Object citn: citns) {
                String[] tokens = String.valueOf(citn).split("\\|");
                int citnId = tokens[0].length() == 0 ? 0 : Integer.parseInt(tokens[0]);
                int srcId  = tokens[1].length() == 0 ? 0 : Integer.parseInt(tokens[1]);
                int typeId = tokens[2].length() == 0 ? 0 : Integer.parseInt(tokens[2]);
                Date citDate = tokens[3].length() == 0 ? null : PlaceRepDoc.formatToDate(tokens[3]);
                CitationDTO citnDTO = new CitationDTO(citnId, srcId, repId, typeId, citDate, tokens[4], tokens[5], 0);
                results.add(citnDTO);
            }
        }

        return results;
    }

    /**
     * Create a list of {@link GroupDTO} instances from a {@link SolrInputDocument}
     * instance.
     * 
     * @param sDoc SolrInputDocument
     * @return list of group definitions
     */
    public List<GroupDTO> makeGroupFromSolr(SolrInputDocument sDoc) {
        return null;
    }

    /**
     * Create a {@link PlaceDTO} instance from a {@link SolrInputDocument}
     * instance.
     * 
     * @param sDoc SolrInputDocument
     * @return a place definition
     */
    public PlaceDTO makePlaceFromSolr(SolrInputDocument sDoc) {
        return null;
    }

    /**
     * Create a {@link PlaceRepresentationDTO} instance from a {@link SolrInputDocument}
     * instance.
     * 
     * @param sDoc SolrInputDocument
     * @return a place-rep definition
     */
    public PlaceRepresentationDTO makePlaceRepFromSolr(SolrInputDocument sDoc) {
        return null;
    }

    /**
     * Create a list of {@link SourceDTO} instances from a {@link SolrInputDocument}
     * instance.
     * 
     * @param sDoc SolrInputDocument
     * @return list of source definitions
     */
    public List<SourceDTO> makeSourceFromSolr(SolrInputDocument sDoc) {
        List<SourceDTO> results = new ArrayList<>();

        Collection<Object> sources = sDoc.getFieldValues("appData");
        if (sources != null) {
            for (Object source: sources) {
                String[] tokens = String.valueOf(source).split("\\|");
                int srcId  = tokens[0].length() == 0 ? 0 : Integer.parseInt(tokens[0]);
                boolean isPub = tokens[0].length() == 0 ? false : Boolean.valueOf(tokens[3]);
                SourceDTO sourceDTO = new SourceDTO(srcId, tokens[1], tokens[2], isPub);
                results.add(sourceDTO);
            }
        }

        return results;
    }

    /**
     * Create a list of {@link TypeDTO} instances from a {@link SolrInputDocument}
     * instance.
     * 
     * @param sDoc SolrInputDocument
     * @return list of group definitions
     */
    public List<TypeDTO> makeTypeFromSolr(SolrInputDocument sDoc) {
        return null;
    }


    /**
     * Retrieve the "int" value of a field based on the field name.
     *
     * @param sDoc SolrInputDocument
     * @param fieldName field name
     * @return int value associated with the field
     */
    private int getInt(SolrInputDocument sDoc, String fieldName) {
        Object intField = sDoc.getFieldValue(fieldName);
        if (intField == null) {
            return 0;
        } else if (intField instanceof Number) {
            return ((Number)intField).intValue();
        } else {
            return 0;
        }
    }
}
