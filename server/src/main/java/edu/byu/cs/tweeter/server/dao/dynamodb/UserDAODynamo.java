package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.DataAccessException;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class UserDAODynamo extends BaseDAODynamo implements IUserDAO {
    private final String tableName = "User";

    public AuthenticationResponse login(LoginRequest request) {
        Table table = dynamoDB.getTable(tableName);

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
                User user = new User(firstName, lastName, request.getUsername(), imageURL);
                //AuthToken authToken = getNewAuthToken();

                // TODO: need to make it just return the user and add authtoken logic

                return new AuthenticationResponse(user, null);
            }
            else {
                throw new RuntimeException();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] invalid credentials");
        }
    }

    public User register(RegisterRequest request) {
        // Given at registration
        String password = request.getPassword();

        // Store this in the database
        String salt = getSalt();

        // Store this in the database
        String securePassword = getSecurePassword(password, salt);

        Table table = dynamoDB.getTable(tableName);

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("user_alias", request.getUsername())
                            .withString("password", securePassword).withString("salt", salt)
                            .withString("first_name", request.getFirstName()).withString("last_name", request.getLastName())
                            .withString("image_url", request.getImage()).withInt("followers_count", 0)
                            .withInt("following_count", 0));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult().toString());
            User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), request.getImage());

            return user;

        } catch (Exception e) {
            System.err.println("Unable to add item: " + request.getUsername());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] register failed");
        }
    }

    public GetUserResponse getUser(GetUserRequest request) {
        return new GetUserResponse(getFakeData().findUserByAlias(request.getUserAlias()));
    }

    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse(true);
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
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
