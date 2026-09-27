package org.pog.custom;

import java.util.ArrayList;
import java.util.List;

public class PdfTable {
    private final PdfDraw pdfDraw;

    private float width;

    private float[] columnWidths;

    private final java.util.List<PdfRow> rows =
            new java.util.ArrayList<>();

    private PdfRow currentRow;

    public PdfTable(PdfDraw pdfDraw) {
        this.pdfDraw = pdfDraw;
    }

    // =====================================================
    // TABLE CONFIG
    // =====================================================

    public PdfTable width(float width) {
        this.width = width;
        return this;
    }

    public PdfTable columns(float... widths) {
        this.columnWidths = widths;
        return this;
    }

    private float[] columnPercentages;

    public PdfTable columnsPercent(float... percentages) {

        if (percentages == null || percentages.length == 0) {
            throw new IllegalArgumentException(
                    "Column percentages must not be empty"
            );
        }

        float total = 0;

        for (float percentage : percentages) {

            if (percentage <= 0) {
                throw new IllegalArgumentException(
                        "Column percentage must be > 0"
                );
            }

            total += percentage;
        }

        if (Math.abs(total - 100f) > 0.01f) {
            throw new IllegalArgumentException(
                    "Column percentages must total 100%. Current = "
                            + total
            );
        }

        this.columnPercentages = percentages.clone();

        return this;
    }



    // =====================================================
    // ROW
    // =====================================================

    public PdfTable newRow() {

        currentRow = new PdfRow();

        rows.add(currentRow);

        return this;
    }

    // =====================================================
    // TEXT
    // =====================================================

    /**
     * Giữ nguyên API cũ.
     */
    public PdfTable addText(String text) {
        return addText(
                text,
                PdfCellStyle.defaultStyle()
        );
    }

    /**
     * Giữ nguyên API cũ.
     */
    public PdfTable addText(
            String text,
            PdfCellStyle style
    ) {

        ensureRow();

        PdfCell cell =
                new PdfCell(style);

        cell.addElement(
                new PdfTextElement(
                        text,
                        style
                )
        );

        currentRow.addCell(cell);

        return this;
    }

    // =====================================================
    // CHECKBOX
    // =====================================================

    public PdfTable addCheckbox(
            boolean checked
    ) {
        return addCheckbox(
                "",
                checked,
                12,
                PdfCheckboxElement.Position.LEFT
        );
    }

    public PdfTable addCheckbox(
            String text,
            boolean checked
    ) {
        return addCheckbox(
                text,
                checked,
                12,
                PdfCheckboxElement.Position.LEFT
        );
    }

    public PdfTable addCheckbox(
            String text,
            boolean checked,
            float size,
            PdfCheckboxElement.Position position
    ) {

        ensureRow();

        PdfCellStyle style =
                PdfCellStyle.defaultStyle();

        PdfCell cell =
                new PdfCell(style);

        cell.addElement(
                new PdfCheckboxElement(
                        text,
                        checked,
                        size,
                        position
                )
        );

        currentRow.addCell(cell);

        return this;
    }

    // =====================================================
    // RADIO
    // =====================================================

    public PdfTable addRadio(
            boolean selected
    ) {
        return addRadio(
                "",
                selected,
                6,
                PdfRadioElement.Position.LEFT
        );
    }

    public PdfTable addRadio(
            String text,
            boolean selected
    ) {
        return addRadio(
                text,
                selected,
                6,
                PdfRadioElement.Position.LEFT
        );
    }

    public PdfTable addRadio(
            String text,
            boolean selected,
            float radius,
            PdfRadioElement.Position position
    ) {

        ensureRow();

        PdfCellStyle style =
                PdfCellStyle.defaultStyle();

        PdfCell cell =
                new PdfCell(style);

        cell.addElement(
                new PdfRadioElement(
                        text,
                        selected,
                        radius,
                        position
                )
        );

        currentRow.addCell(cell);

        return this;
    }

    // =====================================================
    // IMAGE
    // =====================================================

    public PdfTable addImage(
            String imagePath,
            float width,
            float height
    ) {

        ensureRow();

        PdfCellStyle style =
                PdfCellStyle.defaultStyle();

        PdfCell cell =
                new PdfCell(style);

        cell.addElement(
                new PdfImageElement(
                        imagePath,
                        width,
                        height
                )
        );

        currentRow.addCell(cell);

        return this;
    }

    // =====================================================
    // NUMBER
    // =====================================================

    public PdfTable addNumber(
            int number,
            String text
    ) {

        ensureRow();

        PdfCellStyle style =
                PdfCellStyle.defaultStyle();

        PdfCell cell =
                new PdfCell(style);

        cell.addElement(
                new PdfNumberElement(
                        number,
                        text
                )
        );

        currentRow.addCell(cell);

        return this;
    }

    // =====================================================
    // INTERNAL
    // =====================================================

    private void ensureRow() {

        if (currentRow == null) {
            newRow();
        }
    }

    public PdfTable addCell(PdfCell cell) {

        if (currentRow == null) {
            newRow();
        }

        currentRow.addCell(cell);

        return this;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public float getWidth() {
        return width;
    }

    public float[] getColumnWidths() {
        return columnWidths;
    }

    public float[] getColumnPercentages() {
        return columnPercentages;
    }

    public java.util.List<PdfRow> getRows() {
        return rows;
    }

    public PdfDraw end() {
        return pdfDraw;
    }


}
