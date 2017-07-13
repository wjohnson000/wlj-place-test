package std.wlj.analysis.dao.jdbc;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.familysearch.standards.analysis.dal.helper.ThreadLocalConnection;
import org.familysearch.standards.analysis.dal.jdbc.impl.RequestJdbcDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbRequest;

import std.wlj.datasource.DbConnectionManager;

public class RequestDAOAll {

    public static void main(String...args) throws SQLException {
        DataSource globalDS = DbConnectionManager.getDataSourceWLJ();
        ThreadLocalConnection.init(globalDS);

        RequestJdbcDAOImpl dao = new RequestJdbcDAOImpl();

        DbRequest request = dao.read(3900);
        displayRequest(request);

        System.out.println("\n\n01 ================================================================");
        request = dao.readMd5("b3dbe4aa95e3281e046c7ea3f6860f35");
        displayRequest(request);

        System.out.println("\n\n02 ================================================================");
        List<DbRequest> requests = dao.search("Missouri, United States".toLowerCase());
        requests.forEach(reqx -> displayRequest(reqx));

        System.out.println("\n\n03 ================================================================");
        Map<String,String> params = new HashMap<>();
        params.put("one", "1");
        params.put("two", "2");
        request = new DbRequest();
        request.setMd5Hash("this-is-an-md5-hash???");
        request.setPlaceName("Provo, UTAH, UTAH, UTAH, UTAH");
        request.setParameters(params);
        request.setCreateDate(Calendar.getInstance());
        request.setLastUsedDate(Calendar.getInstance());
        request.setTypeCode("type-code");
        displayRequest(request);
        DbRequest reqNew = dao.create(request);
        displayRequest(reqNew);

        ThreadLocalConnection.remove();
    }

    static void displayRequest(DbRequest request) {
        System.out.println("II: " + request.getId());
        System.out.println("  : " + request.getMd5Hash());
        System.out.println("  : " + request.getPlaceName());
        System.out.println("  : " + request.getCreateDate().getTime());
        System.out.println("  : " + request.getLastUsedDate().getTime());
        System.out.println("  : " + request.getTypeCode());
        System.out.println("  : " + request.getParameters());
    }
}
