package org.pog.custom;

public class PdfConfig {
    public enum PageSize {
        A4,
        A5,
        LETTER
    }

    public enum Orientation {
        PORTRAIT,
        LANDSCAPE
    }

    private final PageSize pageSize;
    private final Orientation orientation;

    private final float marginTop;
    private final float marginRight;
    private final float marginBottom;
    private final float marginLeft;

    private final PdfFont defaultFont;
    private final float defaultFontSize;

    private final float lineSpacing;

    private PdfConfig(Builder builder) {
        this.pageSize = builder.pageSize;
        this.orientation = builder.orientation;

        this.marginTop = builder.marginTop;
        this.marginRight = builder.marginRight;
        this.marginBottom = builder.marginBottom;
        this.marginLeft = builder.marginLeft;

        this.defaultFont = builder.defaultFont;
        this.defaultFontSize = builder.defaultFontSize;

        this.lineSpacing = builder.lineSpacing;
    }

    public static Builder builder() {
        return new Builder();
    }

    public PageSize getPageSize() {
        return pageSize;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public PdfFont getDefaultFont() {
        return defaultFont;
    }

    public float getDefaultFontSize() {
        return defaultFontSize;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public static final class Builder {

        private PageSize pageSize = PageSize.A4;
        private Orientation orientation = Orientation.PORTRAIT;

        private float marginTop = 40;
        private float marginRight = 40;
        private float marginBottom = 40;
        private float marginLeft = 40;

        private PdfFont defaultFont = PdfFont.TIMES_NEW_ROMAN;
        private float defaultFontSize = 10;

        private float lineSpacing = 1.2f;

        public Builder pageSize(PageSize pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder orientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder margin(float margin) {
            this.marginTop = margin;
            this.marginRight = margin;
            this.marginBottom = margin;
            this.marginLeft = margin;
            return this;
        }

        public Builder margins(
                float top,
                float right,
                float bottom,
                float left
        ) {
            this.marginTop = top;
            this.marginRight = right;
            this.marginBottom = bottom;
            this.marginLeft = left;
            return this;
        }

        public Builder defaultFont(PdfFont font) {
            this.defaultFont = font;
            return this;
        }

        public Builder defaultFontSize(float size) {
            this.defaultFontSize = size;
            return this;
        }

        public Builder lineSpacing(float lineSpacing) {
            this.lineSpacing = lineSpacing;
            return this;
        }

        public PdfConfig build() {
            return new PdfConfig(this);
        }
    }
}
