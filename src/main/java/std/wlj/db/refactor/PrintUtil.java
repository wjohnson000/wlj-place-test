package std.wlj.db.refactor;

import java.util.Arrays;

import org.familysearch.standards.place.data.*;


public class PrintUtil {

    public static void printIt(TypeBridge typeB) {
        System.out.println("TB: " + typeB);
        if (typeB != null) {
            System.out.println("  ID: " + typeB.getTypeId() + ";   CD: " + typeB.getCode() + ";   TP: " + typeB.getType());
            System.out.println("  NA: " + typeB.getNames());
            System.out.println("  DE: " + typeB.getDescriptions().keySet());
        }
    }

    public static void printIt(SourceBridge sourceB) {
        System.out.println("SB: " + sourceB);
        if (sourceB != null) {
            System.out.println("  ID: " + sourceB.getSourceId() + ";   TITLE: " + sourceB.getTitle());
        }
    }

    public static void printIt(ExternalReferenceBridge extXrefB) {
        System.out.println("EXTB: " + extXrefB);
        if (extXrefB != null) {
            System.out.println("  ID: " + extXrefB.getRefId() + ";   REF: " + extXrefB.getReference());
            printIt(extXrefB.getType());
            printIt(extXrefB.getPlaceRep());
        }
    }

    public static void printIt(PlaceRepBridge placeRepB) {
        System.out.println("PRB: " + placeRepB);
        if (placeRepB != null) {
            System.out.println("  ID: " + placeRepB.getRepId() + ";   PL: " + placeRepB.getDefaultLocale() + ";  REV: " + placeRepB.getRevision());
            System.out.println("  FT: " + placeRepB.getJurisdictionFromYear() + " to " + placeRepB.getJurisdictionToYear());
            System.out.println("  FT: " + placeRepB.getLatitude() + " :: " + placeRepB.getLongitude());
            System.out.println("  Jur-IDs: " + Arrays.toString(placeRepB.getJurisdictionIdentifiers()));
            System.out.println("  UUID: " + placeRepB.getUUID());
            System.out.println("  NM: " + placeRepB.getAllDisplayNames());
            System.out.println("  PlaceID: " + placeRepB.getPlaceId());
            printIt(placeRepB.getAssociatedPlace());
            printIt(placeRepB.getPlaceType());
            for (CitationBridge citnB : placeRepB.getAllCitations()) {
                printIt(citnB);
            }
            for (AttributeBridge attrB : placeRepB.getAllAttributes()) {
                printIt(attrB);
            }
        }
    }

    public static void printIt(PlaceBridge placeB) {
        System.out.println("PB: " + placeB);
        if (placeB != null) {
            System.out.println("  ID: " + placeB.getPlaceId());
            System.out.println("  FT: " + placeB.getFromYear() + " to " + placeB.getToYear());
            System.out.println("  NM: " + placeB.getAllNormalizedVariantNames());
        }
    }

    public static void printIt(GroupBridge groupB) {
        System.out.println("GB: " + groupB);
        if (groupB != null) {
            System.out.println("  ID: " + groupB.getGroupId() + ";   TYPE: " + groupB.getType());
            System.out.println("  NM: " + groupB.getNames());
            System.out.println("  MB: " + groupB.getMembers());
            System.out.println("  SG: " + groupB.getDirectSubGroups());
        }
    }

    public static void printIt(CitationBridge citationB) {
        System.out.println(citationB);
        if (citationB != null) {
            System.out.println("  ID: " + citationB.getCitationId() + ";   DESC: " + citationB.getDescription());
            System.out.println("  SR: " + citationB.getSourceRef() + ";   DATE: " + citationB.getDate());
            printIt(citationB.getType());
            printIt(citationB.getSource());
        }
    }

    public static void printIt(AttributeBridge attrB) {
        System.out.println(attrB);
        if (attrB != null) {
            System.out.println("  ID: " + attrB.getAttributeId() + ";   DESC: " + attrB.getRevision());
            System.out.println("  LC: " + attrB.getLocale() + ";   VALU: " + attrB.getValue() + ";   YR: " + attrB.getYear());
            printIt(attrB.getType());
        }
    }
}
