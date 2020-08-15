package base.custom.logic;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static base.custom.logic.Helper.*;
import static org.testng.Assert.assertEquals;

public class SerializerTest {

    private Serializer serializer;

    @BeforeTest
    void init() {
        serializer = new Serializer();
    }

    @Test
    void serialize_simple_object_successfully() {
        assertEquals(serializer.serialize(SIMPLE_OBJECT), SERIALIZED_SIMPLE_OBJECT);
    }

    @Test
    void serialize_complex_object_successfully() {
        assertEquals(serializer.serialize(COMPLEX_OBJECT), SERIALIZED_COMPLEX_OBJECT);
    }

    @Test
    void serialize_without_synchronization_check_successfully() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Callable callable = () -> {
            Serializer serializer = new Serializer();
            return serializer.serialize(COMPLEX_OBJECT);
        };

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