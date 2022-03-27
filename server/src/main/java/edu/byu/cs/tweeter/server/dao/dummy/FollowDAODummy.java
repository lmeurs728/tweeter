//package edu.byu.cs.tweeter.server.dao.dummy;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import edu.byu.cs.tweeter.model.domain.User;
//import edu.byu.cs.tweeter.model.net.request.*;
//import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
//import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
//import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
//import edu.byu.cs.tweeter.server.dao.FollowDAO;
//import edu.byu.cs.tweeter.util.FakeData;
//
///**
// * A DAO for accessing 'following' data from the database.
// */
//public class FollowDAODummy implements FollowDAO {
//
//    /**
//     * Gets the count of users from the database that the user specified is following. The
//     * current implementation uses generated data and doesn't actually access a database.
//     *
//     * @param follower the User whose count of how many following is desired.
//     * @return said count.
//     */
//    public Integer getFolloweeCount(User follower) {
//        // TODO: uses the dummy data.  Replace with a real implementation.
//        assert follower != null;
//        return getDummyFollowees().size();
//    }
//
//    public Integer getFollowerCount(User followee) {
//        // TODO: uses the dummy data.  Replace with a real implementation.
//        assert followee != null;
//        return getDummyFollowers().size();
//    }
//
//    /**
//     * Gets the users from the database that the user specified in the request is following. Uses
//     * information in the request object to limit the number of followees returned and to return the
//     * next set of followees after any that were returned in a previous request. The current
//     * implementation returns generated data and doesn't actually access a database.
//     *
//     * @param request contains information about the user whose followees are to be returned and any
//     *                other information required to satisfy the request.
//     * @return the followees.
//     */
//    public FollowingResponse getFollowees(FollowingRequest request) {
//        // TODO: Generates dummy data. Replace with a real implementation.
//        assert request.getLimit() > 0;
//        assert request.getFollowerAlias() != null;
//
//        List<User> allFollowees = getDummyFollowees();
//        List<User> responseFollowees = new ArrayList<>(request.getLimit());
//
//        boolean hasMorePages = false;
//
//        if(request.getLimit() > 0) {
//            if (allFollowees != null) {
//                int followeesIndex = getFolloweesStartingIndex(request.getLastFolloweeAlias(), allFollowees);
//
//                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
//                    responseFollowees.add(allFollowees.get(followeesIndex));
//                }
//
//                hasMorePages = followeesIndex < allFollowees.size();
//            }
//        }
//
//        return new FollowingResponse(responseFollowees, hasMorePages);
//    }
//
//    public FollowersResponse getFollowers(FollowersRequest request) {
//        // TODO: Generates dummy data. Replace with a real implementation.
//        assert request.getLimit() > 0;
//        assert request.getFolloweeAlias() != null;
//
//        List<User> allFollowers = getDummyFollowers();
//        List<User> responseFollowers = new ArrayList<>(request.getLimit());
//
//        boolean hasMorePages = false;
//
//        if(request.getLimit() > 0) {
//            if (allFollowers != null) {
//                int followeesIndex = getFolloweesStartingIndex(request.getLastFollowerAlias(), allFollowers);
//
//                for(int limitCounter = 0; followeesIndex < allFollowers.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
//                    responseFollowers.add(allFollowers.get(followeesIndex));
//                }
//
//                hasMorePages = followeesIndex < allFollowers.size();
//            }
//        }
//
//        return new FollowersResponse(responseFollowers, hasMorePages);
//    }
//
//    @Override
//    public void Follow(FollowRequest request) {
//    }
//
//    @Override
//    public void Unfollow(UnfollowRequest request) {
//    }
//
//    @Override
//    public IsFollowerResponse IsFollower(IsFollowerRequest request) {
//        return new IsFollowerResponse(new Random().nextInt() > 0);
//    }
//
//    /**
//     * Determines the index for the first followee in the specified 'allFollowees' list that should
//     * be returned in the current request. This will be the index of the next followee after the
//     * specified 'lastFollowee'.
//     *
//     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
//     *                          request or null if there was no previous request.
//     * @param allFollowees the generated list of followees from which we are returning paged results.
//     * @return the index of the first followee to be returned.
//     */
//    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {
//
//        int followeesIndex = 0;
//
//        if(lastFolloweeAlias != null) {
//            // This is a paged request for something after the first page. Find the first item
//            // we should return
//            for (int i = 0; i < allFollowees.size(); i++) {
//                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
//                    // We found the index of the last item returned last time. Increment to get
//                    // to the first one we should return
//                    followeesIndex = i + 1;
//                    break;
//                }
//            }
//        }
//
//        return followeesIndex;
//    }
//
//
//
//    /**
//     * Returns the list of dummy followee data. This is written as a separate method to allow
//     * mocking of the followees.
//     *
//     * @return the followees.
//     */
//    List<User> getDummyFollowees() {
//        return getFakeData().getFakeUsers();
//    }
//
//    private List<User> getDummyFollowers() {
//        return getFakeData().getFakeUsers();
//    }
//
//    /**
//     * Returns the {@link FakeData} object used to generate dummy followees.
//     * This is written as a separate method to allow mocking of the {@link FakeData}.
//     *
//     * @return a {@link FakeData} instance.
//     */
//    FakeData getFakeData() {
//        return new FakeData();
//    }
//}