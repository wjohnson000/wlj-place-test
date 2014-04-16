package std.wlj.util;

import org.familysearch.standards.place.data.solr.SolrConfig;


/**
 * Simple class that allows programmatic specification of the SOLR read and write
 * locations.
 * 
 * @author wjohnson000
 *
 */
public class SolrConfigManual extends SolrConfig {

    private String readLocation;
    private String writeLocation;


    /**
     * Specify the read and write locations, which should be a file path for embedded
     * SOLR, or a URL for remote SOLR.
     * 
     * @param readLocation read location
     * @param writeLocation write location
     */
    public SolrConfigManual(String readLocation, String writeLocation) {
        this.readLocation = readLocation;
        this.writeLocation = writeLocation;
    }

    /* (non-Javadoc)
     * @see org.familysearch.standards.place.data.solr.SolrConfig#isReadEmbedded()
     */
    @Override
    public boolean isReadEmbedded() {
        return readLocation == null  ||  ! readLocation.startsWith("http");
    }

    /* (non-Javadoc)
     * @see org.familysearch.standards.place.data.solr.SolrConfig#isWriteEmbedded()
     */
    @Override
    public boolean isWriteEmbedded() {
        return writeLocation == null  ||  ! writeLocation.startsWith("http");
    }

    /* (non-Javadoc)
     * @see org.familysearch.standards.place.data.solr.SolrConfig#getReadLocation()
     */
    @Override
    public String getReadLocation() {
        return readLocation;
    }

    /* (non-Javadoc)
     * @see org.familysearch.standards.place.data.solr.SolrConfig#getWriteLocation()
     */
    @Override
    public String getWriteLocation() {
        return writeLocation;
    }

    /* (non-Javadoc)
     * @see org.familysearch.standards.place.data.solr.SolrConfig#getExternalDataLocation()
     */
    @Override
    public String getExternalDataLocation() {
        return null;
    }

}
