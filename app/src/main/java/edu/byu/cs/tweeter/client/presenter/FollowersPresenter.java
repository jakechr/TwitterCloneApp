package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.presenter.View.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> {
    FollowService followService;


    public FollowersPresenter(PagedView<User> view) {
        super(view);
        this.followService = new FollowService();
    }

    @Override
    void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        followService.getFollowers(authToken, targetUser, pageSize, lastItem, new GetItemsObserver());
    }

    @Override
    String getDescription() {
        return "Failed to get followers";
    }
}
