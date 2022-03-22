package edu.byu.cs.tweeter.server.dao.dynamodb;

import static edu.byu.cs.tweeter.server.dao.dynamodb.BaseDAODynamo.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;

public class AuthTokenDAODynamo implements IAuthTokenDAO {
    final String tableName = "AuthToken";
    Table table = dynamoDB.getTable(tableName);

    @Override
    public AuthToken generateAuthToken(User user) {
        AuthToken authToken = getNewAuthToken();

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("token", authToken.getToken())
                            .withString("timestamp", authToken.getDatetime()).withString("user_alias", user.getAlias()));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

            return authToken;

        } catch (Exception e) {
            System.err.println("Unable to add item: " + authToken.getToken());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] AuthToken generation failed");
        }
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        try {
            System.out.println("Clearing out old authToken");
            DeleteItemOutcome outcome = table
                    .deleteItem(new KeyAttribute("token", request.getAuthToken().getToken()));

            System.out.println("DeleteItem succeeded:\n" + outcome.getDeleteItemResult().toString());

            return new LogoutResponse(true);

        } catch (Exception e) {
            System.err.println("Unable to add item: " + request.getAuthToken().getToken());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] AuthToken generation failed");
        }
    }

    @Override
    public String getCurrUserAlias(AuthToken authToken) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("token", authToken.getToken());
            Item authTokenItem = table.getItem(itemToGet);

            return (String) authTokenItem.get("user_alias");
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to get current user for session");
        }
    }

    @Override
    public boolean authenticateCurrUserSession(AuthToken authToken) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("token", authToken.getToken());

            return true;
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to authenticate current user session");
        }
    }

    private static AuthToken getNewAuthToken() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            return new AuthToken(Base64.getEncoder().encodeToString(salt), dtf.format(now));
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }


}
