package base.custom.logic;

import base.object.ComplexObject;
import base.object.SimpleObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static base.custom.logic.Helper.*;
import static org.testng.Assert.assertEquals;

public class DeserializerTest {

    private Deserializer deserializer;

    @BeforeTest
    void init() {
        deserializer = new Deserializer();
    }

    @Test
    void deserialize_simple_object_successfully() {
        SimpleObject actualObject = (SimpleObject) deserializer.deserialize(SERIALIZED_SIMPLE_OBJECT);
        assertEquals(actualObject, SIMPLE_OBJECT);
    }

    @Test
    void deserialize_complex_object_successfully() {
        ComplexObject actualObject = (ComplexObject) deserializer.deserialize(SERIALIZED_COMPLEX_OBJECT);
        assertEquals(actualObject, COMPLEX_OBJECT);
    }

    @Test
    void deserialize_without_synchronization_check_successfully() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Callable callable = () -> {
            Deserializer deserializer = new Deserializer();
            return deserializer.deserialize(SERIALIZED_COMPLEX_OBJECT);
        };

        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            Future future = executor.submit(callable);
            futures.add(future);
        }

        for (Future future : futures) {
            assertEquals(future.get(), COMPLEX_OBJECT);
        }
    }
}