package edu.byu.cs.tweeter.server.service;


import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.StatusDAODynamo;
import org.junit.Assert;
import org.junit.Test;

public class StatusDAODynamoTest {
    private final StatusDAO statusDAO = new StatusDAODynamo();

    @Test
    public void getFeed() {
        statusDAO.getFeed(new GetFeedRequest(null, 0, 10, "@lmeurs", true));
    }

    @Test
    public void getStory() {
        statusDAO.getStory(new GetStoryRequest(null, 0, 10, "@lmeurs", true));
    }

    @Test
    public void postStatus() {
        statusDAO.PostStatus(new PostStatusRequest(null,
                new Status("Lance was here",
                        new User("", "", "@lmeurs", ""),
                        null, null, null)));
    }
}
