package std.wlj.mapper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.AttributeType;
import org.familysearch.standards.place.CitationType;
import org.familysearch.standards.place.ExtXrefType;
import org.familysearch.standards.place.FeedbackResolutionType;
import org.familysearch.standards.place.FeedbackStatusType;
import org.familysearch.standards.place.GenericType;
import org.familysearch.standards.place.PlaceNameType;
import org.familysearch.standards.place.PlaceType;
import org.familysearch.standards.place.RepRelationType;
import org.familysearch.standards.place.ResolutionType;
import org.familysearch.standards.place.data.GroupBridge;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.ws.model.LinkModel;
import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.TypeModel;


/**
 * Utility class to map between:
 * <br/>
 * {@link TypeDTO} and {@link TypeModel} types.<br/>
 * {@link PlaceType} and {@link TypeModel} types.<br/>
 * {@link PLaceNameType} and {@link TypeModel} types.<br/>
 * {@link AttributeType} and {@link TypeModel} types.<br/>
 * {@link CitationType} and {@link TypeModel} types.<br/>
 * {@link ResolutionType} and {@link TypeModel} types.<br/>
 * {@link ExtXrefType} and {@link TypeModel} types.<br/>
 * {@link RepRelationType} and {@link TypeModel} types.<br/>
 *  * 
 * @author dshaellman, wjohnson000
 *
 */
public class TypeMapper {

    protected static Map<TypeBridge.TYPE,String>            urlPaths = new EnumMap<>( TypeBridge.TYPE.class );

    static {
        urlPaths.put( TypeBridge.TYPE.ATTRIBUTE, WebConstants.URL_ATTR_TYPES_ROOT + WebConstants.PATH_SEPARATOR );
        urlPaths.put( TypeBridge.TYPE.CITATION, WebConstants.URL_CIT_TYPES_ROOT + WebConstants.PATH_SEPARATOR );
        urlPaths.put( TypeBridge.TYPE.EXT_XREF, WebConstants.URL_XREF_TYPES_ROOT + WebConstants.PATH_SEPARATOR );
        urlPaths.put( TypeBridge.TYPE.FEEDBACK_RESOLUTION, WebConstants.URL_FEEDBACK_RES_TYPES_ROOT + WebConstants.PATH_SEPARATOR );
        urlPaths.put( TypeBridge.TYPE.FEEDBACK_STATE, WebConstants.URL_FEEDBACK_STATE_TYPES_ROOT + WebConstants.PATH_SEPARATOR );
        urlPaths.put( TypeBridge.TYPE.NAME, WebConstants.URL_NAME_TYPES_ROOT + WebConstants.PATH_SEPARATOR );
        urlPaths.put( TypeBridge.TYPE.PLACE, WebConstants.URL_PLACE_TYPES_ROOT + WebConstants.PATH_SEPARATOR );
        urlPaths.put( TypeBridge.TYPE.REP_RELATION, WebConstants.URL_REP_REL_TYPES_ROOT + WebConstants.PATH_SEPARATOR );
        urlPaths.put( TypeBridge.TYPE.RESOLUTION, WebConstants.URL_RESOLUTION_TYPES_ROOT + WebConstants.PATH_SEPARATOR );
    }


    /**
     * Convert a {@link TypeBridge} instance into a {@link TypeModel} instance, suitable for
     * returning in a web response.  Note: the given input type MUST have a type of PLACE,
     * but that fact isn't verifiable from this method.
     * <p/>
     * NOTE: The returned type will have the name and description for ALL locales. 
     * 
     * @param type a {@link TypeBridge} instance
     * @param path base URL path
     * @return a {@link TypeModel} instance
     */
    public TypeModel createModelFromPlaceTypeBridge(TypeBridge type, Set<GroupBridge> groups, String path) {
        TypeModel model = this.createModelFromTypeBridge(type, null, path, TypeBridge.TYPE.PLACE);

        if ( groups != null && !groups.isEmpty() ) {
            List<LinkModel> groupLinks = new ArrayList<>();
            for ( GroupBridge group : groups ) {
                LinkModel link = new LinkModel();
                link.setTitle( group.getNames().get( "en" ) );
                link.setRel( "group" );
                link.setHref( path + WebConstants.URL_TYPE_GROUPS_ROOT + WebConstants.PATH_SEPARATOR + group.getGroupId() );

                groupLinks.add( link );
            }
            model.setGroups( groupLinks );
        }

        return model;
    }

    /**
     * This is the main conversion method to get a "TypeBridge" from a "GenericType".
     * <p/>
     * NOTE: The returned type will have a single name and description, the one that
     * matches or is closest to the given StdLocale.  If the StdLocale is null, then
     * return all names and descriptions.
     * 
     * @param typeBridge a TypeBridge
     * @param locale locale value
     * @param path path for the SELF link
     * @param typeType type type of this
     * @return a {@link TypeModel} instance
     */
    public TypeModel createModelFromTypeBridge(TypeBridge typeBridge, StdLocale locale, String path, TypeBridge.TYPE typeType) { //NOSONAR
        // Generate the correct TYPE to work on ...
        GenericType type = null;
        switch(typeType) {
            case ATTRIBUTE:
                type = new AttributeType(typeBridge);
                break;
            case CITATION:
                type = new CitationType(typeBridge);
                break;
            case EXT_XREF:
                type = new ExtXrefType(typeBridge);
                break;
            case NAME:
                type = new PlaceNameType(typeBridge);
                break;
            case PLACE:
                type = new PlaceType(typeBridge);
                break;
            case REP_RELATION:
                type = new RepRelationType(typeBridge);
                break;
            case RESOLUTION:
                type = new ResolutionType(typeBridge);
                break;
            case FEEDBACK_STATE:
                type = new FeedbackStatusType(typeBridge);
                break;
            case FEEDBACK_RESOLUTION:
                type = new FeedbackResolutionType(typeBridge);
                break;
            default:
                break;
        }

        if ( type == null ) {
            throw new RuntimeException( "Invalid/unknown type of type specified during type mapping: " + typeType ); //NOSONAR
        }

        return createModelFromType(type, locale, path, typeType);
    }

    /**
     * This is the main conversion method to get a "TypeModel" from a "GenericType".
     * <p/>
     * NOTE: The returned type will have a single name and description, the one that
     * matches or is closest to the given StdLocale.  If the StdLocale is null, then
     * return all names and descriptions.
     * 
     * @param type a subclass of the "GenericType"
     * @param locale locale value
     * @param path path for the SELF link
     * @param typeCat type category of this
     * @return a {@link TypeModel} instance
     */
    public TypeModel createModelFromType(GenericType type, StdLocale locale, String path, TypeBridge.TYPE typeType) {
        TypeModel                       model = new TypeModel();
        List<LocalizedNameDescModel>    nameList = new ArrayList<>();
        LocalizedNameDescModel          nameDesc = new LocalizedNameDescModel();
        LinkModel                       selfLink = new LinkModel();

        // Null locale --> return values for ALL locales
        if (locale == null) {
            Set<String> allLocales = type.getLocales();
            for (String aLocale : allLocales) {
                nameDesc = new LocalizedNameDescModel();
                nameDesc.setLocale(aLocale);
                nameDesc.setName(type.getName(StdLocale.makeLocale(aLocale)).get());
                nameDesc.setDescription(type.getDescription(StdLocale.makeLocale(aLocale)).get());
                nameList.add(nameDesc);
            }
        } else {
            nameDesc.setLocale(type.getName(locale).getLocale().toString());
            nameDesc.setName(type.getName(locale).get());
            nameDesc.setDescription(type.getDescription(locale).get());
            nameList.add(nameDesc);
        }

        if ( path != null ) {
            selfLink.setRel("self");
            selfLink.setHref(path + getRelativePath(typeType) + type.getId());
            model.setSelfLink(selfLink);
        }

        model.setId(type.getId());
        model.setCode(type.getCode());
        model.setName(nameList);
        model.setPublished(type.isPublished());

        return model;
    }

    /**
     * Retrieve the relative path for the type base on the type category
     * 
     * @param typeCat {@link TypeCategory} value
     * @return relative path
     */
    public static String getRelativePath( TypeBridge.TYPE typeCat ) {
        String  relPath = "";
        String  typePath;

        typePath = urlPaths.get( typeCat );
        if ( typePath != null ) {
            relPath += typePath;
        }

        return relPath;
    }
}
