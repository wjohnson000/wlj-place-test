package std.wlj.analysis.dao;

import java.util.List;

import org.familysearch.standards.analysis.dal.impl.ResultRepDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbResultRep;

import std.wlj.datasource.DbConnectionManager;

public class InterpRepDAORead {
    public static void main(String...args) {
        ResultRepDAOImpl interpRepDAO = new ResultRepDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        for (int i=0;  i<=5;  i++) {
            System.out.println("\nRead by InterpRepID=" + i + " ...");
            DbResultRep interpRep = interpRepDAO.read(i);
            output(interpRep);
        }

        for (int i=1;  i<=3;  i++) {
            System.out.println("\nRead by InterpResultID=" + i + " ...");
            List<DbResultRep> interpReps = interpRepDAO.readByResult(i);
            output(interpReps);
        }
    }

    static void output(List<DbResultRep> results) {
        results.stream().forEach(InterpRepDAORead::output);
    }

    static void output(DbResultRep result) {
        if (result != null) {
            System.out.println("DB-Interp-Rep: " + result);
            System.out.println("           id: " + result.getId());
            System.out.println("    result-id: " + result.getResultId());
            System.out.println("       rep-id: " + result.getRepId());
            System.out.println("   parse-path: " + result.getParsePath());
            System.out.println("    rel-score: " + result.getRelevanceScore());
        }
    }
}
