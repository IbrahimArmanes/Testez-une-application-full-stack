package com.openclassrooms.starterjwt.payload.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class MessageResponseTest {

    @Test
    void testMessageResponseConstructor() {
        String testMessage = "Test message";
        MessageResponse response = new MessageResponse(testMessage);
        assertEquals(testMessage, response.getMessage());
    }

    @Test
    void testSetMessage() {
        MessageResponse response = new MessageResponse("Initial message");
        String newMessage = "Updated message";
        response.setMessage(newMessage);
        assertEquals(newMessage, response.getMessage());
    }

    @Test
    void testGetMessage() {
        String testMessage = "Test message";
        MessageResponse response = new MessageResponse(testMessage);
        String retrievedMessage = response.getMessage();
        assertEquals(testMessage, retrievedMessage);
    }
}
