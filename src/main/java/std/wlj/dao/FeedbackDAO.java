package std.wlj.dao;

import java.util.List;

import org.familysearch.standards.place.dao.dbimpl.FeedbackDAOImpl;
import org.familysearch.standards.place.dao.model.DbFeedback;

import std.wlj.datasource.DbConnectionManager;

public class FeedbackDAO {
    public static void main(String...args) {
        FeedbackDAOImpl fbDAO = new FeedbackDAOImpl(DbConnectionManager.getDataSourceWLJ());
        List<DbFeedback> feedbacks = fbDAO.read("QA", null, 490, null, null);
        feedbacks.forEach(fb -> System.out.println(fb.getId() + " . " + fb.getComment() + " . " + fb.getStatusId()));
    }
}
