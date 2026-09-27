package org.pog.custom;

import java.util.ArrayList;
import java.util.List;

public class PdfCell {
    private final List<PdfElement> elements =
            new ArrayList<>();

    private PdfCellStyle style;

    private float x;
    private float y;
    private float width;
    private float height;

    private int rowSpan = 1;
    private int colSpan = 1;

    public PdfCell rowSpan(int rowSpan) {

        if (rowSpan < 1) {
            throw new IllegalArgumentException(
                    "rowSpan phải >= 1"
            );
        }

        this.rowSpan = rowSpan;

        return this;
    }

    public PdfCell colSpan(int colSpan) {

        if (colSpan < 1) {
            throw new IllegalArgumentException(
                    "colSpan phải >= 1"
            );
        }

        this.colSpan = colSpan;

        return this;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    public PdfCell(PdfCellStyle style) {
        this.style = style;
    }

    // =====================================================
    // ELEMENT
    // =====================================================

    public PdfCell addElement(
            PdfElement element
    ) {
        if (element != null) {
            elements.add(element);
        }

        return this;
    }

    public List<PdfElement> getElements() {
        return elements;
    }

    // =====================================================
    // STYLE
    // =====================================================

    public PdfCellStyle getStyle() {
        return style;
    }

    public void setStyle(PdfCellStyle style) {
        this.style = style;
    }

    // =====================================================
    // BOUNDS
    // =====================================================

    public void setBounds(
            float x,
            float y,
            float width,
            float height
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
