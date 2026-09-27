package org.pog.custom;

public class PdfNumberElement implements PdfElement  {
    private final int number;
    private final String text;

    public PdfNumberElement(
            int number,
            String text
    ) {
        this.number = number;
        this.text = text;
    }

    public int getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }
}
