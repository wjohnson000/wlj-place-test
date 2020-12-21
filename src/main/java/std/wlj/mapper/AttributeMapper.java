package std.wlj.mapper;

import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.ReadableDataService;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.ws.model.AttributeModel;
import org.familysearch.standards.place.ws.model.LinkModel;


/**
 * Utility class to map between {@link AttributeBridge} and {@link AttributeModel} types.
 * 
 * @author wjohnson000
 */
public class AttributeMapper {

    /** Data service for doing additional operations for place-type-groups */
    private ReadableDataService dataService;
    private TypeMapper typeMapper;


    /**
     * Simple constructor
     * 
     * @param theService a "ReadablePlaceDataService"
     */
    public AttributeMapper(ReadableDataService theService) {
        this.dataService = theService;
        this.typeMapper  = new TypeMapper();
    }

    /**
     * Convert a {@link AttributeDTO} instance into a {@link AttributeModel} instance,
     * suitable for returning in a web response.
     * 
     * @param dto {@link AttributeDTO} instance
     * @param path base URL path
     * @return {@link AttributeModel} instance
     * 
     * @throws PlaceDataException 
     */
    public AttributeModel createModelFromBridge(AttributeBridge attr, StdLocale outLocale, String path) throws PlaceDataException {
        AttributeModel model = createModelFromBridge(attr, outLocale, path, attr.getPlaceRep().getRepId());

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(getSelfLink(attr, path));
        links.add(getRepLink(attr, path));
        links.add(getTypeLink(attr, path));

        model.setLinks(links);
        return model;
    }

    /**
     * Convert a {@link AttributeBridge} instance into a {@link AttributeModel} instance,
     * suitable for returning in a web response.  This version takes a "place-rep" identifier,
     * bypassing pulling it from the "AttributeBridge".
     * 
     * @param dto {@link AttributeBridge} instance
     * @param path base URL path
     * @param repId place-rep identifier
     * @return {@link AttributeModel} instance
     * 
     * @throws PlaceDataException 
     */
    public AttributeModel createModelFromBridge(AttributeBridge attr, StdLocale outLocale, String path, int repId) throws PlaceDataException {
        AttributeModel model = new AttributeModel();
        TypeBridge typx = attr.getType();
        System.out.println("Typx: " + typx);
        TypeBridge type = dataService.getTypeById(TypeBridge.TYPE.ATTRIBUTE, attr.getType().getTypeId());

        // A blank locale translates to a null locale; a zero (0) year translate to a null year
        String locale = (attr.getLocale() != null  &&  attr.getLocale().trim().length() > 0) ? attr.getLocale() : null;
        Integer fromYear  = (attr.getFromYear() == null  ||  attr.getFromYear().intValue() == 0) ? null : attr.getFromYear();
        Integer toYear  = (attr.getToYear() == null  ||  attr.getToYear().intValue() == 0) ? null : attr.getToYear();

        model.setId(attr.getAttributeId());
        model.setRepId(repId);
        model.setType(typeMapper.createModelFromTypeBridge(type, outLocale, path, TypeBridge.TYPE.ATTRIBUTE));
        model.setFromYear(fromYear);
        model.setToYear(toYear);
        model.setValue(attr.getValue());
        model.setTitle(attr.getTitle());
        model.setLocale(locale);
        model.setUrl(attr.getUrl());
        model.setUrlTitle(attr.getUrlTitle());
        model.setCopyrightNotice(attr.getCopyrightNotice());
        model.setCopyrightUrl(attr.getCopyrightUrl());

        return model;
    }

    /**
     * Create a link to "self"
     * @param dto attribute DTO
     * @param path base URL path
     * @return LinkModel to "self"
     */
    private LinkModel getSelfLink(AttributeBridge attr, String path) {
        LinkModel link = new LinkModel();
        link.setRel("self");
        link.setHref(path + WebConstants.URL_REPS_ROOT + WebConstants.PATH_SEPARATOR + attr.getPlaceRep().getRepId() + WebConstants.PATH_SEPARATOR + WebConstants.URL_ATTRS_ROOT + WebConstants.PATH_SEPARATOR + attr.getAttributeId());

        return link;
    }

    /**
     * Create a link to the place-rep associated with this attribute 
     * @param dto attribute DTO
     * @param path base URL path
     * @return LinkModel to place-rep
     */
    private LinkModel getRepLink(AttributeBridge attr, String path) {
        LinkModel link = new LinkModel();
        link.setRel("via");
        link.setHref(path + WebConstants.URL_REPS_ROOT + WebConstants.PATH_SEPARATOR + attr.getPlaceRep().getRepId());

        return link;
    }

    /**
     * Create a link to the [attribute] type associated with this attribute 
     * @param dto attribute DTO
     * @param path base URL path
     * @return LinkModel to attribute type
     */
    private LinkModel getTypeLink(AttributeBridge attr, String path) {
        LinkModel link = new LinkModel();
        link.setRel("type");
        link.setHref(path + WebConstants.URL_ATTR_TYPES_ROOT + WebConstants.PATH_SEPARATOR + attr.getType().getTypeId());

        return link;
    }
}
