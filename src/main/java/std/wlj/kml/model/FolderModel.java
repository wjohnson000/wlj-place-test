package std.wlj.kml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={ "name", "description", "timeSpan", "folders", "placemarks" })
public class FolderModel {

    private String                id;
    private String                name;
    private String                description;
    private TimeSpanModel         timeSpan;
    private List<FolderModel>     folders;
    private List<PlacemarkModel>  placemarks;

    // Default constructor required for JAXB serialization
    public FolderModel() { }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="name")
    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name="description")
    public String getDescription() {
        return description;
    }

    public void setTimeSpan(TimeSpanModel timeSpan) {
        this.timeSpan = timeSpan;
    }

    @XmlElement(name="TimeSpan")
    public TimeSpanModel getTimeSpan() {
        return timeSpan;
    }
    
    public void setFolders(List<FolderModel> folders) {
        this.folders = folders;
    }
    
    @XmlElement(name="Folder")
    public List<FolderModel> getFolders() {
        return folders;
    }

    public void setPlacemarks(List<PlacemarkModel> placemarks) {
        this.placemarks = placemarks;
    }

    @XmlElement(name="Placemark")
    public List<PlacemarkModel> getPlacemarks() {
        return placemarks;
    }

    public void addPlacemark(PlacemarkModel placemark) {
        if (placemarks == null) {
            placemarks = new ArrayList<>();
        }
        placemarks.add(placemark);
    }
}
