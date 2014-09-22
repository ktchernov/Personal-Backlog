package nz.co.lazycoder.personalbacklog.io;

import android.test.InstrumentationTestCase;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ktchernov on 21/09/2014.
 */
public class AsyncSaveQueuerTest extends InstrumentationTestCase {

    /** This object will be notified when the listener is complete - caters for multi threading */
    private CountDownLatch listenerCoundownLatch;
    private AsyncSaveQueuer.SaveListener saveListener;
    private StringSaver mockStringSaver;

    private final static String TEST_STRING = "Test string to save, test string to save, test string to save";

    public void setUp() {
        saveListener = mock(AsyncSaveQueuer.SaveListener.class);
        mockStringSaver = mock(StringSaver.class);

        setUpListenerCountdownLatch();
    }

    private void setUpListenerCountdownLatch() {
        listenerCoundownLatch = new CountDownLatch(1);

        doAnswer(listenerCountdownAnswer).when(saveListener).onSaveComplete(anyBoolean());
    }


    private AsyncSaveQueuer createSuccessSaveOperationsQueue() {
        when(mockStringSaver.saveString(anyString())).thenReturn(true);

        return new AsyncSaveQueuer(mockStringSaver);
    }

    private AsyncSaveQueuer createFailingSaveOperationsQueue() {
        when(mockStringSaver.saveString(anyString())).thenReturn(false);

        return new AsyncSaveQueuer(mock(StringSaver.class));
    }

    private AsyncSaveQueuer createSlowSaveOperationsQueue() {
        doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(200);
                return Boolean.TRUE;
            }
        }).when(mockStringSaver).saveString(anyString());

        return new AsyncSaveQueuer(mock(StringSaver.class));
    }


    private final Answer listenerCountdownAnswer = new Answer<Void>() {
        @Override
        public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
            listenerCoundownLatch.countDown();
            return null;
        }
    };

    public void testSaveSuccessful() throws InterruptedException {
        AsyncSaveQueuer asyncSaveQueuer = createSuccessSaveOperationsQueue();

        asyncSaveQueuer.queueSave(TEST_STRING, saveListener);

        waitForListenerToBeNotified();

        verify(saveListener).onSaveComplete(true);
    }

    public void testSaveFailed() throws Exception {
        AsyncSaveQueuer asyncSaveQueuer = createFailingSaveOperationsQueue();

        asyncSaveQueuer.queueSave(TEST_STRING, saveListener);

        waitForListenerToBeNotified();

        verify(saveListener).onSaveComplete(false);
    }

    public void testSlowSaveWithConsecutiveCallsShouldSaveMultipleTimes() throws InterruptedException {
        AsyncSaveQueuer asyncSaveQueuer = createSlowSaveOperationsQueue();

        final int NUM_SAVES = 3;
        listenerCoundownLatch = new CountDownLatch(NUM_SAVES);
        doAnswer(listenerCountdownAnswer).when(saveListener).onSaveComplete(anyBoolean());

        asyncSaveQueuer.queueSave(TEST_STRING, saveListener);
        asyncSaveQueuer.queueSave(TEST_STRING, saveListener);
        asyncSaveQueuer.queueSave(TEST_STRING, saveListener);

        waitForListenerToBeNotified();

        verify(saveListener, times(NUM_SAVES)).onSaveComplete(anyBoolean());
    }


    private void waitForListenerToBeNotified() throws InterruptedException {
        listenerCoundownLatch.await(1, TimeUnit.SECONDS);
    }
}
