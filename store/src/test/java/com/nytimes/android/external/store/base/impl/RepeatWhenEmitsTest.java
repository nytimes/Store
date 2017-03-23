package com.nytimes.android.external.store.base.impl;

import org.junit.Test;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class RepeatWhenEmitsTest {

    @Test
    public void testTransformer() throws Exception {
        // prepare mock
        //noinspection unchecked
        Callable<String> mockCallable = (Callable<String>) mock(Callable.class);
        when(mockCallable.call()).thenReturn("value");

        // create an observable and apply the transformer to test
        PublishSubject<String> source = PublishSubject.create();
        TestObserver<String> testSubscriber = Observable.fromCallable(mockCallable)
                .compose(RepeatWhenEmits.<String>from(source))
                .test();

        for (int i = 1; i < 10; i++) {
            // check that the original observable was called i times and that i events arrived.
            verify(mockCallable, times(i)).call();
            testSubscriber.assertValueCount(i);

            source.onNext("event");
        }
    }
}
