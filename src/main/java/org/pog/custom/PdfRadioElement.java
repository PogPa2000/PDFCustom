package org.pog.custom;

public class PdfRadioElement implements PdfElement  {
    public enum Position {
        LEFT,
        RIGHT
    }

    private final String text;
    private final boolean selected;
    private final float radius;
    private final Position position;

    public PdfRadioElement(
            String text,
            boolean selected,
            float radius,
            Position position
    ) {
        this.text = text;
        this.selected = selected;
        this.radius = radius;
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public boolean isSelected() {
        return selected;
    }

    public float getRadius() {
        return radius;
    }

    public Position getPosition() {
        return position;
    }
}
