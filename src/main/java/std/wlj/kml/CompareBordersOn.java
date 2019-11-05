package std.wlj.kml;

import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;
import org.familysearch.standards.place.dao.dbimpl.RepBoundaryDAOImpl;
import org.familysearch.standards.place.dao.model.DbRepBoundary;

import std.wlj.util.DbConnectionManager;

public class CompareBordersOn {

    private static class Stats {
        int    count;
        long   totalNS;
        long   minNS;
        long   maxNS;
        int    maxId;
        double avgNS;
        Map<Integer,Set<Integer>> results = new TreeMap<>();
    }

    static Random random   = new Random();
    static Stats  oldStats = new Stats();
    static Stats  newStats = new Stats();

    public static void main(String... args) {
        DAOFactory daoFactory = new DAOFactoryImpl(DbConnectionManager.getDataSourceDev55());
        RepBoundaryDAOImpl boundaryDAO = (RepBoundaryDAOImpl)daoFactory.getRepBoundaryDAO();

        for (int i=0;  i<100;  i++) {
            int bdyId = (i == 0) ? 1819 : (i == 1) ? 1855 : random.nextInt(14000) + 1;
            long time0 = System.nanoTime();
//            List<DbRepBoundary> repBs = boundaryDAO.bordersOnOld(bdyId, null, null);
            List<DbRepBoundary> repBs = boundaryDAO.bordersOn(bdyId, null, null);
            long time1 = System.nanoTime();
            accumulate(bdyId, repBs, (time1-time0), oldStats);
            System.out.println("BDY --> " + bdyId + " --> " + (time1-time0) / 1_000_000.0);

            time0 = System.nanoTime();
            repBs = boundaryDAO.bordersOn(bdyId, null, null);
            time1 = System.nanoTime();
            accumulate(bdyId, repBs, (time1-time0), newStats);
            System.out.println("        " + bdyId + " --> " + (time1-time0) / 1_000_000.0);
        }
        System.out.println();

        displayResults();
    }

    static void accumulate(int bdyId, List<DbRepBoundary> repBs, long timeNS, Stats theStats) {
        theStats.count++;
        theStats.totalNS += timeNS;
        theStats.avgNS = theStats.totalNS / theStats.count;
        if (theStats.count == 1) {
            theStats.maxId = bdyId;
            theStats.minNS = timeNS;
            theStats.maxNS = timeNS;
        } else {
            if (timeNS < theStats.minNS) {
                theStats.minNS = timeNS;
            } else if (timeNS > theStats.maxNS) {
                theStats.maxId = bdyId;
                theStats.maxNS = timeNS;
            }
        }

        Set<Integer> borderIds = repBs.stream()
           .map(repB -> repB.getId())
           .collect(Collectors.toSet());
        theStats.results.put(bdyId, borderIds);
    }

    static void displayResults() {
        System.out.println(">>> OLD STATS");
        System.out.println("    count=" + oldStats.count);
        System.out.println("    total=" + (oldStats.totalNS / 1_000_000.0));
        System.out.println("      min=" + (oldStats.minNS / 1_000_000.0));
        System.out.println("      max=" + (oldStats.maxNS / 1_000_000.0) + " --> " + oldStats.maxId);
        System.out.println("      avg=" + (oldStats.avgNS / 1_000_000.0));

        System.out.println("\n>>> NEW STATS");
        System.out.println("    count=" + newStats.count);
        System.out.println("    total=" + (newStats.totalNS / 1_000_000.0));
        System.out.println("      min=" + (newStats.minNS / 1_000_000.0));
        System.out.println("      max=" + (newStats.maxNS / 1_000_000.0) + " --> " + newStats.maxId);
        System.out.println("      avg=" + (newStats.avgNS / 1_000_000.0));

        System.out.println();
        for (Integer bdyId : oldStats.results.keySet()) {
            Set<Integer> borderOld = oldStats.results.get(bdyId);
            Set<Integer> borderNew = newStats.results.get(bdyId);

            Set<Integer> notInNew = new TreeSet<>(borderOld);
            notInNew.removeAll(borderNew);

            Set<Integer> notInOld = new TreeSet<>(borderNew);
            notInOld.removeAll(borderOld);

            System.out.println(">>> " + bdyId + " :: count-old=" + borderOld.size() + " :: count-new=" + borderNew.size());
            if (! notInNew.isEmpty()) {
                System.out.println("    NEW missing: " + notInNew);
            }
            if (! notInOld.isEmpty()) {
                System.out.println("    OLD missing: " + notInNew);
            }
        }
    }
}
