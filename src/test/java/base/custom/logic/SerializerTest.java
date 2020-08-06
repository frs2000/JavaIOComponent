package base.custom.logic;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static base.custom.logic.Helper.*;
import static base.custom.logic.Serializer.serialize;
import static org.testng.Assert.assertEquals;

public class SerializerTest {

    @Test
    public void serialize_simple_object_successfully() {
        assertEquals(serialize(SIMPLE_OBJECT), SERIALIZED_SIMPLE_OBJECT);
    }

    @Test
    public void serialize_complex_object_successfully() {
        assertEquals(serialize(COMPLEX_OBJECT), SERIALIZED_COMPLEX_OBJECT);
    }

    @Test
    public void serialize_synchronization_check_successfully() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Callable callable = () -> serialize(COMPLEX_OBJECT);

        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            Future future = executor.submit(callable);
            futures.add(future);
        }

        for (int i = 0; i < futures.size(); i++) {
            assertEquals(futures.get(i).get(), SERIALIZED_COMPLEX_OBJECT, String.format("Iteration %s", i));
        }
    }
}