package std.wlj.extxref;

import org.familysearch.standards.place.data.solr.SolrConfig;


public class SolrConfigSimple extends SolrConfig {

    private String readConnStr;
    private String writConnStr;


    public SolrConfigSimple(String readConnStr, String writConnStr) {
        this.readConnStr = readConnStr;
        this.writConnStr = writConnStr;
    }

    /* (non-Javadoc)
     * @see org.familysearch.standards.place.data.solr.SolrConfig#getReadLocation()
     */
    @Override
    public String getReadLocation() {
        return readConnStr;
    }

    /* (non-Javadoc)
     * @see org.familysearch.standards.place.data.solr.SolrConfig#getWriteLocation()
     */
    @Override
    public String getWriteLocation() {
        return writConnStr;
    }

    /* (non-Javadoc)
     * @see org.familysearch.standards.place.data.solr.SolrConfig#isReadEmbedded()
     */
    @Override
    public boolean isReadEmbedded() {
        return readConnStr == null  ||  ! readConnStr.startsWith("http");
    }

    /* (non-Javadoc)
     * @see org.familysearch.standards.place.data.solr.SolrConfig#isWriteEmbedded()
     */
    @Override
    public boolean isWriteEmbedded() {
        return writConnStr == null  ||  ! writConnStr.startsWith("http");
    }

    
}
