package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

public interface IUserDAO {

    public User login(LoginRequest request);

    public User register(RegisterRequest request);

    public GetUserResponse getUser(GetUserRequest request);

    public LogoutResponse logout(LogoutRequest request);

}
