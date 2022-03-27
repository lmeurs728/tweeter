package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.*;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.model.net.response.*;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

import java.util.*;

import static java.util.Objects.isNull;

public class FollowDAODynamo extends DynamoDAO implements FollowDAO  {
    @Override
    public int getFolloweeCount(String follower) {
        Table table = buildTable("user");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", follower);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            return Integer.parseInt(outcome.getMap("info").get("followee_count").toString());
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + follower);
            System.err.println(e.getMessage());
            return 0;
        }
    }

    @Override
    public int getFollowerCount(String followee) {
        Table table = buildTable("user");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", followee);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            return Integer.parseInt(outcome.getMap("info").get("follower_count").toString());
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + followee);
            System.err.println(e.getMessage());
            return 0;
        }
    }

    @Override
    public FollowingResponse getFollowees(FollowingRequest request) {
        Table table = buildTable("follows");
        String follower = request.getFollowerAlias();
        int maxResultSize = request.getLimit();
        System.out.println(request.getLastFolloweeAlias());
        PrimaryKey startFollower = new PrimaryKey(new KeyAttribute("follower_handle", follower), new KeyAttribute("followee_handle", request.getLastFolloweeAlias()));

        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#e", "follower_handle");

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":followee", follower);

        QuerySpec querySpec = request.isFirstTime()
                ? new QuerySpec().withKeyConditionExpression("#e = :followee").withNameMap(nameMap)
                .withValueMap(valueMap).withMaxResultSize(maxResultSize).withScanIndexForward(true)
                : new QuerySpec().withKeyConditionExpression("#e = :followee").withNameMap(nameMap)
                .withValueMap(valueMap).withMaxResultSize(maxResultSize).withExclusiveStartKey(startFollower).withScanIndexForward(true);

        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        try {
            System.out.println("Users following " + follower);
            items = table.query(querySpec);

            iterator = items.iterator();
            List<User> followees = new ArrayList<>();
            while (iterator.hasNext()) {
                item = iterator.next();
                GetUserRequest req = new GetUserRequest(item.getString("followee_handle"));
                UserResponse resp = new UserDAODynamo().getUser(req);
                if (resp.isSuccess()) {
                    User user = resp.getUser();
                    followees.add(user);
                }
                else {
                    throw new Exception("User was not found when getting follower");
                }
            }

            Map<String, AttributeValue> lastFollowee = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
            if (lastFollowee != null) {
                return new FollowingResponse(followees, true, lastFollowee.get("followee_handle").getS());
            }
            return new FollowingResponse(followees, false, null);
        }
        catch (Exception e) {
            System.err.println("Unable to get users following " + follower);
            System.err.println(e.getMessage());
            return new FollowingResponse(e.getMessage());
        }
    }

    public ArrayList<String> getFollowersForFeedGeneration(String follower) {
        Table table = buildTable("follows");

        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#e", "followee_handle");

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":followee", follower);

        Index tableIndex = table.getIndex("follows_index");
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#e = :followee").withNameMap(nameMap)
                .withValueMap(valueMap).withScanIndexForward(true);

        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        try {
            System.out.println("Users following " + follower);
            items = tableIndex.query(querySpec);

            iterator = items.iterator();
            ArrayList<String> followers = new ArrayList<>();
            while (iterator.hasNext()) {
                item = iterator.next();
                followers.add(item.getString("follower_handle"));
            }

            return followers;
        }
        catch (Exception e) {
            System.err.println("Unable to get users following " + follower);
            System.err.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public FollowersResponse getFollowers(FollowersRequest request) {
        Table table = buildTable("follows");
        String followee = request.getFolloweeAlias();
        int maxResultSize = request.getLimit();
        PrimaryKey startFollower = new PrimaryKey(new KeyAttribute("follower_handle", request.getLastFollowerAlias()), new KeyAttribute("followee_handle", followee));
        Index tableIndex = table.getIndex("follows_index");

        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#e", "followee_handle");

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":followee", followee);

        QuerySpec querySpec = request.isFirstTime()
                ? new QuerySpec().withKeyConditionExpression("#e = :followee").withNameMap(nameMap)
                .withValueMap(valueMap).withMaxResultSize(maxResultSize).withScanIndexForward(true)
                : new QuerySpec().withKeyConditionExpression("#e = :followee").withNameMap(nameMap)
                .withValueMap(valueMap).withMaxResultSize(maxResultSize).withExclusiveStartKey(startFollower).withScanIndexForward(true);

        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        try {
            System.out.println("Users following " + followee);
            items = tableIndex.query(querySpec);

            iterator = items.iterator();
            List<User> followers = new ArrayList<>();
            while (iterator.hasNext()) {
                item = iterator.next();
                GetUserRequest req = new GetUserRequest(item.getString("follower_handle"));
                User user = new UserDAODynamo().getUser(req).getUser();
                followers.add(user);
            }

            Map<String, AttributeValue> lastFollower = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
            if (lastFollower != null) {
                return new FollowersResponse(followers, true);
            }
            return new FollowersResponse(followers, false);
        }
        catch (Exception e) {
            System.err.println("Unable to get users following " + followee);
            System.err.println(e.getMessage());
            return new FollowersResponse(e.getMessage());
        }
    }

    @Override
    // Add entry to "follows" database
    // TODO: handle errors
    public void Follow(FollowRequest request) {
        Table table = buildTable("follows");
        String followerHandle = request.getFollowerAlias();
        String followeeHandle = request.getFolloweeAlias();

        try {
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("follower_handle", followerHandle,
                                    "followee_handle", followeeHandle));
            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult() + "\n" + followeeHandle);
        }
        catch (Exception e) {
            System.err.println("Unable to add item: " + followerHandle + " -> " + followeeHandle);
            System.err.println(e.getMessage());
        }

        table = buildTable("user");
        updateFolloweeCount(table, followerHandle);
        updateFollowerCount(table, followeeHandle);
    }

    public void updateFolloweeCount(Table table, String alias) {
        int count = getFolloweeCount("alias");
        count++;

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("alias", alias)
                .withUpdateExpression("set info.followee_count = :r")
                .withValueMap(new ValueMap().withInt(":r", count))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            System.out.println("Updating the item...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());
        }
        catch (Exception e) {
            System.err.println("Unable to update item: " + alias);
            System.err.println(e.getMessage());
        }
    }

    public void updateFollowerCount(Table table, String alias) {
        int count = getFollowerCount("alias");
        count++;

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("alias", alias)
                .withUpdateExpression("set info.follower_count = :r")
                .withValueMap(new ValueMap().withInt(":r", count))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            System.out.println("Updating the item...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());
        }
        catch (Exception e) {
            System.err.println("Unable to update item: " + alias);
            System.err.println(e.getMessage());
        }
    }

    @Override
    // Remove entry from "follows" database
    // TODO: handle errors
    public void Unfollow(UnfollowRequest request) {
        Table table = buildTable("follows");
        String follower = request.getFollowerAlias();
        String followee = request.getFolloweeAlias();

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("follower_handle", follower,
                        "followee_handle", followee));
        try {
            System.out.println("Attempting to delete...");
            table.deleteItem(deleteItemSpec);
            System.out.println("DeleteItem succeeded");
        }
        catch (Exception e) {
            System.err.println("Unable to delete item: " + follower + " " + followee);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public IsFollowerResponse IsFollower(IsFollowerRequest request) {
        Table table = buildTable("follows");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("follower_handle", request.getFollowerAlias(),
                "followee_handle", request.getFolloweeAlias());
        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            return new IsFollowerResponse(!isNull(outcome));
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + request.getFollowerAlias() + " -> " + request.getFolloweeAlias());
            System.err.println(e.getMessage());
        }
        return new IsFollowerResponse("Error");
    }
}
