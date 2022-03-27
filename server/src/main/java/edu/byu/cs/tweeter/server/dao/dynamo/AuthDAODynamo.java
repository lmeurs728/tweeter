package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.PasswordAuthentication;
import org.joda.time.DateTime;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthDAODynamo extends DynamoDAO implements AuthDAO {

    // TODO error handling
    @Override
    public AuthToken createAuthToken(String alias) {
        String token = new ObjectIdGenerators.UUIDGenerator().generateId(null).toString();

        // put it in the db
        Table table = buildTable("auth");

        final Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("alias", alias);
        infoMap.put("time_stamp", System.currentTimeMillis());

        try {
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("token", token)
                            .withMap("info", infoMap));
            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult() + "\n" + alias);
        }
        catch (Exception e) {
            System.err.println("Unable to add auth token for: " + alias);
            System.err.println(e.getMessage());
        }
        return new AuthToken(token, alias);
    }

    @Override
    public void deleteAuthToken(AuthToken token) {
        Table table = buildTable("auth");

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("token", token.getToken()));
        try {
            System.out.println("Attempting to delete...");
            table.deleteItem(deleteItemSpec);
            System.out.println("DeleteItem succeeded");
        }
        catch (Exception e) {
            System.err.println("Unable to delete item: " + token.getToken());
            System.err.println(e.getMessage());
        }
    }

    @Override
    public boolean verifyAuthToken(AuthToken token) {
        Table table = buildTable("auth");
        long authTime = findTokenInTable(token, table);
        double tenHourMillis = 3.6e+7;
        if ((authTime - System.currentTimeMillis()) < tenHourMillis) {
            renewAuthTime(token, table);
            return true;
        }
        return false;
    }

    private long findTokenInTable(AuthToken token, Table table) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("token", token.getToken());
        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            if (outcome == null) {
                throw new Exception("authToken not found in db");
            }

            // we could check that the user matches here, but most of our requests don't pass the user
            Map<String, Object> infoMap = outcome.getMap("info");
            return Long.parseLong(infoMap.get("time_stamp").toString());
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + token.getToken());
            System.err.println(e.getMessage());
            long tenHourMillis = 36000000;
            return System.currentTimeMillis() + tenHourMillis;
        }
    }

    private void renewAuthTime(AuthToken token, Table table) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("token", token.getToken())
                .withUpdateExpression("set info.time_stamp = :r")
                .withValueMap(new ValueMap().withLong(":r", System.currentTimeMillis()))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            System.out.println("Updating the item...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());
        }
        catch (Exception e) {
            System.err.println("Unable to update item: " + token.getToken());
            System.err.println(e.getMessage());
        }
    }
}
