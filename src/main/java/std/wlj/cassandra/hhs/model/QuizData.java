/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author wjohnson000
 *
 */
public class QuizData {

    public String         id;
    public String         collectionId;
    public String         externalId;
    public String         title;
    public String         question;
    public String         quizItemTag;
    public String         category;
    public String         subcategory;
    public String         modifyUser;
    public LocalDateTime  modifyDate;
    public Set<String>    languages = new TreeSet<>();

}
