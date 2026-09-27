package org.pog.custom;

public class PdfTextElement implements PdfElement{
    private final String text;
    private final PdfCellStyle style;

    public PdfTextElement(
            String text,
            PdfCellStyle style
    ) {
        this.text = text;
        this.style = style;
    }

    public String getText() {
        return text;
    }

    public PdfCellStyle getStyle() {
        return style;
    }


}
