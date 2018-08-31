package std.wlj.wikipedia;

import java.io.ByteArrayInputStream;
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
    private static final String DIV_TAG         = "DIV";
    private static final String PARAGRAPH_TAG   = "P";
    private static final String SPAN_TAG        = "SPAN";
    private static final String SUPERSCRIPT_TAG = "SUP";
    private static final String TABLE_TAG       = "TABLE";

    int     tblDepth  = 0;
    int     divDepth  = 0;
    int     spanDepth = 0;
    boolean inBody    = false;
    boolean inCoord   = false;
    boolean inSuper   = false;
    boolean inPara01  = false;
    boolean inPara02  = false;
    boolean tblFound  = false;
//    int     level     = 0;

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
     * This method is here for testing purposes only!!  It allows us to control exactly what
     * is coming in and hence the generated results.
     * 
     * @param rawText HTML text to be parsed
     * @return First one or two paragraphs, as per the rules
     */
    protected List<String> parseWikiSAXRawText(String rawText) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(rawText.getBytes());
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(bais, this);
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
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//for (int i=0;  i<level;  i++) System.out.print("  ");
//System.out.println("SSS::" + qName);
//level++;
        if (qName.equalsIgnoreCase(BODY_TAG)) {
            inBody = true;
        } else if (qName.equalsIgnoreCase(TABLE_TAG)) {
            tblFound = true;
            tblDepth++;
        } else if (qName.equalsIgnoreCase(DIV_TAG)) {
            divDepth++;
        } else if (qName.equalsIgnoreCase(SPAN_TAG)) {
            if ("coordinates".equalsIgnoreCase(attributes.getValue("id"))) {
                inCoord = true;
                spanDepth++;
            } if (inCoord) {
                spanDepth++;
            }
        } else if (qName.equalsIgnoreCase(SUPERSCRIPT_TAG)) {
            inSuper = true;
        } else if (tblDepth == 0  &&  tblFound) {
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
//level--;
//for (int i=0;  i<level;  i++) System.out.print("  ");
//System.out.println("EEE::" + qName);
        if (qName.equalsIgnoreCase(TABLE_TAG)) {
            tblDepth--;
        } else if (qName.equalsIgnoreCase(DIV_TAG)) {
            divDepth--;
        } else if (qName.equalsIgnoreCase(SPAN_TAG)) {
            if (inCoord) {
                spanDepth--;
            }
            if (spanDepth == 0) {
                inCoord = false;
            }
        } else if (qName.equalsIgnoreCase(SUPERSCRIPT_TAG)) {
            inSuper = false;
        } else if (qName.equalsIgnoreCase(PARAGRAPH_TAG)) {
            if (inPara01) {
                inPara01 = false;
            } else if (inPara02) {
                inPara02 = false;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
//for (int i=0;  i<level;  i++) System.out.print("  ");
//System.out.print(tblDepth + "." + divDepth + "." + inSuper + "." + inCoord + "." + inPara01 + "." + inPara02 + "...");
//System.out.println(new String(ch, start, length));
        if (! inSuper  &&  ! inCoord) {
            if (inPara01) {
                buff01.append(new String(ch, start, length));
            } else if (inPara02) {
                buff02.append(new String(ch, start, length));
            }
        }
    }
}