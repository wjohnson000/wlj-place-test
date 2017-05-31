package std.wlj.analysis.dao;

import java.util.List;

import org.familysearch.standards.analysis.dal.impl.RepTokenDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbRepToken;

import std.wlj.datasource.DbConnectionManager;

public class RepTokenDAORead {
    public static void main(String...args) {
        RepTokenDAOImpl repTokenDAO = new RepTokenDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        for (int i=0;  i<=5;  i++) {
            System.out.println("\nRead by ID=" + i + " ...");
            DbRepToken interpRep = repTokenDAO.read(i);
            output(interpRep);
        }

        for (int i=1;  i<=3;  i++) {
            System.out.println("\nRead by InterpRepID=" + i + " ...");
            List<DbRepToken> interpReps = repTokenDAO.readByResultRep(i);
            output(interpReps);
        }
    }

    static void output(List<DbRepToken> results) {
        results.stream().forEach(RepTokenDAORead::output);
    }

    static void output(DbRepToken result) {
        if (result != null) {
            System.out.println("DB-Rep-Token: " + result);
            System.out.println("          id: " + result.getId());
            System.out.println("  int-rep-id: " + result.getResultRepId());
            System.out.println("       token: " + result.getRawToken());
            System.out.println("  norm-token: " + result.getNormalizedToken());
            System.out.println("  token-type: " + result.getTokenTypes());
            System.out.println("   token-ndx: " + result.getTokenIndex());
            System.out.println("    var-text: " + result.getVariantText());
            System.out.println("   type-code: " + result.getVariantTypeCode());
            System.out.println("      locale: " + result.getVariantLocale());
            System.out.println("   match-rep: " + result.getMatchedRepId());
        }
    }
}
