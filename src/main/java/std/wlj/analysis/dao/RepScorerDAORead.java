package std.wlj.analysis.dao;

import java.util.List;

import org.familysearch.standards.analysis.dal.impl.RepScorerDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbRepScorer;

import std.wlj.datasource.DbConnectionManager;

public class RepScorerDAORead {
    public static void main(String...args) {
        RepScorerDAOImpl repScorerDAO = new RepScorerDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        for (int i=0;  i<=5;  i++) {
            System.out.println("\nRead by ID=" + i + " ...");
            DbRepScorer interpRep = repScorerDAO.read(i);
            output(interpRep);
        }

        for (int i=1;  i<=3;  i++) {
            System.out.println("\nRead by InterpRepID=" + i + " ...");
            List<DbRepScorer> interpReps = repScorerDAO.readByResultRep(i);
            output(interpReps);
        }
    }

    static void output(List<DbRepScorer> results) {
        results.stream().forEach(RepScorerDAORead::output);
    }

    static void output(DbRepScorer result) {
        if (result != null) {
            System.out.println("DB-Rep-Scorer: " + result);
            System.out.println("           id: " + result.getId());
            System.out.println("   int-rep-id: " + result.getResultRepId());
            System.out.println("  scorer-name: " + result.getScorerName());
            System.out.println("        score: " + result.getScore());
            System.out.println("  basis-score: " + result.getBasisScore());
        }
    }
}
