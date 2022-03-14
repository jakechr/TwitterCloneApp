package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.presenter.View.BaseView;

public abstract class DescriptionPresenter<T extends BaseView> extends BasePresenter<T> {
    public DescriptionPresenter(T view) {
        super(view);
    }

    abstract String getDescription();
}
