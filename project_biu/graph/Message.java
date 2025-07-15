package graph;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(byte[] data) {
        this.data = data.clone();
        this.asText = new String(this.data);
        this.asDouble = parseDouble(this.asText);
        this.date = new Date();
    }

    public Message(String text) {
        this(text.getBytes());
    }

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

    public double getDouble() {
    return asDouble;
    }

    public Date getDate() {
        return date;
    }

    public String getText() {
        return asText;
    }

    public byte[] getData() {
        return data.clone();
    }
    
}
