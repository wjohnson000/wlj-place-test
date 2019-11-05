/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.familysearch.standards.place.dao.TransactionDAO;
import org.familysearch.standards.place.dao.dbimpl.TransactionDAOImpl;
import org.familysearch.standards.place.data.RevisionInfo;

import std.wlj.util.DbConnectionManager;

/**
 * @author wjohnson000
 *
 */
public class TransactionDAOTest {

    public static void main(String...args) {
        TransactionDAO transxDAO = new TransactionDAOImpl(DbConnectionManager.getDataSourceAwsDev());
        String username = transxDAO.getUserOnRevision(1111111);
        Date   date     = transxDAO.getDateOnRevision(1111111);
        RevisionInfo info = transxDAO.getRevisionInfo(1111111);

        System.out.println("UU: " + username);
        System.out.println("DD: " + date);
        System.out.println("II: " + info.getRevision() + " . " + info.getCreateUser() + " . " + info.getCreateDate());

        List<RevisionInfo> infos = transxDAO.getRevisionInfo(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        for (RevisionInfo infox : infos) {
            System.out.println("JJ: " + infox.getRevision() + " . " + infox.getCreateUser() + " . " + infox.getCreateDate());
        }

        List<Integer> ids = new ArrayList<>();
        for (int i=1; i<252;  i++) {
            ids.add(i);
        }

        infos = transxDAO.getRevisionInfo(ids);
        System.out.println("KK.count: " + infos.size());
        for (RevisionInfo infox : infos) {
            System.out.println("KK: " + infox.getRevision() + " . " + infox.getCreateUser() + " . " + infox.getCreateDate());
        }
    }
}
