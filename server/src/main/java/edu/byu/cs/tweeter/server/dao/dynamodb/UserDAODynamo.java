package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public class UserDAODynamo extends BaseDAODynamo implements IUserDAO {
    private final String tableName = "User";
    Table table = dynamoDB.getTable(tableName);
    private final static Logger logger = Logger.getLogger(UserDAODynamo.class.toString());

    @Override
    public User login(LoginRequest request) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", request.getUsername());
            Item userItem = table.getItem(itemToGet);
            String databasePassword = (String) userItem.get("password");
            String databaseSalt = (String) userItem.get("salt");
            String firstName = (String) userItem.get("first_name");
            String lastName = (String) userItem.get("last_name");
            String imageURL = (String) userItem.get("image_url");

            // Given at login
            String suppliedPassword = request.getPassword();
            String regeneratedPasswordToVerify = getSecurePassword(suppliedPassword, databaseSalt);

            if (databasePassword.equals(regeneratedPasswordToVerify)) {

                return new User(firstName, lastName, request.getUsername(), imageURL);
            }
            else {
                throw new RuntimeException("[BadRequest] Invalid Credentials");
            }
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to get user");
        }
    }

    @Override
    public User register(RegisterRequest request) {
        // Given at registration
        String password = request.getPassword();

        // Store this in the database
        String salt = getSalt();

        // Store this in the database
        String securePassword = getSecurePassword(password, salt);

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("user_alias", request.getUsername())
                            .withString("password", securePassword).withString("salt", salt)
                            .withString("first_name", request.getFirstName()).withString("last_name", request.getLastName())
                            .withString("image_url", request.getImage()).withInt("followers_count", 0)
                            .withInt("following_count", 0));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult().toString());

            return new User(request.getFirstName(), request.getLastName(), request.getUsername(), request.getImage());

        } catch (Exception e) {
            System.err.println("Unable to add item: " + request.getUsername());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] register failed");
        }
    }

    public User getUser(String userAlias) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", userAlias);
            Item userItem = table.getItem(itemToGet);
            String firstName = (String) userItem.get("first_name");
            String lastName = (String) userItem.get("last_name");
            String imageURL = (String) userItem.get("image_url");

            return new User(firstName, lastName, userAlias, imageURL);
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to get user");
        }
    }

    public boolean checkIfUserInDB(String userAlias) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", userAlias);
            Item userItem = table.getItem(itemToGet);

            return userItem != null;
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to check if alias in use");
        }
    }


    @Override
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", request.getTargetUser().getAlias());
            Item userItem = table.getItem(itemToGet);
            int followersCount = userItem.getInt("followers_count");

            return new GetFollowersCountResponse(followersCount);
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to get followers count");
        }
    }

    @Override
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", request.getTargetUser().getAlias());
            Item userItem = table.getItem(itemToGet);
            int followingCount = userItem.getInt("following_count");

            return new GetFollowingCountResponse(followingCount);
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to get following count");
        }
    }

    @Override
    public boolean incrementDecrementFollowCount(String userAlias, boolean increment, String attributeToChange) {
        int followingCount;
        int change = increment ? 1 : -1;
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", userAlias);
            Item userItem = table.getItem(itemToGet);
            followingCount = userItem.getInt(attributeToChange);

            UpdateItemOutcome outcome = table.updateItem(new PrimaryKey("user_alias", userAlias),
                    new AttributeUpdate(attributeToChange).put(followingCount + change));

            System.out.println("UpdateItem succeeded:\n" + outcome.getUpdateItemResult().toString());
            return true;
        } catch (Exception e) {
            System.err.println("Unable to update item: " + userAlias + " with change: " + change + " for attribute: " + attributeToChange);
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void addUserBatch(List<User> users) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(tableName);

        // Add each user into the TableWriteItems object
        for (User user : users) {
            Item item = new Item().withPrimaryKey("user_alias", user.getAlias())
                    .withString("password", "password").withString("salt", getSalt())
                    .withString("first_name", user.getFirstName()).withString("last_name", user.getLastName())
                    .withString("image_url", user.getImageUrl()).withInt("followers_count", 0)
                    .withInt("following_count", 0);
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(tableName);
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        logger.info("Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            logger.info("Wrote more Users");
        }
    }

    private static String getSecurePassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH PASSWORD";
    }

    private static String getSalt() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return "FAILED TO GET SALT";
    }

}
