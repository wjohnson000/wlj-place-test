package std.wlj.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.data.*;

public final class CompareBridges {
    private CompareBridges() { }

    public static void compare(AttributeBridge attrB01, AttributeBridge attrB02) {
        System.out.println(">>> Comparing two Attributes, Attr01: " + attrB01.getAttributeId() + " vs. " + attrB02.getAttributeId());
        compare("    *ID:", attrB01.getAttributeId(), attrB02.getAttributeId());
        compare("    *Locale:", attrB01.getLocale(), attrB02.getLocale());
        compare("    *Value:", attrB01.getValue(), attrB02.getValue());
        compare("    *Year:", attrB01.getFromYear(), attrB02.getFromYear());
        compare("    *Revn:", attrB01.getRevision(), attrB02.getRevision());
        compare("    *Rep.Id:", attrB01.getPlaceRep().getRepId(), attrB02.getPlaceRep().getRepId());
        compare("    *Type.Id:", attrB01.getType().getTypeId(), attrB02.getType().getTypeId());
    }

    public static void compare(CitationBridge citnB01, CitationBridge citnB02) {
        System.out.println(">>> Comparing two Citations, Citn01: " + citnB01.getCitationId() + " vs. " + citnB02.getCitationId());
        compare("    *ID:", citnB01.getCitationId(), citnB02.getCitationId());
        compare("    *Descr:", citnB01.getDescription(), citnB02.getDescription());
        compare("    *SrcRef:", citnB01.getSourceRef(), citnB02.getSourceRef());
        compare("    *Date:", citnB01.getDate(), citnB02.getDate());
        compare("    *Revn:", citnB01.getRevision(), citnB02.getRevision());
        compare("    *Rep.Id:", citnB01.getPlaceRep().getRepId(), citnB02.getPlaceRep().getRepId());
        compare("    *Source.Id:", citnB01.getSource().getSourceId(), citnB02.getSource().getSourceId());
        compare("    *Type.Id:", citnB01.getType().getTypeId(), citnB02.getType().getTypeId());
    }

    public static void compare(ExternalReferenceBridge extXrefB01, ExternalReferenceBridge extXrefB02) {
        if (extXrefB01 == null  ||  extXrefB02 == null) {
            System.out.println(">>> Comparing two ExtXrefs, ExtXref01: " + extXrefB01 + " vs. " + extXrefB02);
            System.out.println("    *One of 'em is null ...");
            return;
        }

        System.out.println(">>> Comparing two ExtXrefs, ExtXref01: " + extXrefB01.getRefId() + " vs. " + extXrefB02.getRefId());
        compare("    *ID:", extXrefB01.getRefId(), extXrefB02.getRefId());
        compare("    *Ref:", extXrefB01.getReference(), extXrefB02.getReference());
        compare("    *Rep.Id:", extXrefB01.getPlaceRep().getRepId(), extXrefB02.getPlaceRep().getRepId());
        compare("    *Type.Id:", extXrefB01.getType().getTypeId(), extXrefB02.getType().getTypeId());
    }

    public static void compare(GroupBridge groupB01, GroupBridge groupB02) {
        System.out.println(">>> Comparing two Groups, Group01: " + groupB01.getGroupId() + " vs. " + groupB02.getGroupId());
        compare("    *ID:", groupB01.getGroupId(), groupB02.getGroupId());
        compare("    *Members:", groupB01.getDirectMembers(), groupB02.getDirectMembers());
//        compare("    *SubGroups:", groupB01.getDirectSubGroups(), groupB02.getDirectSubGroups());
        compare("    *Names:", groupB01.getNames(), groupB02.getNames());
        compare("    *Descr:", groupB01.getDescriptions(), groupB02.getDescriptions());
    }

    public static void compare(PlaceBridge placeB01, PlaceBridge placeB02) {
        System.out.println(">>> Comparing two Places, Place01: " + placeB01.getPlaceId() + " vs. " + placeB02.getPlaceId());
        compare("    *ID:", placeB01.getPlaceId(), placeB02.getPlaceId());
        compare("    *From:", placeB01.getFromYear(), placeB02.getFromYear());
        compare("    *To:", placeB01.getToYear(), placeB02.getToYear());
        compare("    *Revn:", placeB01.getPlaceRevision(), placeB02.getPlaceRevision());
        compare("    *Varnt:", placeB01.getAllVariantNames(), placeB02.getAllVariantNames());
    }

    public static void compare(PlaceRepBridge repB01, PlaceRepBridge repB02) {
        System.out.println(">>> Comparing two Place-Reps, Rep01: " + repB01.getPlaceId() + " vs. " + repB02.getPlaceId());
        compare("    *ID:", repB01.getRepId(), repB02.getRepId());
        compare("    *PlaceId:", repB01.getPlaceId(), repB02.getPlaceId());
        compare("    *Juris:", repB01.getJurisdictionIdentifiers(), repB02.getJurisdictionIdentifiers());
        compare("    *From:", repB01.getJurisdictionFromYear(), repB02.getJurisdictionFromYear());
        compare("    *To:", repB01.getJurisdictionToYear(), repB02.getJurisdictionToYear());
        compare("    *Revn:", repB01.getRevision(), repB02.getRevision());
        compare("    *Locale:", repB01.getDefaultLocale(), repB02.getDefaultLocale());
        compare("    *Lattd:", repB01.getLatitude(), repB02.getLatitude());
        compare("    *Long:", repB01.getLongitude(), repB02.getLongitude());
        compare("    *Type.ID:", repB01.getPlaceType().getTypeId(), repB02.getPlaceType().getTypeId());
        compare("    *IsPub:", repB01.isPublished(), repB02.isPublished());
        compare("    *IsVal:", repB01.isValidated(), repB02.isValidated());
        compare("    *UUID:", repB01.getUUID(), repB02.getUUID());
        compare("    *DispNames:", repB01.getAllDisplayNames(), repB02.getAllDisplayNames());
    }

    public static void compare(SourceBridge sourceB01, SourceBridge sourceB02) {
        System.out.println(">>> Comparing two Sources, Source01: " + sourceB01.getSourceId() + " vs. " + sourceB02.getSourceId());
        compare("    *ID:", sourceB01.getSourceId(), sourceB02.getSourceId());
        compare("    *Title:", sourceB01.getTitle(), sourceB02.getTitle());
        compare("    *Descr:", sourceB01.getDescription(), sourceB02.getDescription());
    }

    public static void compare(TypeBridge typeB01, TypeBridge typeB02) {
        System.out.println(">>> Comparing two Types, Type01: " + typeB01.getTypeId() + " vs. " + typeB02.getTypeId());
        compare("    *ID:", typeB01.getTypeId(), typeB02.getTypeId());
        compare("    *Members:", typeB01.getCode(), typeB02.getCode());
        compare("    *Names:", typeB01.getNames(), typeB02.getNames());
        compare("    *Descr:", typeB01.getDescriptions(), typeB02.getDescriptions());
    }

    private static void compare(String field, int val01, int val02) {
        if (val01 != val02) {
            System.out.println(field + " " + val01 + " != " + val02);
        }
    }

    private static void compare(String field, Integer val01, Integer val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null) {
            System.out.println(field + " " + val01 + " != " + val02);
        } else if (val01.intValue() != val02.intValue()) {
            System.out.println(field + " " + val01 + " != " + val02);
        }
    }

    private static void compare(String field, Double val01, Double val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null) {
            System.out.println(field + " " + val01 + " != " + val02);
        } else if (Math.abs(val01 - val02) > 0.0005) {
            System.out.println(field + " " + val01 + " != " + val02);
        }
    }

    private static void compare(String field, String val01, String val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null) {
            System.out.println(field + " " + val01 + " != " + val02);
        } else if (! val01.equals(val02)) {
            System.out.println(field + " " + val01 + " != " + val02);
        }
    }

    private static void compare(String field, StdLocale val01, StdLocale val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null) {
            System.out.println(field + " " + val01 + " != " + val02);
        } else if (! val01.equals(val02)) {
            System.out.println(field + " " + val01 + " != " + val02);
        }
    }

    private static void compare(String field, Date val01, Date val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null) {
            System.out.println(field + " " + val01 + " != " + val02);
        } else if (! val01.equals(val02)) {
            System.out.println(field + " " + val01 + " != " + val02);
        }
    }

    private static void compare(String field, boolean val01, boolean val02) {
        if (val01 != val02) {
            System.out.println(field + " " + val01 + " != " + val02);
        }
    }

    private static void compare(String field, int[] val01, int[] val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null) {
            System.out.println(field + " " + val01 + " != " + val02);
        } else if (val01.length != val02.length) {
            System.out.println(field + " " + val01 + " != " + val02);
        } else {
            for (int i=0;  i<val01.length;  i++) {
                if (val01[i] != val02[i]) {
                    System.out.println(field + " " + i + "." + val01[i] + " != " + i + "." + val02[i]);
                }
            }
        }
    }

    private static void compare(String field, Set<Integer> val01, Set<Integer> val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null) {
            System.out.println(field + " " + val01 + " != " + val02);
        } else {
            Set<Integer> base = new TreeSet<Integer>(val01);
            base.removeAll(val02);
            for (Integer id : base) {
                System.out.println(field + " " + id + " != " + -1);
            }

            base = new TreeSet<Integer>(val02);
            base.removeAll(val01);
            for (Integer id : base) {
                System.out.println(field + " " + -1 + " != " + id);
            }
        }
    }

    private static void compare(String field, Map<String,String> val01, Map<String, String> val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null) {
            System.out.println(field + " " + val01 + " != " + val02);
        } else {
            Set<String> keys = new TreeSet<>();
            keys.addAll(val01.keySet());
            keys.addAll(val02.keySet());
            for (String key : keys) {
                compare(field, val01.get(key), val02.get(key));
            }
        }
    }

    private static void compare(String field, List<PlaceNameBridge> val01, List<PlaceNameBridge> val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null) {
            System.out.println(field + " " + val01 + " != " + val02);
        } else {
            Map<Integer,PlaceNameBridge> map01 = new HashMap<>();
            Map<Integer,PlaceNameBridge> map02 = new HashMap<>();
            for (PlaceNameBridge nameB : val01) {
                map01.put(nameB.getNameId(), nameB);
            }
            for (PlaceNameBridge nameB : val02) {
                map02.put(nameB.getNameId(), nameB);
            }

            Set<Integer> keys = new TreeSet<>();
            keys.addAll(map01.keySet());
            keys.addAll(map02.keySet());
            for (Integer id : keys) {
                PlaceNameBridge name01 = map01.get(id);
                PlaceNameBridge name02 = map02.get(id);
                if (name01 == null  ||  name02 == null) {
                    System.out.println(field + " " + name01 + " != " + name02);
                } else {
                    compare(field+"id:", name01.getNameId(), name02.getNameId());
                    compare(field+"type:", name01.getType().getTypeId(), name02.getType().getTypeId());
                    compare(field+"locale:", name01.getName().getLocale(), name02.getName().getLocale());
                    compare(field+"text:", name01.getName().get(), name02.getName().get());
                }
            }
        }
    }

}
