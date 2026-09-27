package org.pog.custom;

public class PdfImageElement implements PdfElement  {
    private final String imagePath;
    private final float width;
    private final float height;

    public PdfImageElement(
            String imagePath,
            float width,
            float height
    ) {
        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
    }

    public String getImagePath() {
        return imagePath;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
