package base.custom.logic;

import base.object.ComplexObject;
import base.object.SimpleObject;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static base.custom.logic.Deserializer.deserialize;
import static base.custom.logic.Helper.*;
import static org.testng.Assert.assertEquals;

public class DeserializerTest {

    @Test
    public void deserialize_simple_object_successfully() {
        SimpleObject actualObject = (SimpleObject) deserialize(SERIALIZED_SIMPLE_OBJECT);
        assertEquals(actualObject, SIMPLE_OBJECT);
    }

    @Test
    public void deserialize_complex_object_successfully() {
        ComplexObject actualObject = (ComplexObject) deserialize(SERIALIZED_COMPLEX_OBJECT);
        assertEquals(actualObject, COMPLEX_OBJECT);
    }

    @Test
    public void deserialize_synchronization_check_successfully() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Callable callable = () -> deserialize(SERIALIZED_COMPLEX_OBJECT);

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