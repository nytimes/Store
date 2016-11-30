package com.nytimes.android.sample;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;
import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    public static final String KEY = "key";
    private final LinkedHashMap<String, AtomicReference<String>> cache =
            new LinkedHashMap<String, AtomicReference<String>>(){
        private static final int MAX_ENTRIES = 100;

        @Override
        protected boolean removeEldestEntry(Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    };
    private final AtomicInteger counter = new AtomicInteger(0);

    @Test
    public void addition_isCorrect() throws Exception {
        new Runnable() {
            @Override
            public void run() {
                try {
                    synchronizedGet(KEY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Runnable() {
            @Override
            public void run() {
                try {
                    synchronizedGet(KEY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Runnable() {
            @Override
            public void run() {
                try {
                    synchronizedGet(KEY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Runnable() {
            @Override
            public void run() {
                try {
                    synchronizedGet(KEY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Runnable() {
            @Override
            public void run() {
                try {
                    synchronizedGet(KEY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        assertEquals(synchronizedGet(KEY), "1");

    }

    private String synchronizedGet(String key) throws InterruptedException {
        AtomicReference<String> ref;
        synchronized (cache) {
            ref = cache.get(key);
            if (ref == null) {
                ref = new AtomicReference<>();
                cache.put(key, ref);
            }
        }
        synchronized (ref) {
            if(ref.get()==null){
                sleep(1000);
                counter.incrementAndGet();
                ref.set(counter.toString());
            }

        }
        return ref.get();
    }
}
