/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.Properties;

/**
 * @author wjohnson000
 *
 */
public class String2Bytes2String {

    public static void main(String...args) {
        StringBuilder buff = new StringBuilder();
        for (char ch=65;  ch<15000;  ch+=75) {
            buff.append(ch);
        }
        String str01 = buff.toString();
        System.out.println(str01);

        byte[] bytes = str01.getBytes();
        String str02 = new String(bytes);
        System.out.println(str02);

        int len = Math.min(str01.length(), str02.length());
        for (int i=0;  i<len;  i++) {
            System.out.println(String.valueOf(i) + "\t" + Integer.toHexString(str01.charAt(i)) + " <> " + (char)str01.charAt(i));
            System.out.println(                    "\t" + Integer.toHexString(str02.charAt(i)) + " <> " + (char)str02.charAt(i));
        }
    }
}
