/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.*;

import org.testng.Assert;

import std.wlj.marshal.POJOMarshalUtil;

/**
 * @author wjohnson000
 *
 */
public class ZzzMarshalItem {

    public static void main(String... args) {
        Item item = new Item();
        item.setId("123-abcd");
        item.setType("EVENT");
        item.setName("Moon Landing");
        item.setCreateDate("2019-01-01");
        item.setCreateUser("no-body");
        item.setModifyDate("2019-11-01");
        item.setModifyUser("some-body");
        item.setProperties(null);

        marshalJson(item);
        marshalXml(item);

        Map<String, String> props = new HashMap<>();
        props.put("this.one", "some-property");
        props.put("that.oue", "other-property");

        item.setProperties(props);

        marshalJson(item);
        marshalXml(item);
    }

    static void marshalJson(Item item) {
        String json = POJOMarshalUtil.toJSON(item);
        System.out.println("\n\nJSON:\n" + json);

        Item itemX = POJOMarshalUtil.fromJSON(json, Item.class);
        System.out.println("  Item: " + itemX);

        Assert.assertEquals(itemX.getId(), item.getId());
        Assert.assertEquals(itemX.getType(), item.getType());
        Assert.assertEquals(itemX.getName(), item.getName());
        Assert.assertEquals(itemX.getCreateDate(), item.getCreateDate());
        Assert.assertEquals(itemX.getCreateUser(), item.getCreateUser());
        Assert.assertEquals(itemX.getModifyDate(), item.getModifyDate());
        Assert.assertEquals(itemX.getModifyUser(), item.getModifyUser());

        if (item.getProperties() == null) {
            Assert.assertNull(itemX.getProperties());
        } else {
            Assert.assertEquals(itemX.getProperties().size(), item.getProperties().size());
        }
    }

    static void marshalXml(Item item) {
        String xml = POJOMarshalUtil.toXML(item);
        System.out.println("\n\nXML:\n" + xml);

        Item itemX = POJOMarshalUtil.fromXML(xml, Item.class);
        System.out.println("  Item: " + itemX);

        Assert.assertEquals(itemX.getId(), item.getId());
        Assert.assertEquals(itemX.getType(), item.getType());
        Assert.assertEquals(itemX.getName(), item.getName());
        Assert.assertEquals(itemX.getCreateDate(), item.getCreateDate());
        Assert.assertEquals(itemX.getCreateUser(), item.getCreateUser());
        Assert.assertEquals(itemX.getModifyDate(), item.getModifyDate());
        Assert.assertEquals(itemX.getModifyUser(), item.getModifyUser());

        if (item.getProperties() == null) {
            Assert.assertNull(itemX.getProperties());
        } else {
            Assert.assertEquals(itemX.getProperties().size(), item.getProperties().size());
        }
    }
}
