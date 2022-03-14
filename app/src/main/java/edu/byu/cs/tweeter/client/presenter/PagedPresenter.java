package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.PagedService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleItemObserver;
import edu.byu.cs.tweeter.client.presenter.View.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends DescriptionPresenter<PagedView<T>> {
    private static final int PAGE_SIZE = 10;
    T lastItem;
    boolean hasMorePages;
    boolean isLoading;
    PagedService pagedService;

    public PagedPresenter(PagedView<T> view) {
        super(view);
        pagedService = new PagedService();
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingStatus(true);

            getItems(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem);
        }
    }

    public void getUser(String userAlias) {
        pagedService.getUser(userAlias, new GetUserObserver());
    }

    abstract void getItems(AuthToken authToken, User targetUser, int pageSize, T lastItem);

    public class GetUserObserver implements SimpleItemObserver<User> {

        @Override
        public void handleSuccess(User user) {
            view.navigateToUser(user);
        }

        @Override
        public void handleError(String message) {
            view.displayErrorMessage("Failed to login" + message);
        }
    }

    public class GetItemsObserver implements PagedObserver<T> {

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingStatus(false);

            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addItems(items);
        }

        @Override
        public void handleError(String message) {
            isLoading = false;
            view.setLoadingStatus(false);
            view.displayErrorMessage(getDescription() + message);
        }
    }

}
