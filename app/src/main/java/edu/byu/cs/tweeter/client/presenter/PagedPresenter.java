package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends BasePresenter {

    protected T lastItem;
    public boolean getHasMorePages() {
        return hasMorePages;
    }
    protected boolean hasMorePages = true;
    protected final int PAGE_SIZE = 10;
    public final PagedView<T> view;
    private boolean isLoading = false;
    protected User user;

    public void setUser(User user) {
        this.user = user;
    }

    public interface PagedView<U> extends BaseView {
        void setLoading(boolean value);
        void addItems(List<U> items);
    }

    public PagedPresenter(PagedView<T> view) {
        super(view);
        this.view = view;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            doServiceMethod();
        }
    }

    public abstract void doServiceMethod();

    @Override
    public void handleUserSuccess(User user) {
        setLoading(false);
        view.startActivity(user);
    }

    @Override
    public void sendMessage(String message) {
        setLoading(false);
        view.displayMessage(message);
    }

    protected void setLastItemAndHasMorePages(List<T> items, boolean hasMorePages) {
        lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
        this.hasMorePages = hasMorePages;
    }

    public void setLoading(boolean b) {
//        view.setLoading(b);
//        this.isLoading = b;
    }
}
