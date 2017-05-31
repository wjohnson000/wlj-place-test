package std.wlj.analysis.dao;

import java.util.List;

import org.familysearch.standards.analysis.dal.impl.InterpretationDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbInterpretation;

import std.wlj.datasource.DbConnectionManager;

public class InterpResultDateDAORead {
    public static void main(String...args) {
        InterpretationDAOImpl interpResultDateDAO = new InterpretationDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        for (int i=1;  i<=2;  i++) {
            System.out.println("\nRead by InterpID=" + i + " ...");
            List<DbInterpretation> irds = interpResultDateDAO.readByRequest(i);
            output(irds);
        }

        for (int i=1;  i<=3;  i++) {
            System.out.println("\nRead by ResultID=" + i + " ...");
            List<DbInterpretation> irds = interpResultDateDAO.readByResult(i);
            output(irds);
        }
    }

    static void output(List<DbInterpretation> results) {
        results.stream().forEach(InterpResultDateDAORead::output);
    }

    static void output(DbInterpretation result) {
        if (result != null) {
            System.out.println("DB-Interp-Res-Date: " + result);
            System.out.println("         interp-ID: " + result.getRequestId());
            System.out.println("         result-ID: " + result.getResultId());
            System.out.println("              Date: " + result.getEventDate().toInstant());
        }
    }

}
