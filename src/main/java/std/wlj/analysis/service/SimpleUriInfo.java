package std.wlj.analysis.service;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class SimpleUriInfo implements UriInfo {

    String placeName;

    public SimpleUriInfo(String placeName) {
        this.placeName = placeName;
    }

    @Override
    public String getPath() {
        return "http://localhost:8080/std-ws-place/places/request";
    }

    @Override
    public String getPath(boolean decode) {
        return getPath();
    }

    @Override
    public List<PathSegment> getPathSegments() {
        return null;
    }

    @Override
    public List<PathSegment> getPathSegments(boolean decode) {
        return null;
    }

    @Override
    public URI getRequestUri() {
        return null;
    }

    @Override
    public UriBuilder getRequestUriBuilder() {
        return null;
    }

    @Override
    public URI getAbsolutePath() {
        return null;
    }

    @Override
    public UriBuilder getAbsolutePathBuilder() {
        return null;
    }

    @Override
    public URI getBaseUri() {
        return null;
    }

    @Override
    public UriBuilder getBaseUriBuilder() {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters() {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters(boolean decode) {
        return getPathParameters();
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.putSingle("partial", "false");
        params.putSingle("wildcards", "false");
        params.putSingle("name", placeName);
        params.putSingle("text", placeName);
        return params;
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
        return getQueryParameters();
    }

    @Override
    public List<String> getMatchedURIs() {
        return null;
    }

    @Override
    public List<String> getMatchedURIs(boolean decode) {
        return null;
    }

    @Override
    public List<Object> getMatchedResources() {
        return null;
    }

    @Override
    public URI resolve(URI uri) {
        return uri;
    }

    @Override
    public URI relativize(URI uri) {
        return uri;
    }

}
