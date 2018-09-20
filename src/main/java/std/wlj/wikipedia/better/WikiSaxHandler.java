/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.wikipedia.better;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.familysearch.standards.core.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author wjohnson000
 *
 */
public final class WikiSaxHandler extends DefaultHandler {

    private static final Logger logger = new Logger(WikiSaxHandler.class);

    private static final String REVISIONS_TAG = "REVISIONS";
    private static final String REV_TAG       = "REV";
    private static final String SLOTS_TAG     = "SLOTS";
    private static final String SLOT_TAG      = "SLOT";
    private static final String WIKITEXT_TAG  = "WIKITEXT";

    private static final SAXParserFactory factory = SAXParserFactory.newInstance();

    boolean inContent = false;
    StringBuilder contextBuff = new StringBuilder();

    public String parseWikiSAX(String wikiURL) {
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(wikiURL, this);

            return contextBuff.toString();
        } catch (Exception ex) {
            logger.debug(ex, "WIKIPEDIA-SAX", "Unable to parse data from wikipedia", "Error", ex.getMessage());
            return "";
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName.toUpperCase()) {
            case REVISIONS_TAG:
            case REV_TAG:
            case SLOTS_TAG:
            case SLOT_TAG:
            case WIKITEXT_TAG:
                inContent = true;
                contextBuff = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName.toUpperCase()) {
            case REVISIONS_TAG:
            case REV_TAG:
            case SLOTS_TAG:
            case SLOT_TAG:
            case WIKITEXT_TAG:
                inContent = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inContent) {
            contextBuff.append(new String(ch, start, length));
        }
    }
}
