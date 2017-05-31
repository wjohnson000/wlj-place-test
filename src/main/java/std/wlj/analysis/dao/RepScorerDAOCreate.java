package std.wlj.analysis.dao;

import org.familysearch.standards.analysis.dal.impl.RepScorerDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbRepScorer;

import std.wlj.datasource.DbConnectionManager;

public class RepScorerDAOCreate {
    public static void main(String...args) {
        RepScorerDAOImpl repScorerDAO = new RepScorerDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        DbRepScorer newRepScorer = new DbRepScorer();
        newRepScorer.setResultRepId(3);
        newRepScorer.setScorerName("scorer-04");
        newRepScorer.setScore(25);
        newRepScorer.setBasisScore(21);

        DbRepScorer dbRepScorer = repScorerDAO.create(newRepScorer);
        System.out.println("DB-Rep-Scorer: " + dbRepScorer);
        System.out.println("           id: " + dbRepScorer.getId());
        System.out.println("   int-rep-id: " + dbRepScorer.getResultRepId());
        System.out.println("  scorer-name: " + dbRepScorer.getScorerName());
        System.out.println("        score: " + dbRepScorer.getScore());
        System.out.println("  basis-score: " + dbRepScorer.getBasisScore());
    }
}
