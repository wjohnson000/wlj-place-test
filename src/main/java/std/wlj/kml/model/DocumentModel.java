package std.wlj.kml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={ "name", "description", "style", "schema", "folders", "placemarks" })
public class DocumentModel {

    private String                id;
    private String                name;
    private String                description;
    private StyleModel            style;
    private SchemaModel           schema;
    private List<FolderModel>     folders;
    private List<PlacemarkModel>  placemarks;

    // Default constructor required for JAXB serialization
    public DocumentModel() { }

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
    
    public void setStyle(StyleModel model) {
        style = model;
    }
    
    @XmlElement(name="Style")
    public StyleModel getStyle() {
        return style;
    }

    public void setSchema(SchemaModel model) {
        schema = model;
    }

    @XmlElement(name="Schema")
    public SchemaModel getSchema() {
        return schema;
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

    @XmlTransient
    public List<PlacemarkModel> getPlacemarksAll() {
        List<PlacemarkModel> results = new ArrayList<>();

        if (folders != null) {
            results = folders.stream()
                .filter(folder -> folder.getPlacemarks() != null)
                .flatMap(folder -> folder.getPlacemarks().stream())
                .collect(Collectors.toList());
        }

        if (placemarks != null) {
            results.addAll(getPlacemarks());
        }

        return results;
    }

    public void addFolder(FolderModel folder) {
        if (folders == null) {
            folders = new ArrayList<>();
        }
        folders.add(folder);
    }

    public void addPlacemark(PlacemarkModel placemark) {
        if (placemarks == null) {
            placemarks = new ArrayList<>();
        }
        placemarks.add(placemark);
    }
}
