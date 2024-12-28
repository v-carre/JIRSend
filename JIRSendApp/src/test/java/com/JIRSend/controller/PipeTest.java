package com.JIRSend.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.JIRSendApp.controller.InfinitePipeRecursion;
import com.JIRSendApp.controller.Pipe;
import com.JIRSendApp.controller.Subscription;

public class PipeTest {
    static Integer valueToBeChanged = 0;

    @Test
    void pipeCreation() {
        String pipeName = "testPipe";
        Pipe<Integer> pipe = new Pipe<>(pipeName);
        assertEquals(pipeName, pipe.getName());
        assertEquals("[" + pipeName + "]", pipe.toString());
    }

    @Test
    void pipeSendAndReceive() {
        String pipeName = "testPipe";
        Pipe<Integer> pipe = new Pipe<>(pipeName);
        valueToBeChanged = 0;

        pipe.subscribe((newValue) -> {
            valueToBeChanged = newValue;
        });
        assertEquals(0, valueToBeChanged);
        pipe.safePut(1);
        assertEquals(1, valueToBeChanged);
    }

    @Test
    void pipeSendDoesntThrows() {
        String pipeName = "testPipe";
        Pipe<Integer> pipe = new Pipe<>(pipeName);
        valueToBeChanged = 0;

        pipe.subscribe((newValue) -> {
            valueToBeChanged = newValue;
        });
        assertEquals(0, valueToBeChanged);
        assertDoesNotThrow(() -> pipe.put(1));
        assertEquals(1, valueToBeChanged);
    }

    @Test
    void pipeSendThrows() {
        String pipeName = "testPipe";
        Pipe<Integer> pipe1 = new Pipe<>(pipeName);
        Pipe<Integer> pipe2 = new Pipe<>(pipeName);
        valueToBeChanged = 0;

        pipe1.subscribe((newValue) -> {
            pipe2.safePut(newValue);
        });
        pipe2.subscribe(new Subscription<Integer>() {

            @Override
            public void get(Integer newValue) {
                valueToBeChanged = newValue;
                assertThrows(InfinitePipeRecursion.class, () -> pipe1.put(1));
            }

        });
        assertEquals(0, valueToBeChanged);
        // assertThrows(AssertionError.class, () -> pipe1.put(1));
        pipe1.safePut(1);
        assertEquals(1, valueToBeChanged);
    }
}
