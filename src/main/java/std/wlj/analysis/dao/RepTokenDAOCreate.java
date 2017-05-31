package std.wlj.analysis.dao;

import java.util.Arrays;

import org.familysearch.standards.analysis.dal.impl.RepTokenDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbRepToken;

import std.wlj.datasource.DbConnectionManager;

public class RepTokenDAOCreate {
    public static void main(String...args) {
        RepTokenDAOImpl repTokenDAO = new RepTokenDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        DbRepToken newRepToken = new DbRepToken();
        newRepToken.setResultRepId(3);
        newRepToken.setRawToken("[token-02]");
        newRepToken.setNormalizedToken("token-02");
        newRepToken.setTokenTypes(Arrays.asList("type-03"));
        newRepToken.setTokenIndex(2);
        newRepToken.setVariantText("variant-text");
        newRepToken.setVariantTypeCode("the-type-code");
        newRepToken.setVariantLocale("en");
        newRepToken.setMatchedRepId(2345678);

        DbRepToken dbRepToken = repTokenDAO.create(newRepToken);
        System.out.println("DB-Rep-Token: " + dbRepToken);
        System.out.println("          id: " + dbRepToken.getId());
        System.out.println("  int-rep-id: " + dbRepToken.getResultRepId());
        System.out.println("       token: " + dbRepToken.getRawToken());
        System.out.println("  norm-token: " + dbRepToken.getNormalizedToken());
        System.out.println("  token-type: " + dbRepToken.getTokenTypes());
        System.out.println("   token-ndx: " + dbRepToken.getTokenIndex());
        System.out.println("    var-text: " + dbRepToken.getVariantText());
        System.out.println("   type-code: " + dbRepToken.getVariantTypeCode());
        System.out.println("      locale: " + dbRepToken.getVariantLocale());
        System.out.println("   match-rep: " + dbRepToken.getMatchedRepId());
    }
}
