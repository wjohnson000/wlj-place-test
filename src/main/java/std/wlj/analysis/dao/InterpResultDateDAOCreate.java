package std.wlj.analysis.dao;

import java.util.Calendar;

import org.familysearch.standards.analysis.dal.impl.InterpretationDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbInterpretation;

import std.wlj.datasource.DbConnectionManager;

public class InterpResultDateDAOCreate {
    public static void main(String...args) {
        InterpretationDAOImpl interpResultDateDAO = new InterpretationDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        DbInterpretation newInterpResDate = new DbInterpretation();
        newInterpResDate.setRequestId(3);
        newInterpResDate.setResultId(2);
        newInterpResDate.setEventDate(Calendar.getInstance());

        DbInterpretation dbInterpResDate = interpResultDateDAO.create(newInterpResDate);
        System.out.println("DB-Interp-Res-Date: " + dbInterpResDate);
        System.out.println("         interp-ID: " + dbInterpResDate.getRequestId());
        System.out.println("         result-ID: " + dbInterpResDate.getResultId());
        System.out.println("              Date: " + dbInterpResDate.getEventDate().toInstant());
    }

}
