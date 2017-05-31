package std.wlj.general;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.familysearch.standards.place.data.WritableDataService.VariantNameDef;

public class SortVariantDefs {
    public static void main(String...args) {
        List<VariantNameDef> vars = Arrays.asList(
                makeVar(1, "howdy-one"),
                makeVar(3, "howdy-three"),
                makeVar(2, "howdy-two"),
                makeVar(0, "howdy-zero"),
                makeVar(6, "howdy-six"),
                makeVar(0, "howdy-zero"),
                makeVar(5, "howdy-five"));
        printList(vars);

        vars.sort(Comparator.comparingInt(name -> -1 * name.id));
        printList(vars);
    }

    static VariantNameDef makeVar(int id, String text) {
        VariantNameDef result = new VariantNameDef();
        result.id = id;
        result.text = text;
        return result;
    }

    static void printList(List<VariantNameDef> vars) {
        System.out.println("SORTED LIST:");
        vars.forEach(var -> System.out.println("  " + var.id + " -> " + var.text));
    }
}
