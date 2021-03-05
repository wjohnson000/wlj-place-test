/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

/**
 * @author wjohnson000
 *
 */
public class DeleteQuiz {

    public static void main(String... args) {
        CassandraHelper cHelper = new CassandraHelper();
        cHelper.deleteItem("MM98-7J1", "QUIZ");

        System.exit(0);
    }
}
