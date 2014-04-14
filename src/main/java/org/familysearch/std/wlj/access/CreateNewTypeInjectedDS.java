package org.familysearch.std.wlj.access;

import java.util.*;

import javax.sql.DataSource;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.TypeCategory;
import org.familysearch.standards.place.data.TypeDTO;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.service.DbConfigurator;
import org.familysearch.standards.place.service.DbDataService;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class CreateNewTypeInjectedDS {
    public static void main(String[] args) throws PlaceDataException {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("postgres-context-aws-team.xml");
        AutowireCapableBeanFactory autowireFactory = appContext.getAutowireCapableBeanFactory();
        System.out.println("AW-Factory: " + autowireFactory);
        autowireFactory.autowireBean(DbConfigurator.class);
        autowireFactory.autowireBean(DataSource.class);

//        autowireFactory.
//        DataSource ds = (DataSource)appContext.getBean("dataSource");
//        System.out.println("Service: " + ds);

        SolrDataService solrService = new SolrDataService();
        DbDataService dbService = new DbDataService();

        PlaceDataServiceImpl service = new PlaceDataServiceImpl(dbService, solrService);

        Set<TypeDTO> nameTypes = service.getAllTypes(TypeCategory.NAME);
        System.out.println("\nList of all NAME types ...");
        for (TypeDTO nameType : nameTypes) {
            System.out.println(nameType.getId() + " . " + nameType.getCode() + " --> " + nameType.getAllNames());
        }

        Map<String,String> names = new HashMap<String,String>();
        Map<String,String> descr = new HashMap<String,String>();
        names.put("en", "name-en");
        names.put("fr", "name-fr");
        names.put("de", "name-de");
        names.put("ja", "name-ja");
        descr.put("en", "descr-en");
        descr.put("de", "descr-de");
        descr.put("ja", "descr-ja");

        TypeDTO newType = new TypeDTO(1111, "WLJ-TEST-3", names, descr, true);
        service.create(TypeCategory.NAME, newType, "wjohnson000");
        nameTypes = service.getAllTypes(TypeCategory.NAME);
        System.out.println("\nList of all NAME types ...");
        for (TypeDTO nameType : nameTypes) {
            System.out.println(nameType.getId() + " . " + nameType.getCode() + " --> " + nameType.getAllNames());
        }

        ((ClassPathXmlApplicationContext)appContext).close();
        service.shutdown();
    }
}
