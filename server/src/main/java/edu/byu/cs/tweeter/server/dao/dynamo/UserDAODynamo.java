package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.PasswordAuthentication;
import edu.byu.cs.tweeter.server.dao.UserDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAODynamo extends DynamoDAO implements UserDAO {
    @Override
    public User login(LoginRequest request) throws Exception {
        Table table = buildTable("user");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", request.getUsername());

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome.toString());
            Map<String, Object> infoMap = outcome.getMap("info");
            String token = infoMap.get("password").toString();

            boolean correctCredentials = new PasswordAuthentication().authenticate(request.getPassword().toCharArray(), token);
            if (correctCredentials) {
                return new User(infoMap.get("first_name").toString(),
                        infoMap.get("last_name").toString(),
                        outcome.getString("alias"),
                        infoMap.get("image_url").toString());
            }
            else {
                throw new Exception("Invalid credentials");
            }
        }
        catch (Exception e) {
            System.err.println("Unable to read user: " + request.getUsername());
            System.err.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public User register(RegisterRequest request, String imageURL) {
        Table table = buildTable("user");

        String encryptedPassword = new PasswordAuthentication().hash(request.getPassword().toCharArray());

        final Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("first_name", request.getFirstName());
        infoMap.put("last_name", request.getLastName());
        infoMap.put("password", encryptedPassword);
        infoMap.put("image_url", imageURL);
        infoMap.put("follower_count", 0);
        infoMap.put("followee_count", 0);

        try {
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("alias", request.getUsername())
                            .withMap("info", infoMap));
            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult() + "\n" + request.getUsername());
            return new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageURL);
        }
        catch (Exception e) {
            System.err.println("Unable to add item: " + request.getUsername());
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public UserResponse getUser(GetUserRequest request) {
        Table table = buildTable("user");
        String alias = request.getAlias();

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", alias);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            Map<String, Object> infoMap = outcome.getMap("info");

            return new UserResponse(new User(infoMap.get("first_name").toString(),
                    infoMap.get("last_name").toString(),
                    outcome.getString("alias"),
                    infoMap.get("image_url").toString()));
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + alias);
            System.err.println(e.getMessage());
            return new UserResponse(e.getMessage());
        }
    }

    private static List<User> getManyUsers() {
        return null;
//        DynamoDB dynamoDB = getDynamoDB();
//
//        try {
//            TableKeysAndAttributes forumTableKeysAndAttributes = new TableKeysAndAttributes("user");
//            // Add a partition key
//            forumTableKeysAndAttributes.addHashOnlyPrimaryKeys("Name", "Amazon S3", "Amazon DynamoDB");
//
//            TableKeysAndAttributes threadTableKeysAndAttributes = new TableKeysAndAttributes(threadTableName);
//            // Add a partition key and a sort key
//            threadTableKeysAndAttributes.addHashAndRangePrimaryKeys("ForumName", "Subject", "Amazon DynamoDB",
//                    "DynamoDB Thread 1", "Amazon DynamoDB", "DynamoDB Thread 2", "Amazon S3", "S3 Thread 1");
//
//            System.out.println("Making the request.");
//
//            BatchGetItemOutcome outcome = dynamoDB.batchGetItem(forumTableKeysAndAttributes,
//                    threadTableKeysAndAttributes);
//
//            Map<String, KeysAndAttributes> unprocessed = null;
//
//            do {
//                for (String tableName : outcome.getTableItems().keySet()) {
//                    System.out.println("Items in table " + tableName);
//                    List<Item> items = outcome.getTableItems().get(tableName);
//                    for (Item item : items) {
//                        System.out.println(item.toJSONPretty());
//                    }
//                }
//
//                // Check for unprocessed keys which could happen if you exceed
//                // provisioned
//                // throughput or reach the limit on response size.
//                unprocessed = outcome.getUnprocessedKeys();
//
//                if (unprocessed.isEmpty()) {
//                    System.out.println("No unprocessed keys found");
//                }
//                else {
//                    System.out.println("Retrieving the unprocessed keys");
//                    outcome = dynamoDB.batchGetItemUnprocessed(unprocessed);
//                }
//
//            } while (!unprocessed.isEmpty());
//
//        }
//        catch (Exception e) {
//            System.err.println("Failed to retrieve items.");
//            System.err.println(e.getMessage());
//        }
    }
}
