package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;

    public class FeedDAODynamo extends BaseDAODynamo implements IFeedDAO {
    private final String tableName = "Feed";
    Table table = dynamoDB.getTable(tableName);


    @Override
    public boolean addStatusToFeed(List<String> followerAliases, Status status) {
        for(String userAlias : followerAliases) {
            try {
                System.out.println("Adding a new status to feed...");
                PutItemOutcome outcome = table
                        .putItem(new Item().withPrimaryKey("user_alias", userAlias, "timestamp", status.getDate())
                                .withString("message", status.getPost()).withList("mentions", status.getMentions())
                                .withList("urls", status.getUrls()));

                System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult().toString());

            } catch (Exception e) {
                System.err.println("Unable to add item: " + status + " to feed for user: " + userAlias);
                System.err.println(e.getMessage());
                throw new RuntimeException("[DBError] postStatus failed when propagating to user feeds");
            }
        }

        return true;
    }
}
