package org.pog.custom;

public class PdfCellStyle {
    public enum HorizontalAlign {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalAlign {
        TOP,
        MIDDLE,
        BOTTOM
    }

    private final PdfFont font;
    private final Float fontSize;

    private final boolean bold;

    private final HorizontalAlign horizontalAlign;
    private final VerticalAlign verticalAlign;

    private final float paddingTop;
    private final float paddingRight;
    private final float paddingBottom;
    private final float paddingLeft;

    private final boolean borderTop;
    private final boolean borderRight;
    private final boolean borderBottom;
    private final boolean borderLeft;

    private PdfCellStyle(Builder builder) {
        this.font = builder.font;
        this.fontSize = builder.fontSize;

        this.bold = builder.bold;

        this.horizontalAlign = builder.horizontalAlign;
        this.verticalAlign = builder.verticalAlign;

        this.paddingTop = builder.paddingTop;
        this.paddingRight = builder.paddingRight;
        this.paddingBottom = builder.paddingBottom;
        this.paddingLeft = builder.paddingLeft;

        this.borderTop = builder.borderTop;
        this.borderRight = builder.borderRight;
        this.borderBottom = builder.borderBottom;
        this.borderLeft = builder.borderLeft;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PdfCellStyle defaultStyle() {
        return builder().build();
    }

    public static PdfCellStyle header() {
        return builder()
                .bold(true)
                .horizontalAlign(HorizontalAlign.CENTER)
                .verticalAlign(VerticalAlign.MIDDLE)
                .build();
    }

    public PdfFont getFont() {
        return font;
    }

    public Float getFontSize() {
        return fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public HorizontalAlign getHorizontalAlign() {
        return horizontalAlign;
    }

    public VerticalAlign getVerticalAlign() {
        return verticalAlign;
    }

    public float getPaddingTop() {
        return paddingTop;
    }

    public float getPaddingRight() {
        return paddingRight;
    }

    public float getPaddingBottom() {
        return paddingBottom;
    }

    public float getPaddingLeft() {
        return paddingLeft;
    }

    public boolean isBorderTop() {
        return borderTop;
    }

    public boolean isBorderRight() {
        return borderRight;
    }

    public boolean isBorderBottom() {
        return borderBottom;
    }

    public boolean isBorderLeft() {
        return borderLeft;
    }

    public static final class Builder {

        private PdfFont font;

        /*
         * null = sử dụng PdfConfig.defaultFontSize
         */
        private Float fontSize;

        private boolean bold = false;

        private HorizontalAlign horizontalAlign =
                HorizontalAlign.LEFT;

        private VerticalAlign verticalAlign =
                VerticalAlign.MIDDLE;

        private float paddingTop = 5;
        private float paddingRight = 5;
        private float paddingBottom = 5;
        private float paddingLeft = 5;

        private boolean borderTop = true;
        private boolean borderRight = true;
        private boolean borderBottom = true;
        private boolean borderLeft = true;

        public Builder font(PdfFont font) {
            this.font = font;
            return this;
        }

        public Builder fontSize(float fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public Builder bold(boolean bold) {
            this.bold = bold;
            return this;
        }

        public Builder horizontalAlign(HorizontalAlign align) {
            this.horizontalAlign = align;
            return this;
        }

        public Builder verticalAlign(VerticalAlign align) {
            this.verticalAlign = align;
            return this;
        }

        public Builder padding(float padding) {
            this.paddingTop = padding;
            this.paddingRight = padding;
            this.paddingBottom = padding;
            this.paddingLeft = padding;
            return this;
        }

        public Builder border(boolean value) {
            this.borderTop = value;
            this.borderRight = value;
            this.borderBottom = value;
            this.borderLeft = value;
            return this;
        }

        public Builder borderTop(boolean value) {
            this.borderTop = value;
            return this;
        }

        public Builder borderRight(boolean value) {
            this.borderRight = value;
            return this;
        }

        public Builder borderBottom(boolean value) {
            this.borderBottom = value;
            return this;
        }

        public Builder borderLeft(boolean value) {
            this.borderLeft = value;
            return this;
        }

        public PdfCellStyle build() {
            return new PdfCellStyle(this);
        }
    }
}
