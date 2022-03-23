package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class StatusServiceTest {
    private User currentUser;
    private Status status;

    private StatusService statusServiceSpy;
    private StatusServiceObserver observer;

    private CountDownLatch countDownLatch;

    private class StatusServiceObserver implements StatusService.Observer {
        private boolean success;
        private String message;
        private List<Status> statuses;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void setLoading(boolean b) {
            // Do nothing
        }

        @Override
        public void addStatuses(List<Status> statuses, boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.statuses = statuses;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void sendMessage(String message) {
            this.success = false;
            this.message = message;
            this.statuses = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }
    }

    @Before
    public void setup() {
        currentUser = new User("FirstName", "LastName", null);
        status = new Status("myPost", currentUser, null, null, null);

        observer = new StatusServiceObserver();

        statusServiceSpy = Mockito.spy(new StatusService(observer));

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }


    /**
     * Verify that for successful requests, the {@link FollowService#getFollowees}
     * asynchronous method eventually returns the same result as the {@link ServerFacade}.
     */
    @Test
    public void testGetStory_validRequest_correctResponse() throws InterruptedException {
        statusServiceSpy.getStory(3, status, currentUser);
        awaitCountDownLatch();

        List<Status> expectedStatuses = new FakeData().getFakeStatuses().subList(0, 3);

        Assert.assertTrue(observer.isSuccess());
        Assert.assertNull(observer.getMessage());
        Assert.assertEquals(expectedStatuses, observer.getStatuses());
        Assert.assertTrue(observer.getHasMorePages());
        Assert.assertNull(observer.getException());
    }
}
