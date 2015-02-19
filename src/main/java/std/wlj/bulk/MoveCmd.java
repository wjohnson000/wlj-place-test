package std.wlj.bulk;

import java.util.List;

import org.familysearch.standards.place.tools.bulk.ClientException;
import org.familysearch.standards.place.tools.bulk.PlaceServiceClient;
import org.familysearch.standards.place.ws.model.AttributeModel;
import org.familysearch.standards.place.ws.model.CitationModel;
import org.familysearch.standards.place.ws.model.JurisdictionModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.RootModel;


public class MoveCmd {
	static PlaceServiceClient client;

	public static void main(String... args) throws ClientException {
		int repId = 2387478;
	    int newParentId = 431502;

		client = new PlaceServiceClient("http://localhost:8080/std-ws-place/places");

		PlaceRepresentationModel placeRep  = client.getPlaceRepresentation(repId);
		PlaceRepresentationModel newParent = client.getPlaceRepresentation(newParentId);
		System.out.println("REP: " + placeRep.getId());
		System.out.println("PAR: " + newParent.getId());

		List<AttributeModel> attrs = client.getAttributesForPlaceRepresentation(repId);
		List<CitationModel>  citns = client.getCitationsForPlaceRepresentation(repId);
		System.out.println("Attrs: " + attrs.size());
		System.out.println("Citns: " + citns.size());

		RootModel            newRep = createModel(placeRep, newParent);
		RootModel            createRep = client.createPlaceRepresentation(newRep);
		System.out.println("NewREP: " + createRep.getPlaceRepresentation().getId());

		client.deletePlaceRepresentation(repId, createRep.getPlaceRepresentation().getId());
		System.out.println("Delete done ...");

		for (AttributeModel attr : attrs) {
			RootModel root = new RootModel();
			attr.setRepId(createRep.getPlaceRepresentation().getId());
			root.setAttribute(attr);
			client.createAttribute(root);
		}
		System.out.println("Attributes created ...");

		for (CitationModel citn : citns) {
			RootModel root = new RootModel();
			citn.setRepId(createRep.getPlaceRepresentation().getId());
			root.setCitation(citn);
			client.createCitation(root);
		}
		System.out.println("Citations created ...");
	}

    private static RootModel createModel(PlaceRepresentationModel child, PlaceRepresentationModel parent) {
        RootModel           newRep = new RootModel();
        JurisdictionModel   newJurisdiction;

        newJurisdiction = new JurisdictionModel();
        newJurisdiction.setId(parent.getId());
        child.setJurisdiction(newJurisdiction);
        newRep.setPlaceRepresentation(child);

        return newRep;
    }

}
