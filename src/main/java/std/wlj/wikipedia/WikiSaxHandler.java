package std.wlj.wikipedia;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.familysearch.standards.core.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WikiSaxHandler extends DefaultHandler {


    private static final Logger logger = new Logger(WikiSaxHandler.class);

    private static final String BODY_TAG        = "BODY";
    private static final String PARAGRAPH_TAG   = "P";
    private static final String SUPERSCRIPT_TAG = "SUP";
    private static final String TABLE_TAG       = "TABLE";

    int     tblDepth = 0;
    boolean inBody   = false;
    boolean inSuper  = false;
    boolean inPara01 = false;
    boolean inPara02 = false;

    StringBuilder buff01  = null;
    StringBuilder buff02  = null;

    public List<String> parseWikiSAX(String wikiURL) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(wikiURL, this);
            return getContents();
        } catch (Exception ex) {
            logger.debug(ex, "WIKIPEDIA-SAX", "Unable to parse data from wikipedia", "Error", ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * @return Return the data from the first two paragraphs, if anything was extracted.
     */
    protected List<String> getContents() {
        if (buff01 == null) {
            return Collections.emptyList();
        } else if (buff02 == null) {
            return Arrays.asList(buff01.toString());
        } else {
            return Arrays.asList(buff01.toString(), buff02.toString());
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase(BODY_TAG)) {
            inBody = true;
        } else if (qName.equalsIgnoreCase(TABLE_TAG)) {
            tblDepth++;
        } else if (qName.equalsIgnoreCase(SUPERSCRIPT_TAG)) {
            inSuper = true;
        } else if (tblDepth == 0) {
            if (qName.equalsIgnoreCase(PARAGRAPH_TAG)) {
                if (buff01 == null) {
                    inPara01 = true;
                    buff01   = new StringBuilder();
                } else if (buff02 == null) {
                    inPara02 = true;
                    buff02   = new StringBuilder();
                } else {
                    inPara02 = false;
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(TABLE_TAG)) {
            tblDepth--;
        } else if (qName.equalsIgnoreCase(PARAGRAPH_TAG)) {
            if (inPara01) {
                inPara01 = false;
            } else if (inPara02) {
                inPara02 = false;
            }
        } else if (qName.equalsIgnoreCase(SUPERSCRIPT_TAG)) {
            inSuper = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (! inSuper) {
            if (inPara01) {
                buff01.append(new String(ch, start, length));
            } else if (inPara02) {
                buff02.append(new String(ch, start, length));
            }
        }
    }
}
