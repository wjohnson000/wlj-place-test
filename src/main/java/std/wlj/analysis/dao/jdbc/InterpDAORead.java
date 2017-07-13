package std.wlj.analysis.dao.jdbc;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.familysearch.standards.analysis.dal.helper.ThreadLocalConnection;
import org.familysearch.standards.analysis.dal.jdbc.impl.InterpretationJdbcDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbInterpretation;

import std.wlj.datasource.DbConnectionManager;

public class InterpDAORead {

    public static void main(String...args) throws SQLException {
        DataSource globalDS = DbConnectionManager.getDataSourceWLJ();
        ThreadLocalConnection.init(globalDS);

        InterpretationJdbcDAOImpl dao = new InterpretationJdbcDAOImpl();

        DbInterpretation interp = dao.read(4990);
        displayInterp(interp);

        System.out.println("\n\n================================================================");
        List<DbInterpretation> interps = dao.readByRequest(3860);
        interps.forEach(interpx -> displayInterp(interpx));

        System.out.println("\n\n================================================================");
        interps = dao.readByResult(3864);
        interps.forEach(interpx -> displayInterp(interpx));

        ThreadLocalConnection.remove();
    }

    static void displayInterp(DbInterpretation interp) {
        System.out.println("II: " + interp.getId());
        System.out.println("  : " + interp.getRequestId());
        System.out.println("  : " + interp.getResultId());
        System.out.println("  : " + interp.getAcceptLang());
        System.out.println("  : " + interp.getUserAgent());
        System.out.println("  : " + interp.getCollectionId());
        System.out.println("  : " + interp.getEventDate().getTime());
    }
}
