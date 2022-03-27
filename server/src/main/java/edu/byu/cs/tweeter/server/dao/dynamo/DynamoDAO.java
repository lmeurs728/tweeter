package edu.byu.cs.tweeter.server.dao.dynamo;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public class DynamoDAO {
    protected static Table buildTable(String tableName) {
        return getDynamoDB().getTable(tableName);
    }

    protected static DynamoDB getDynamoDB() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-1")
                .build();

        return new DynamoDB(client);
    }


}
