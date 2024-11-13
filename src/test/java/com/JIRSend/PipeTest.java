package com.JIRSend;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.JIRSend.controller.InfinitePipeRecursion;
import com.JIRSend.controller.Pipe;
import com.JIRSend.controller.Subscription;

public class PipeTest {
    static Integer valueToBeChanged = 0;

    @Test
    void pipeCreation()
    {
        String pipeName = "testPipe";
        Pipe<Integer> pipe = new Pipe<>(pipeName);
        assertEquals(pipeName, pipe.getName());
    }

    @Test
    void pipeSendAndReceive()
    {
        String pipeName = "testPipe";
        Pipe<Integer> pipe = new Pipe<>(pipeName);
        valueToBeChanged = 0;
        
        pipe.subscribe((newValue) -> {valueToBeChanged = newValue;});
        assertEquals(0, valueToBeChanged);
        pipe.safePut(1);
        assertEquals(1, valueToBeChanged);
    }

    @Test
    void pipeSendDoesntThrows()
    {
        String pipeName = "testPipe";
        Pipe<Integer> pipe = new Pipe<>(pipeName);
        valueToBeChanged = 0;
        
        pipe.subscribe((newValue) -> {valueToBeChanged = newValue;});
        assertEquals(0, valueToBeChanged);
        assertDoesNotThrow(() -> pipe.safePut(1));
        assertEquals(1, valueToBeChanged);
    }

    @Test
    void pipeSendThrows()
    {
        String pipeName = "testPipe";
        Pipe<Integer> pipe1 = new Pipe<>(pipeName);
        Pipe<Integer> pipe2 = new Pipe<>(pipeName);
        valueToBeChanged = 0;
        
        pipe1.subscribe((newValue) -> {pipe2.safePut(newValue);});
        pipe2.subscribe(new Subscription<Integer>() {

            @Override
            public void get(Integer newValue) {
                valueToBeChanged = newValue; 
                assertThrows(InfinitePipeRecursion.class, () -> pipe1.safePut(1));
            }
            
        });
        assertEquals(0, valueToBeChanged);
        assertThrows(AssertionError.class, () -> pipe1.safePut(1));
        assertEquals(1, valueToBeChanged);
    }
}
