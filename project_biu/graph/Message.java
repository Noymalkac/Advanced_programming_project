/**
 * Message.java
 * This file is part of the project_biu graph representation.
 * It implements a Message class that encapsulates data for communication between agents.
 */
package graph;

import java.util.Date;
/**
 * Message.java
 * This file is part of the project_biu graph representation.
 * It implements a Message class that encapsulates data for communication between agents.
 */
public class Message {
    /**
     * The byte array representing the message content.
     * This is the raw data of the message.
     */
    public final byte[] data;
    /**
     * The text representation of the message.
     * This is derived from the byte array and can be used for display purposes.
     */
    public final String asText;
    /**
     * The double value of the message.
     * This is parsed from the text representation and can be used for numerical operations.
     */
    public final double asDouble;
    /**
     * The date when the message was created.
     * This is used to track the time of the message for logging or processing purposes.
     */
    public final Date date;
    /**
     * Constructs a Message with the given byte array.
     *
     * @param data The byte array representing the message content.
     */
    public Message(byte[] data) {
        this.data = data.clone();
        this.asText = new String(this.data);
        this.asDouble = parseDouble(this.asText);
        this.date = new Date();
    }
    /**
     * Constructs a Message with the given text.
     *
     * @param text The text content of the message.
     */
    public Message(String text) {
        this(text.getBytes());
    }
    /**
     * Constructs a Message with the given double value.
     *
     * @param val The double value to be encapsulated in the message.
     */
    public Message(double val) {
        this(Double.toString(val));
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
    /**
     * Gets the double value of the message.
     *
     * @return The double value, or NaN if the message does not represent a valid double.
     */
    public double getDouble() {
    return asDouble;
    }
    /**
     * Gets the date when the message was created.
     *
     * @return The date of the message.
     */
    public Date getDate() {
        return date;
    }
    /**
     * Gets the text representation of the message.
     *
     * @return The text content of the message.
     */
    public String getText() {
        return asText;
    }
    /**
     * Gets the byte array representation of the message.
     *
     * @return The byte array data of the message.
     */
    public byte[] getData() {
        return data.clone();
    }
    
}
