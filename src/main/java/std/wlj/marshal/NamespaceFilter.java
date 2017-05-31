package std.wlj.marshal;

import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;

import std.wlj.kml.model.KmlModel;

public class NamespaceFilter extends XMLFilterImpl {
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(KmlModel.NAMESPACE, localName, qName, atts);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(KmlModel.NAMESPACE, localName, qName);
    }
}
