package std.wlj.appdata;

import org.apache.solr.client.solrj.SolrQuery;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

public class SolrQueryMatcher extends ArgumentMatcher<SolrQuery> {

    private final String queryStr;

    public SolrQueryMatcher(String expected) {
        this.queryStr = expected;
    }

    @Override
    public boolean matches(Object argument) {
        String temp = String.valueOf(argument);
        return queryStr != null  &&  temp.contains(queryStr);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(queryStr == null ? null : queryStr);
    }
}
