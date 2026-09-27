package org.pog.custom;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public enum PdfFont {

    TIMES_NEW_ROMAN(
            "Times New Roman",
            "D:/back_end_java/font/times.ttf"
    );

    private final String name;
    private final Path path;

    PdfFont(
            String name,
            String path
    ) {
        this.name = name;
        this.path = Path.of(path);
    }

    public String getName() {
        return name;
    }

    public String getPath() throws IOException {
        return path.toRealPath().toString();
    }

    public PDFont load(PDDocument document)
            throws IOException {

        return PDType0Font.load(
                document,
                path.toFile()
        );
    }

    public static PDFont load(PDDocument document, String path )
            throws IOException {

        return PDType0Font.load(
                document,
                new File(path)
        );
    }

    public Path getFontPath() {
        return path;
    }
}
