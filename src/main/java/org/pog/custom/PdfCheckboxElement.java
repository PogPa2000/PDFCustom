package org.pog.custom;

public class PdfCheckboxElement implements PdfElement {
    public enum Position {
        LEFT,
        RIGHT
    }

    private final String text;
    private final boolean checked;
    private final float size;
    private final Position position;

    public PdfCheckboxElement(
            String text,
            boolean checked,
            float size,
            Position position
    ) {
        this.text = text;
        this.checked = checked;
        this.size = size;
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public boolean isChecked() {
        return checked;
    }

    public float getSize() {
        return size;
    }

    public Position getPosition() {
        return position;
    }
}
