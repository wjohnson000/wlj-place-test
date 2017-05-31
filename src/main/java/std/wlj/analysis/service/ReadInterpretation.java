package std.wlj.analysis.service;

import org.familysearch.standards.analysis.dal.dao.DAOFactory;
import org.familysearch.standards.analysis.dal.impl.DAOFactoryImpl;
import org.familysearch.standards.analysis.exception.AnalysisSystemException;
import org.familysearch.standards.analysis.model.*;
import org.familysearch.standards.analysis.service.AnalysisService;
import org.familysearch.standards.analysis.service.impl.AnalysisServiceImpl;
import org.familysearch.standards.analysis.util.POJOMarshalUtil;

import std.wlj.datasource.DbConnectionManager;

public class ReadInterpretation {

    public static void main(String...args) throws AnalysisSystemException {
        DAOFactory daoFactory = new DAOFactoryImpl(DbConnectionManager.getDataSourceWLJ());

        AnalysisService aService = new AnalysisServiceImpl(daoFactory);
        InterpretationModel intModel01 = aService.readInterpretation(1);

        System.out.println("1.F =\n" + intModel01);
        printAsJSON(intModel01);
        printAsXML(intModel01);
    }

    static void printAsJSON(InterpretationModel model) {
        System.out.println("\n\n\n2.F =\n" + POJOMarshalUtil.toJSON(model));
    }

    static void printAsXML(InterpretationModel model) {
        System.out.println("\n\n\n2.T.IM =\n" + POJOMarshalUtil.toXML(model));
    }
}
