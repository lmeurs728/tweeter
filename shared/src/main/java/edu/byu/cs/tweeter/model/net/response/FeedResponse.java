package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.Status;

import java.util.List;
import java.util.Objects;

public class FeedResponse extends PagedResponse {
    private List<Status> statuses;
    private String lastStatus;

    public FeedResponse(String message) {
        super(false, message, false);
    }

    public FeedResponse(List<Status> statuses, boolean hasMorePages, String lastStatus) {
        super(true, hasMorePages);
        this.statuses = statuses;
        this.lastStatus = lastStatus;
    }


    public List<Status> getStatuses() {
        return statuses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedResponse that = (FeedResponse) o;
        return Objects.equals(statuses, that.statuses) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess();
    }

    @Override
    public int hashCode() {
        return Objects.hash(statuses);
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }
}
