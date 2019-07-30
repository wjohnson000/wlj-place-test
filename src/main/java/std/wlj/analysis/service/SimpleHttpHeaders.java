package std.wlj.analysis.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class SimpleHttpHeaders implements HttpHeaders {

    @Override
    public List<String> getRequestHeader(String name) {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getRequestHeaders() {
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
        headers.putSingle("user_agent", "wjohnson007");
        return headers;
    }

    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        return null;
    }

    @Override
    public List<Locale> getAcceptableLanguages() {
        return null;
    }

    @Override
    public MediaType getMediaType() {
        return null;
    }

    @Override
    public Locale getLanguage() {
        return null;
    }

    @Override
    public Map<String, Cookie> getCookies() {
        return null;
    }

//    @Override
//    public String getHeaderString(String name) {
//        return null;
//    }
//
//    @Override
//    public Date getDate() {
//        return new Date();
//    }
//
//    @Override
//    public int getLength() {
//        return 0;
//    }

}
