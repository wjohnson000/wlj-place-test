package org.familysearch.std.wlj.util;

import javax.sql.DataSource;

import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DbManager {

    /**
     * Return a DbDataService that's connected to a local Postgres database.
     * 
     * @return Local DB connection
     */
    public static DbDataService getLocal() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        DbDataService dataService = new DbDataService(ds);

        ((ClassPathXmlApplicationContext)appContext).close();

        return dataService;
    }

    /**
     * Return a DbDataService that's connected to the DEV-DB Postgres database.
     * 
     * @return DEV-DB connection
     */
    public static DbDataService getDevDb() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("postgres-context-devdb.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        DbDataService dataService = new DbDataService(ds);

        ((ClassPathXmlApplicationContext)appContext).close();

        return dataService;
    }

    /**
     * Return a DbDataService that's connected to the AWS-Team Postgres database.
     * 
     * @return AWS-DB connection
     */
    public static DbDataService getAwsTeam() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("postgres-context-aws-team.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        DbDataService dataService = new DbDataService(ds);

        ((ClassPathXmlApplicationContext)appContext).close();

        return dataService;
    }

    /**
     * Return a DbDataService that's connected to the AWS-Integration Postgres database.
     * 
     * @return AWS-DB connection
     */
    public static DbDataService getAwsInt() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("postgres-context-aws-int.xml");
        DataSource ds = (DataSource)appContext.getBean("dataSource");
        DbDataService dataService = new DbDataService(ds);

        ((ClassPathXmlApplicationContext)appContext).close();

        return dataService;
    }
}
