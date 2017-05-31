package std.wlj.analysis.dao;

import org.familysearch.standards.analysis.dal.impl.ResultRepDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbResultRep;

import std.wlj.datasource.DbConnectionManager;

public class InterpRepDAOCreate {
    public static void main(String...args) {
        ResultRepDAOImpl interpRepDAO = new ResultRepDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        DbResultRep newInterpRep = new DbResultRep();
        newInterpRep.setResultId(1);
        newInterpRep.setRepId(2345678);
        newInterpRep.setParsePath("[path-02][path-03][path-04]");
        newInterpRep.setRelevanceScore(81);

        DbResultRep dbInterpRep = interpRepDAO.create(newInterpRep);
        System.out.println("DB-Interp-Rep: " + dbInterpRep);
        System.out.println("           id: " + dbInterpRep.getId());
        System.out.println("    result-id: " + dbInterpRep.getResultId());
        System.out.println("       rep-id: " + dbInterpRep.getRepId());
        System.out.println("   parse-path: " + dbInterpRep.getParsePath());
        System.out.println("    rel-score: " + dbInterpRep.getRelevanceScore());
    }
}
