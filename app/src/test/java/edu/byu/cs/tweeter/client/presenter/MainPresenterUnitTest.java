package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;

public class MainPresenterUnitTest {
    private MainActivityPresenter.View mockView;
    private StatusService mockStatusService;
    private Cache mockCache;
    private String postMessage = "Lance is great!";

    private MainActivityPresenter mainPresenterSpy;

    @Before
    public void setup() {
        // Create mocks
        mockView = Mockito.mock(MainActivityPresenter.View.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockCache = Mockito.mock(Cache.class);

        mainPresenterSpy = Mockito.spy(new MainActivityPresenter(mockView));
        Mockito.doReturn(mockStatusService).when(mainPresenterSpy).getStatusService();

        Cache.setInstance(mockCache);
    }

    private void testPostStatus(Answer<Void> answer, String msg) {
        Mockito.doAnswer(answer).when(mockStatusService).doPostStatusTask(Mockito.any());
        mainPresenterSpy.doPostStatusTask(postMessage);

        // Verify that it makes the toast
        Mockito.verify(mockView).displayPostToast();

        // Verify that the first argument has the same value as the post
        ArgumentCaptor<Status> argument = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockStatusService).doPostStatusTask(argument.capture());
        assertEquals(postMessage, argument.getValue().getPost());

        // Verify that it builds the currUser on the cache
        Mockito.verify(mockCache).getCurrUser();

        // Verify that it deletes the original toast
        Mockito.verify(mockView).cancelPostToast();

        // Verify that it displays the chosen message
        Mockito.verify(mockView).displayMessage(msg);
    }

    @Test
    public void testDoPostStatusTask_success() {
        String msg = "Successfully Posted!";
        Answer<Void> answer = invocation -> {
            mainPresenterSpy.handlePostStatusSuccess();
            return null;
        };
        testPostStatus(answer, msg);
    }
    @Test
    public void testDoPostStatusTask_failed() {
        String msg = "Failed";
        Answer<Void> answer = invocation -> {
            mainPresenterSpy.handlePostStatusFailure(msg);
            return null;
        };
        testPostStatus(answer, msg);
    }
    @Test
    public void testDoPostStatusTask_exception() {
        String msg = "Excepted";
        Answer<Void> answer = invocation -> {
            mainPresenterSpy.handlePostStatusFailure(msg);
            return null;
        };
        testPostStatus(answer, msg);
    }
}
