package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

import java.awt.*;
import java.util.*;
import java.util.List;

public class StatusDAODynamo extends DynamoDAO implements StatusDAO {
    @Override
    public FeedResponse getFeed(GetFeedRequest request) {
        Table table = buildTable("feed");
        String targetUser = request.getTargetUser();
        int maxResultSize = request.getLimit();
        PrimaryKey startStatus = request.getLastStatusTime() == 0 ? null : new PrimaryKey(new KeyAttribute("receiver_alias", targetUser),
                new KeyAttribute("time_stamp", request.getLastStatusTime()));

        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#e", "receiver_alias");

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":receiver", targetUser);

        QuerySpec querySpec = request.isFirstTime()
                ? new QuerySpec().withKeyConditionExpression("#e = :receiver").withNameMap(nameMap)
                .withValueMap(valueMap).withMaxResultSize(maxResultSize).withScanIndexForward(true)
                : new QuerySpec().withKeyConditionExpression("#e = :receiver").withNameMap(nameMap)
                .withValueMap(valueMap).withMaxResultSize(maxResultSize).withExclusiveStartKey(startStatus).withScanIndexForward(true);

        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        try {
            System.out.println("Feed for " + targetUser);
            items = table.query(querySpec);

            iterator = items.iterator();
            List<Status> statuses = new ArrayList<>();
            while (iterator.hasNext()) {
                item = iterator.next();
                Map<String, Object> infoMap = item.getMap("info");
                String post = infoMap.get("post").toString();
                GetUserRequest req = new GetUserRequest(infoMap.get("sender_alias").toString());
                User user = new UserDAODynamo().getUser(req).getUser();
                Status status = new Status(post, user, item.getNumber("time_stamp").toString(), parseURLs(post), parseMentions(post));
                statuses.add(status);
            }

            Map<String, AttributeValue> lastStatus = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
            if (lastStatus != null) {
                return new FeedResponse(statuses, true, new Date(lastStatus.get("time_stamp").getB().getLong()*1000L).toString());
            }
            return new FeedResponse(statuses, false, new Date(0).toString());
        }
        catch (Exception e) {
            System.err.println("Unable to get feed for " + targetUser);
            System.err.println(e.getMessage());
        }
        return null;
    }

    private List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    private List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    private int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    @Override
    public StoryResponse getStory(GetStoryRequest request) {
        Table table = buildTable("story");
        String targetUser = request.getTargetUser();
        int maxResultSize = request.getLimit();
        PrimaryKey startStatus = request.getLastStatusTime() == 0 ? null :  new PrimaryKey(new KeyAttribute("sender_alias", targetUser),
                new KeyAttribute("time_stamp", request.getLastStatusTime()));

        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#e", "sender_alias");

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":sender", targetUser);

        QuerySpec querySpec = request.isFirstTime()
                ? new QuerySpec().withKeyConditionExpression("#e = :sender").withNameMap(nameMap)
                .withValueMap(valueMap).withMaxResultSize(maxResultSize).withScanIndexForward(true)
                : new QuerySpec().withKeyConditionExpression("#e = :sender").withNameMap(nameMap)
                .withValueMap(valueMap).withMaxResultSize(maxResultSize).withExclusiveStartKey(startStatus).withScanIndexForward(true);

        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        try {
            System.out.println("Story for " + targetUser);
            items = table.query(querySpec);

            iterator = items.iterator();
            List<Status> statuses = new ArrayList<>();
            while (iterator.hasNext()) {
                item = iterator.next();
                String post = item.getMap("info").get("post").toString();
                GetUserRequest req = new GetUserRequest(item.getString("sender_alias"));
                User user = new UserDAODynamo().getUser(req).getUser();
                Status status = new Status(post, user, item.getNumber("time_stamp").toString(), parseURLs(post), parseMentions(post));
                statuses.add(status);
            }

            Map<String, AttributeValue> lastStatus = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
            if (lastStatus != null) {
                return new StoryResponse(statuses, true, new Date(lastStatus.get("time_stamp").getB().getLong()*1000L).toString());
            }
            return new StoryResponse(statuses, false, new Date(0).toString());
        }
        catch (Exception e) {
            System.err.println("Unable to get Story for " + targetUser);
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void PostStatus(PostStatusRequest request) {
        addTheStory(request.getStatus());
        addTheFeeds(request.getStatus());
    }

    private void addTheFeeds(Status status) {
        Table table = buildTable("feed");

        ArrayList<String> followers = new FollowDAODynamo().getFollowersForFeedGeneration(status.user.getAlias());
        long timeStamp = System.currentTimeMillis();

        for (String follower : followers) {
            final Map<String, Object> infoMap = new HashMap<>();
            infoMap.put("post", status.post);
            infoMap.put("sender_alias", status.user.getAlias());

            try {
                PutItemOutcome outcome = table
                        .putItem(new Item().withPrimaryKey("receiver_alias", follower,
                                        "time_stamp", timeStamp)
                                .withMap("info", infoMap));
                System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult() + "\n" + follower);
            } catch (Exception e) {
                System.err.println("Unable to add item: " + follower + "feed");
                System.err.println(e.getMessage());
            }
        }
    }

    private void addTheStory(Status status) {
        Table table = buildTable("story");

        final Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("post", status.post);

        long timeStamp = System.currentTimeMillis();
        try {
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("sender_alias", status.user.getAlias(),
                                    "time_stamp", timeStamp)
                            .withMap("info", infoMap));
            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult() + "\n" + "story");
        } catch (Exception e) {
            System.err.println("Unable to add item: " + "story");
            System.err.println(e.getMessage());
        }
    }
}
