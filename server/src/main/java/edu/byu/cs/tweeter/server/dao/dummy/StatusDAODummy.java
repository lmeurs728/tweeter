//package edu.byu.cs.tweeter.server.dao.dummy;
//
//import edu.byu.cs.tweeter.model.domain.Status;
//import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
//import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
//import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
//import edu.byu.cs.tweeter.model.net.response.FeedResponse;
//import edu.byu.cs.tweeter.model.net.response.StoryResponse;
//import edu.byu.cs.tweeter.server.dao.StatusDAO;
//import edu.byu.cs.tweeter.util.FakeData;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * A DAO for accessing 'following' data from the database.
// */
//public class StatusDAODummy implements StatusDAO {
//    public FeedResponse getFeed(GetFeedRequest request) {
//        // TODO: Generates dummy data. Replace with a real implementation.
//        assert request != null;
//        assert request.getLimit() > 0;
//
//        List<Status> allStatuses = getDummyStatuses();
//        List<Status> responseStatuses = new ArrayList<>(request.getLimit());
//
//        boolean hasMorePages = false;
//
//        if(request.getLimit() > 0) {
//            if (allStatuses != null) {
//                int idx = getStartingIndex(request.getLastStatus(), allStatuses);
//
//                for(int limitCounter = 0; idx < allStatuses.size() && limitCounter < request.getLimit(); idx++, limitCounter++) {
//                    responseStatuses.add(allStatuses.get(idx));
//                }
//
//                hasMorePages = idx < allStatuses.size();
//            }
//        }
//
//        return new FeedResponse(responseStatuses, hasMorePages);
//    }
//
//    public StoryResponse getStory(GetStoryRequest request) {
//        // TODO: Generates dummy data. Replace with a real implementation.
//        assert request != null;
//        assert request.getLimit() > 0;
//
//        List<Status> allStatuses = getDummyStatuses();
//        List<Status> responseStatuses = new ArrayList<>(request.getLimit());
//
//        boolean hasMorePages = false;
//
//        if(request.getLimit() > 0) {
//            if (allStatuses != null) {
//                int idx = getStartingIndex(request.getLastStatus(), allStatuses);
//
//                for(int limitCounter = 0; idx < allStatuses.size() && limitCounter < request.getLimit(); idx++, limitCounter++) {
//                    responseStatuses.add(allStatuses.get(idx));
//                }
//
//                hasMorePages = idx < allStatuses.size();
//            }
//        }
//
//        return new StoryResponse(responseStatuses, hasMorePages);
//    }
//
//    public void PostStatus(PostStatusRequest request) {
//    }
//
//    private int getStartingIndex(Status lastStatus, List<Status> allStatuses) {
//
//        int idx = 0;
//
//        if(lastStatus != null) {
//            // This is a paged request for something after the first page. Find the first item
//            // we should return
//            for (int i = 0; i < allStatuses.size(); i++) {
//                if (lastStatus.equals(allStatuses.get(i))) {
//                    // We found the index of the last item returned last time. Increment to get
//                    // to the first one we should return
//                    idx = i + 1;
//                    break;
//                }
//            }
//        }
//
//        return idx;
//    }
//
//    List<Status> getDummyStatuses() {
//        return getFakeData().getFakeStatuses();
//    }
//
//    FakeData getFakeData() {
//        return new FakeData();
//    }
//}
