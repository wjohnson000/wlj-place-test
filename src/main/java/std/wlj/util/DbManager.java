package std.wlj.util;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DbManager {

    static Map<String,ApplicationContext> appContextMap = new HashMap<>();

    public static class DbServices {
        public DbReadableService readService;
        public DbWritableService writeService;
        DbServices(DbReadableService readService, DbWritableService writeService) {
            this.readService = readService;
            this.writeService = writeService;
        }
    }

    /**
     * Return a DbReadableService that's connected to a local Postgres database.
     * 
     * @return Local DB connection
     */
    public static DbServices getLocal() {
        ApplicationContext appContext = getAppContext("postgres-context-localhost-wlj.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        return (new DbServices(new DbReadableService(ds), new DbWritableService(ds)));
    }

    /**
     * Return a DbReadableService that's connected to the DEV-DB Postgres database.
     * 
     * @return DEV-DB connection
     */
    public static DbServices getDevDb() {
        ApplicationContext appContext = getAppContext("postgres-context-devdb.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        return (new DbServices(new DbReadableService(ds), new DbWritableService(ds)));
    }

    /**
     * Return a DbReadableService that's connected to the AWS-Team Postgres database.
     * 
     * @return AWS-DB connection
     */
    public static DbServices getAwsTeam() {
        ApplicationContext appContext = getAppContext("postgres-context-aws-team.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        return (new DbServices(new DbReadableService(ds), new DbWritableService(ds)));
    }

    /**
     * Return a DbReadableService that's connected to the AWS-Integration Postgres database.
     * 
     * @return AWS-DB connection
     */
    public static DbServices getAwsInt() {
        ApplicationContext appContext = getAppContext("postgres-context-aws-int.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        return (new DbServices(new DbReadableService(ds), new DbWritableService(ds)));
    }

    /**
     * Close all application contexts
     */
    public static void closeAppContext() {
        for (ApplicationContext appContext : appContextMap.values()) {
            ((ClassPathXmlApplicationContext)appContext).close();
        }
    }

    /**
     * Retrieve an application-context based on the name.
     * 
     * @param name file name
     * @return Associated 'ApplicationContext'
     */
    private static ApplicationContext getAppContext(String name) {
        ApplicationContext appContext = appContextMap.get(name);
        if (appContext == null) {
            appContext = new ClassPathXmlApplicationContext(name);
            appContextMap.put(name, appContext);
        }
        return appContext;
    }
}
