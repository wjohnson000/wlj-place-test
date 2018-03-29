package std.wlj.wikipedia;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TitleSaxHandler extends DefaultHandler {

    private static final String HEAD_TAG  = "HEAD";
    private static final String TITLE_TAG = "TITLE";

    private static SAXParserFactory factory = SAXParserFactory.newInstance();

    boolean inHead   = false;
    boolean inTitle  = false;
    String  title    = "";

    public String parseTitle(String siteURL) {
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(siteURL, this);
            return title;
        } catch (Exception ex) {
            return title;
        }
    }

    public String parseTitleFromHtml(String rawHtml) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(rawHtml.getBytes(Charset.forName("UTF-8")));
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(bais, this);
            return title;
        } catch (Exception ex) {
            return title;
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase(HEAD_TAG)) {
            inHead = true;
        } else if (qName.equalsIgnoreCase(TITLE_TAG)) {
            inTitle = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(HEAD_TAG)) {
            inHead = false;
        } else if (qName.equalsIgnoreCase(TITLE_TAG)) {
            inTitle = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inHead  &&  inTitle) {
            title = new String(ch, start, length);
            throw new SAXException("We found the title ... time to bail!!");
        }
    }
}
