package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.model.net.response.*;

/**
 * A DAO for accessing 'following' data from the database.
 */
public interface StatusDAO {
    FeedResponse getFeed(GetFeedRequest request);
    StoryResponse getStory(GetStoryRequest request);
    void PostStatus(PostStatusRequest request);
}
