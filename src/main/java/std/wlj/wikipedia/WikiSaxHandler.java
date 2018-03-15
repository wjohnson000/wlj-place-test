package std.wlj.wikipedia;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WikiSaxHandler extends DefaultHandler {

    int     tblDepth = 0;
    boolean inBody   = false;
    boolean inPara01 = false;
    boolean inPara02 = false;

    StringBuilder buff01  = null;
    StringBuilder buff02  = null;

    public static String parseWikiSAX(String wikiAll) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(wikiAll.getBytes(StandardCharsets.UTF_8));

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            WikiSaxHandler handler = new WikiSaxHandler();
            saxParser.parse(bais, handler);
            return handler.getContents();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("What?? " + ex.getMessage());
            return "";
        }
    }

    public static String parseWikiUrlSAX(String wikiURL) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            WikiSaxHandler handler = new WikiSaxHandler();
            saxParser.parse(wikiURL, handler);
            return handler.getContents();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("What?? " + ex.getMessage());
            return "";
        }
    }
    
    protected String getContents() {
        StringBuilder buff = new StringBuilder();
        buff.append(buff01 == null ? "" : buff01);
        buff.append(buff02 == null ? "" : ("  " + buff02));
        return buff.toString();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase("BODY")) {
            inBody = true;
        } else if (qName.equalsIgnoreCase("TABLE")) {
            tblDepth++;
        } else if (tblDepth == 0) {
            if (qName.equalsIgnoreCase("P")) {
                if (buff01 == null) {
                    inPara01 = true;
                    buff01   = new StringBuilder();
                } else if (buff02 == null) {
                    inPara01 = true;
                    buff02   = new StringBuilder();
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("TABLE")) {
            tblDepth--;
        } else if (qName.equalsIgnoreCase("P")) {
            inPara01 = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inPara01) {
            buff01.append(new String(ch, start, length));
        } else if (inPara02) {
            buff02.append(new String(ch, start, length));
        }
    }
}
