package org.familysearch.std.wlj.cache;

public class MyObject implements java.io.Serializable {

    private static final long serialVersionUID = 4764874418229659359L;

    private static int nextKey = 1;

    int      key;
    String   text;
    String   firstName;
    String   lastName;
    String[] addressList;
    double   latitude;
    double   longitude;

    public static MyObject getInstance() {
        MyObject what = new MyObject();

        what.key         = nextKey++;
        what.text        = "Some Text ... " + what.key;
        what.firstName   = "First Name ...";
        what.lastName    = "Last Name ...";
        what.addressList = new String[] { "Address one."+what.key, "Address two."+what.key, "Address three."+what.key, "Address four."+what.key };
        what.latitude    = -44.4;
        what.longitude   = 55.5;

        return what;
    }

    /**
     * No one else can make one of dese tings ...
     */
    private MyObject() { }
}
