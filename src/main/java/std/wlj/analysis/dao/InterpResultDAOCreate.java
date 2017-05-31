package std.wlj.analysis.dao;

import java.util.Arrays;

import org.familysearch.standards.analysis.dal.impl.ResultDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbResult;

import std.wlj.datasource.DbConnectionManager;

public class InterpResultDAOCreate {
    public static void main(String...args) {
        ResultDAOImpl interpResultDAO = new ResultDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        DbResult newInterpRes = new DbResult();
        newInterpRes.setRequestId(2);
        newInterpRes.setMd5Hash("hash-for-res-" + System.nanoTime());
        newInterpRes.setCount(111);
        newInterpRes.setAnnotations(Arrays.asList("annotation-1", "annotation-A", "annotation-ようこそ"));

        DbResult dbInterpRes = interpResultDAO.create(newInterpRes);
        System.out.println("DB-Interp-Res: " + dbInterpRes);
        System.out.println("           id: " + dbInterpRes.getId());
        System.out.println("    interp-id: " + dbInterpRes.getRequestId());
        System.out.println("          md5: " + dbInterpRes.getMd5Hash());
        System.out.println("        count: " + dbInterpRes.getCount());
        System.out.println("         anns: " + dbInterpRes.getAnnotations());
    }
}
