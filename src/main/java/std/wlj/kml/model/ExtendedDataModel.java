package std.wlj.kml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={ "data", "schemaData" })
public class ExtendedDataModel {

    private List<DataModel>  dataList;
    private SchemaDataModel  schemaData;

    // Default constructor required for JAXB serialization
    public ExtendedDataModel() { }

    public void setData(List<DataModel> dataList) {
        this.dataList = dataList;
    }

    @XmlElement(name="Data")
    public List<DataModel> getData() {
        return dataList;
    }

    public void setSchemaData(SchemaDataModel schemaData) {
        this.schemaData = schemaData;
    }

    @XmlElement(name="SchemaData")
    public SchemaDataModel getSchemaData() {
        return schemaData;
    }
}
