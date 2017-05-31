package std.wlj.kml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class SchemaDataModel {

    private List<SimpleDataModel>  dataList;
    private String                 schemaUrl;

    // Default constructor required for JAXB serialization
    public SchemaDataModel() { }

    public void setSchemaUrl(String url) {
        schemaUrl = url;
    }

    @XmlAttribute(name="schemaUrl")
    public String getSchemaUrl() {
        return schemaUrl;
    }

    public void setSimpleData(List<SimpleDataModel> dataList) {
        this.dataList = dataList;
    }

    @XmlElement(name="SimpleData")
    public List<SimpleDataModel> getSimpleData() {
        return dataList;
    }
}
