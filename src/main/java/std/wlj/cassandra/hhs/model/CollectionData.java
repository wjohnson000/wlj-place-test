/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs.model;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author wjohnson000
 *
 */
public class CollectionData {

    public String      id;
    public String      name;
    public String      description;
    public Set<String> languages = new TreeSet<>();
    public Set<String> types     = new TreeSet<>();
    public int         memberCount = 0;

}
