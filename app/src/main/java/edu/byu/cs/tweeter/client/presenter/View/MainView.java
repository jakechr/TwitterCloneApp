package edu.byu.cs.tweeter.client.presenter.View;

public interface MainView extends BaseView {
    void handleUnfollowSuccess();
    void handleFollowSuccess();
    void handleLogoutSuccess();
    void handleGetFollowersCountSuccess(int count);
    void handleGetFollowingCountSuccess(int count);
    void handleIsFollowerSuccess(boolean isFollower);
    void handlePostStatusSuccess();
    void resetFollowButton();
    void displayInfoMessage(String message);
    void clearInfoMessage();

}
