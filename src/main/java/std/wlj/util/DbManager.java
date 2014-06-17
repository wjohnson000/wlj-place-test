package std.wlj.util;

import javax.sql.DataSource;

import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DbManager {

    static ApplicationContext appContext = null;
    
    /**
     * Return a DbDataService that's connected to a local Postgres database.
     * 
     * @return Local DB connection
     */
    public static DbDataService getLocal() {
        appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        return new DbDataService(ds);
    }

    /**
     * Return a DbDataService that's connected to the DEV-DB Postgres database.
     * 
     * @return DEV-DB connection
     */
    public static DbDataService getDevDb() {
        appContext = new ClassPathXmlApplicationContext("postgres-context-devdb.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        return new DbDataService(ds);
    }

    /**
     * Return a DbDataService that's connected to the AWS-Team Postgres database.
     * 
     * @return AWS-DB connection
     */
    public static DbDataService getAwsTeam() {
        appContext = new ClassPathXmlApplicationContext("postgres-context-aws-team.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        return new DbDataService(ds);
    }

    /**
     * Return a DbDataService that's connected to the AWS-Integration Postgres database.
     * 
     * @return AWS-DB connection
     */
    public static DbDataService getAwsInt() {
        appContext = new ClassPathXmlApplicationContext("postgres-context-aws-int.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        return new DbDataService(ds);
    }

    public static void closeAppContext() {
        if (appContext != null) {
            ((ClassPathXmlApplicationContext)appContext).close();
        }
        appContext = null;
    }
}
