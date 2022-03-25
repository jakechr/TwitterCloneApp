package edu.byu.cs.tweeter.server.dao.dynamodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowToggleRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowToggleResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.util.FakeData;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class FollowsDAODynamo extends BaseDAODynamo implements IFollowDAO {
    private final String tableName = "follows";
    Table table = dynamoDB.getTable(tableName);

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        String lastItemAlias = (String) request.getLastItem();
        List<User> following = new ArrayList<>();

        QuerySpec querySpec3 = new QuerySpec();

        if (lastItemAlias == null) {
            querySpec3.withHashKey(new KeyAttribute("follower_handle", request.getUserAlias()))
                    .withScanIndexForward(false).withMaxResultSize(request.getLimit());
        } else {
            querySpec3.withHashKey(new KeyAttribute("follower_handle", request.getUserAlias()))
                    .withScanIndexForward(false).withMaxResultSize(request.getLimit())
                    .withExclusiveStartKey(new PrimaryKey("follower_handle", request.getUserAlias(),
                            "followee_handle", lastItemAlias));
        }


        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            items = table.query(querySpec3);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                String firstName = item.getString("followee_first_name");
                String lastName = item.getString("followee_last_name");
                String handle = item.getString("followee_handle");
                String image = item.getString("followee_image");
                System.out.println(item.getString("followee_handle") + ": " + item.getString("followee_name"));

                following.add(new User(firstName, lastName, handle, image));
            }

            Map<String, AttributeValue> lastItem = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
            boolean hasMorePages = lastItem != null;

            return new FollowingResponse(following, hasMorePages);

        } catch (Exception e) {
            System.err.println("Unable to query followees of " + request.getUserAlias());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] get followees failed for " + request.getUserAlias());
        }

    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        String lastItemAlias = (String) request.getLastItem();
        List<User> followers = new ArrayList<>();

        QuerySpec querySpec3 = new QuerySpec();
        Index index = table.getIndex("followee_handle-follower_handle-index");

        if (lastItemAlias == null) {
            querySpec3.withHashKey(new KeyAttribute("followee_handle", request.getUserAlias()))
                    .withScanIndexForward(false).withMaxResultSize(request.getLimit());
        } else {
            querySpec3.withHashKey(new KeyAttribute("followee_handle", request.getUserAlias()))
                    .withScanIndexForward(false).withMaxResultSize(request.getLimit())
                    .withExclusiveStartKey(new PrimaryKey("follower_handle", lastItemAlias,
                            "followee_handle", request.getUserAlias()));
        }


        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            items = index.query(querySpec3);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                String firstName = item.getString("follower_first_name");
                String lastName = item.getString("follower_last_name");
                String handle = item.getString("follower_handle");
                String image = item.getString("follower_image");
                System.out.println(item.getString("follower_handle") + ": " + item.getString("follower_name"));

                followers.add(new User(firstName, lastName, handle, image));
            }

            Map<String, AttributeValue> lastItem = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
            boolean hasMorePages = lastItem != null;

            return new FollowersResponse(followers, hasMorePages);

        } catch (Exception e) {
            System.err.println("Unable to query followers of " + request.getUserAlias());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] get followers failed for " + request.getUserAlias());
        }


    }

    public List<String> getAllFollowers(User user) {
        QuerySpec querySpec = new QuerySpec();
        Index index = table.getIndex("followee_handle-follower_handle-index");

        querySpec.withHashKey(new KeyAttribute("followee_handle", user.getAlias()));

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        List<String> followerAliases = new ArrayList<>();

        try {
            System.out.println("All users following: " + user.getAlias());
            items = index.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                followerAliases.add(item.getString("follower_handle"));
            }
            return followerAliases;
        }
        catch (Exception e) {
            System.err.println("Unable to query followers of: " + user.getAlias());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] propagate status to followers failed, find followers failed");
        }
    }


    public FollowToggleResponse follow(FollowToggleRequest request, User currUser) {
        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("follower_handle", currUser.getAlias(), "followee_handle", request.getFollowee().getAlias())
                            .withString("follower_first_name", currUser.getFirstName())
                            .withString("follower_last_name", currUser.getLastName())
                            .withString("follower_image", currUser.getImageUrl())
                            .withString("followee_first_name", request.getFollowee().getFirstName())
                            .withString("followee_last_name", request.getFollowee().getLastName())
                            .withString("followee_image", request.getFollowee().getImageUrl()));


            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult().toString());
            return new FollowToggleResponse(true);
        } catch (Exception e) {
            System.err.println("Unable to add item: " + "{ Follower: " + currUser.getAlias() + " Followee: " + request.getFollowee().getAlias() + " }");
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] follow failed");
        }
    }

    public FollowToggleResponse unfollow(FollowToggleRequest request, User currUser) {
        try {
            System.out.println("Clearing out old followee");
            DeleteItemOutcome outcome = table
                    .deleteItem(new PrimaryKey("follower_handle", currUser.getAlias(), "followee_handle", request.getFollowee().getAlias()));

            System.out.println("DeleteItem succeeded:\n" + outcome.getDeleteItemResult().toString());
            return new FollowToggleResponse(true);
        } catch (Exception e) {
            System.err.println("Unable to delete item: " + "{ Follower: " + currUser.getAlias() + " Followee: " + request.getFollowee().getAlias() + " }");
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] unfollow failed");
        }
    }


    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        try {
            System.out.println("Checking if the user is a follower...");
            Item item = table
                    .getItem(new PrimaryKey("follower_handle", request.getFollower().getAlias(), "followee_handle", request.getFollowee().getAlias()));

            System.out.println("Check for follower succeeded:\n" + item.toString());
            return new IsFollowerResponse(true);
        } catch (Exception e) {
            System.err.println("No follow relationship found: " + "{ Follower: " + request.getFollower().getAlias() + " Followee: " + request.getFollowee().getAlias() + " }");
            System.err.println(e.getMessage());
            return new IsFollowerResponse(false);
        }
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getDummyFollowees() {
        return getFakeData().getFakeUsers();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}
