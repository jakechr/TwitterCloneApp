package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

public interface IUserDAO {

    public User login(LoginRequest request);

    public User register(RegisterRequest request);

    public User getUser(String userAlias);

    GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request);

    GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request);

    boolean incrementDecrementFollowCount(String userAlias, boolean increment, String attributeToChange);

    boolean checkIfUserInDB(String userAlias);

    void addUserBatch(List<User> users);
}
