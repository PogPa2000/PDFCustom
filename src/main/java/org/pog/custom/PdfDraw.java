package org.pog.custom;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.Map;

public class PdfDraw implements AutoCloseable {

    // =========================================================
    // CONFIG
    // =========================================================

    private final PdfConfig config;

    // =========================================================
    // PDFBOX OBJECTS
    // =========================================================

    private final PDDocument document;

    private PDPage currentPage;

    private PDPageContentStream contentStream;

    // =========================================================
    // DRAWING STATE
    // =========================================================

    /**
     * Tọa độ X hiện tại.
     */
    private float currentX;

    /**
     * Tọa độ Y hiện tại.
     *
     * PDFBox có hệ tọa độ:
     *
     * (0,0)
     *   |
     *   |-------> X
     *   |
     *   v
     *   Y
     *
     * Tuy nhiên khi vẽ PDF thông thường ta bắt đầu từ
     * phía trên nên currentY được khởi tạo từ:
     *
     * pageHeight - marginTop
     */
    private float currentY;

    /**
     * Trang hiện tại.
     */
    private int currentPageNumber = 0;

    // =========================================================
    // FONT CACHE
    // =========================================================

    /**
     * PDFont phụ thuộc vào PDDocument.
     *
     * Vì vậy KHÔNG được để PDFont static dùng chung
     * giữa nhiều PdfDraw / nhiều thread.
     *
     * Mỗi PdfDraw có một fontCache riêng.
     */
    private final Map<PdfFont, PDFont> fontCache =
            new EnumMap<>(PdfFont.class);

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PdfDraw(PdfConfig config) throws IOException {

        if (config == null) {
            throw new IllegalArgumentException(
                    "PdfConfig không được null"
            );
        }

        this.config = config;
        this.document = new PDDocument();

        // Tạo trang đầu tiên
        addPage();
    }

    // =========================================================
    // PAGE
    // =========================================================

    /**
     * Tạo page mới.
     */
    public void addPage() throws IOException {

        // Đóng content stream của page trước
        closeContentStream();

        PDRectangle pageSize = getPageSize();

        currentPage = new PDPage(pageSize);

        document.addPage(currentPage);

        contentStream = new PDPageContentStream(
                document,
                currentPage
        );

        currentPageNumber++;

        // Reset vị trí vẽ
        currentX = config.getMarginLeft();

        currentY =
                pageSize.getHeight()
                        - config.getMarginTop();
    }

    /**
     * Alias cho addPage().
     */
    public void nextPage() throws IOException {
        addPage();
    }

    /**
     * Lấy số page hiện tại.
     */
    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    /**
     * Lấy kích thước page theo config.
     */
    private PDRectangle getPageSize() {

        PDRectangle rectangle;

        switch (config.getPageSize()) {

            case A4:
                rectangle = PDRectangle.A4;
                break;

            case A5:
                rectangle = PDRectangle.A5;
                break;

            case LETTER:
                rectangle = PDRectangle.LETTER;
                break;

            default:
                rectangle = PDRectangle.A4;
        }

        if (config.getOrientation()
                == PdfConfig.Orientation.LANDSCAPE) {

            return new PDRectangle(
                    rectangle.getHeight(),
                    rectangle.getWidth()
            );
        }

        return rectangle;
    }

    // =========================================================
    // FONT
    // =========================================================

    /**
     * Lấy PDFont từ cache.
     *
     * Nếu font chưa được load thì load vào document hiện tại.
     */
    private PDFont getFont(PdfFont font) throws IOException {

        if (font == null) {
            font = config.getDefaultFont();
        }

        PDFont cachedFont = fontCache.get(font);

        if (cachedFont != null) {
            return cachedFont;
        }

        PDFont loadedFont = font.load(document);

        fontCache.put(font, loadedFont);

        return loadedFont;
    }

    // =========================================================
    // TEXT
    // =========================================================

    /**
     * Vẽ text tại currentX/currentY
     * sử dụng font và font size mặc định.
     */
    public void text(String text) throws IOException {

        text(
                text,
                config.getDefaultFont(),
                config.getDefaultFontSize()
        );
    }

    /**
     * Vẽ text tại currentX/currentY.
     */
    public void text(
            String text,
            PdfFont font,
            float fontSize
    ) throws IOException {

        if (text == null) {
            return;
        }

        PDFont pdfFont = getFont(font);

        contentStream.beginText();

        contentStream.setFont(
                pdfFont,
                fontSize
        );

        contentStream.newLineAtOffset(
                currentX,
                currentY
        );

        contentStream.showText(text);

        contentStream.endText();

        // Sau khi vẽ text thì xuống dòng
        currentY -=
                fontSize
                        * config.getLineSpacing();
    }

    /**
     * Vẽ text tại tọa độ cụ thể.
     *
     * Không thay đổi currentX/currentY.
     */
    public void textAt(
            String text,
            float x,
            float y
    ) throws IOException {

        textAt(
                text,
                x,
                y,
                config.getDefaultFont(),
                config.getDefaultFontSize()
        );
    }

    /**
     * Vẽ text tại tọa độ cụ thể.
     */
    public void textAt(
            String text,
            float x,
            float y,
            PdfFont font,
            float fontSize
    ) throws IOException {

        if (text == null) {
            return;
        }

        PDFont pdfFont = getFont(font);

        contentStream.beginText();

        contentStream.setFont(
                pdfFont,
                fontSize
        );

        contentStream.newLineAtOffset(
                x,
                y
        );

        contentStream.showText(text);

        contentStream.endText();
    }

    /**
     * Vẽ text inline tại currentX/currentY.
     *
     * Ví dụ:
     *
     * Hợp đồng: [in đậm] ABC123 [/in đậm]
     *
     * Lưu ý:
     * Hàm này tự beginText/endText.
     */
    public void textInline(
            String text,
            PdfFont font,
            float fontSize
    ) throws IOException {

        if (text == null) {
            return;
        }

        PDFont pdfFont = getFont(font);

        contentStream.beginText();

        contentStream.setFont(
                pdfFont,
                fontSize
        );

        contentStream.newLineAtOffset(
                currentX,
                currentY
        );

        contentStream.showText(text);

        contentStream.endText();

        // Tăng X để lần vẽ tiếp theo nằm ngay bên phải
        float textWidth =
                pdfFont.getStringWidth(text)
                        / 1000
                        * fontSize;

        currentX += textWidth;
    }

    /**
     * Text inline sử dụng font mặc định.
     */
    public void textInline(String text) throws IOException {

        textInline(
                text,
                config.getDefaultFont(),
                config.getDefaultFontSize()
        );
    }

    /**
     * Xuống dòng.
     */
    public void newLine() {

        currentX = config.getMarginLeft();

        currentY -=
                config.getDefaultFontSize()
                        * config.getLineSpacing();
    }

    // =========================================================
    // TABLE
    // =========================================================

    /**
     * Tạo PdfTable.
     */
    public PdfTable table() {

        return new PdfTable(this);
    }

    /**
     * Vẽ table.
     *
     * Phiên bản hiện tại sử dụng rowHeight cố định.
     *
     * Sau này có thể mở rộng:
     *
     * - Auto row height
     * - Text wrapping
     * - Page break
     * - Repeat header
     * - Rowspan
     * - Colspan
     */
    public void drawTable(
            PdfTable table
    ) throws IOException {

        if (table == null) {
            throw new IllegalArgumentException(
                    "PdfTable không được null"
            );
        }

        float x = currentX;
        float y = currentY;

        // =========================================================
        // Lấy column width
        // =========================================================

        float[] columnWidths = null;

        // ---------------------------------------------------------
        // Trường hợp 1: columnsPercent()
        // ---------------------------------------------------------

        if (table.getColumnPercentages() != null
                && table.getColumnPercentages().length > 0) {

            float[] percentages =
                    table.getColumnPercentages();

            columnWidths =
                    new float[percentages.length];

            float tableWidth =
                    table.getWidth();

            if (tableWidth <= 0) {
                throw new IllegalArgumentException(
                        "Table width phải > 0"
                );
            }

            for (int i = 0; i < percentages.length; i++) {

                columnWidths[i] =
                        tableWidth
                                * percentages[i]
                                / 100f;
            }

        }

        // ---------------------------------------------------------
        // Trường hợp 2: columns()
        // ---------------------------------------------------------

        else if (table.getColumnWidths() != null
                && table.getColumnWidths().length > 0) {

            columnWidths =
                    table.getColumnWidths();
        }

        // ---------------------------------------------------------
        // Không có column
        // ---------------------------------------------------------

        else {

            throw new IllegalArgumentException(
                    "Table phải có columns() hoặc columnsPercent()"
            );
        }


        // =========================================================
        // Vẽ từng row
        // =========================================================

        for (PdfRow row : table.getRows()) {

            float rowHeight = 25;

            float currentCellX = x;

            for (
                    int i = 0;
                    i < row.getCells().size();
                    i++
            ) {

                // -------------------------------------------------
                // Row có nhiều cell hơn số column
                // -------------------------------------------------

                if (i >= columnWidths.length) {

                    throw new IllegalArgumentException(
                            "Số cell trong row vượt quá số column"
                    );
                }


                // -------------------------------------------------
                // Lấy cell
                // -------------------------------------------------

                PdfCell cell =
                        row.getCells().get(i);


                // -------------------------------------------------
                // Width của cell
                // -------------------------------------------------

                float cellWidth =
                        columnWidths[i];


                // -------------------------------------------------
                // Set bounds
                // -------------------------------------------------

                cell.setBounds(
                        currentCellX,
                        y - rowHeight,
                        cellWidth,
                        rowHeight
                );


                // -------------------------------------------------
                // Draw cell
                // -------------------------------------------------

                drawCell(cell);


                // -------------------------------------------------
                // Sang column tiếp theo
                // -------------------------------------------------

                currentCellX += cellWidth;
            }


            // -----------------------------------------------------
            // Sang row tiếp theo
            // -----------------------------------------------------

            y -= rowHeight;
        }


        // =========================================================
        // Update current position
        // =========================================================

        currentY = y;
        currentX = x;
    }



    /**
     * Vẽ một cell.
     */
    private void drawCell(
            PdfCell cell
    ) throws IOException {

        PdfCellStyle style =
                cell.getStyle();

        if (style == null) {
            style = PdfCellStyle.defaultStyle();
        }

        float x = cell.getX();
        float y = cell.getY();

        float width = cell.getWidth();
        float height = cell.getHeight();

        // =====================================================
        // BORDER
        // =====================================================

        if (style.isBorderTop()) {
            line(
                    x,
                    y + height,
                    x + width,
                    y + height
            );
        }

        if (style.isBorderBottom()) {
            line(
                    x,
                    y,
                    x + width,
                    y
            );
        }

        if (style.isBorderLeft()) {
            line(
                    x,
                    y,
                    x,
                    y + height
            );
        }

        if (style.isBorderRight()) {
            line(
                    x + width,
                    y,
                    x + width,
                    y + height
            );
        }

        // =====================================================
        // ELEMENTS
        // =====================================================

        float elementX =
                x + style.getPaddingLeft();

        float centerY =
                y + height / 2;

        for (PdfElement element :
                cell.getElements()) {

            // =================================================
            // TEXT
            // =================================================

            if (element instanceof PdfTextElement text) {

                PdfFont font =
                        text.getStyle().getFont()
                                != null
                                ? text.getStyle().getFont()
                                : config.getDefaultFont();

                float fontSize =
                        text.getStyle().getFontSize()
                                != null
                                ? text.getStyle().getFontSize()
                                : config.getDefaultFontSize();

                float textY =
                        centerY - fontSize / 2;

                textAt(
                        text.getText(),
                        elementX,
                        textY,
                        font,
                        fontSize
                );

                PDFont pdfFont = getFont(config.getDefaultFont());

                float textWidth =
                        pdfFont.getStringWidth(text.getText())
                                / 1000f
                                * fontSize;

                elementX += textWidth;
            }

            // =================================================
            // CHECKBOX
            // =================================================

            else if (
                    element instanceof PdfCheckboxElement checkbox
            ) {

                float size =
                        checkbox.getSize();

                float checkboxY =
                        centerY - size / 2;

                if (checkbox.getPosition()
                        == PdfCheckboxElement.Position.LEFT) {

                    checkbox(
                            elementX,
                            checkboxY,
                            size,
                            checkbox.isChecked()
                    );

                    elementX += size + 5;

                    if (!checkbox.getText().isEmpty()) {

                        textAt(
                                checkbox.getText(),
                                elementX,
                                centerY
                                        - config.getDefaultFontSize()
                                        / 2
                        );

                        PDFont font =
                                getFont(
                                        config.getDefaultFont()
                                );

                        float textWidth =
                                font.getStringWidth(
                                        checkbox.getText()
                                )
                                        / 1000
                                        * config.getDefaultFontSize();

                        elementX += textWidth;
                    }

                } else {

                    // RIGHT
                    PDFont font =
                            getFont(
                                    config.getDefaultFont()
                            );

                    float textWidth =
                            font.getStringWidth(
                                    checkbox.getText()
                            )
                                    / 1000
                                    * config.getDefaultFontSize();

                    float checkboxX =
                            x
                                    + width
                                    - style.getPaddingRight()
                                    - size;

                    if (!checkbox.getText().isEmpty()) {

                        textAt(
                                checkbox.getText(),
                                checkboxX
                                        - 5
                                        - textWidth,
                                centerY
                                        - config.getDefaultFontSize()
                                        / 2
                        );
                    }

                    checkbox(
                            checkboxX,
                            checkboxY,
                            size,
                            checkbox.isChecked()
                    );
                }
            }

            // =================================================
            // RADIO
            // =================================================

            else if (
                    element instanceof PdfRadioElement radio
            ) {

                float radius =
                        radio.getRadius();

                if (radio.getPosition()
                        == PdfRadioElement.Position.LEFT) {

                    radio(
                            elementX + radius,
                            centerY,
                            radius,
                            radio.isSelected()
                    );

                    elementX +=
                            radius * 2 + 5;

                    if (!radio.getText().isEmpty()) {

                        textAt(
                                radio.getText(),
                                elementX,
                                centerY
                                        - config.getDefaultFontSize()
                                        / 2
                        );
                    }

                } else {

                    PDFont font =
                            getFont(
                                    config.getDefaultFont()
                            );

                    float textWidth =
                            font.getStringWidth(
                                    radio.getText()
                            )
                                    / 1000
                                    * config.getDefaultFontSize();

                    float radioCenterX =
                            x
                                    + width
                                    - style.getPaddingRight()
                                    - radius;

                    if (!radio.getText().isEmpty()) {

                        textAt(
                                radio.getText(),
                                radioCenterX
                                        - radius
                                        - 5
                                        - textWidth,
                                centerY
                                        - config.getDefaultFontSize()
                                        / 2
                        );
                    }

                    radio(
                            radioCenterX,
                            centerY,
                            radius,
                            radio.isSelected()
                    );
                }
            }

            // =================================================
            // IMAGE
            // =================================================

            else if (
                    element instanceof PdfImageElement image
            ) {

                float imageY =
                        centerY
                                - image.getHeight() / 2;

                image(
                        image.getImagePath(),
                        elementX,
                        imageY,
                        image.getWidth(),
                        image.getHeight()
                );

                elementX +=
                        image.getWidth() + 5;
            }

            // =================================================
            // NUMBER
            // =================================================

            else if (
                    element instanceof PdfNumberElement number
            ) {

                String value =
                        number.getNumber()
                                + ". "
                                + number.getText();

                textAt(
                        value,
                        elementX,
                        centerY
                                - config.getDefaultFontSize()
                                / 2
                );
            }
        }
    }

    // =========================================================
    // LINE
    // =========================================================

    /**
     * Vẽ đường thẳng.
     */
    public void line(
            float x1,
            float y1,
            float x2,
            float y2
    ) throws IOException {

        contentStream.moveTo(x1, y1);

        contentStream.lineTo(x2, y2);

        contentStream.stroke();
    }

    // =========================================================
    // RECTANGLE
    // =========================================================

    /**
     * Vẽ hình chữ nhật.
     */
    public void rectangle(
            float x,
            float y,
            float width,
            float height
    ) throws IOException {

        contentStream.addRect(
                x,
                y,
                width,
                height
        );

        contentStream.stroke();
    }

    /**
     * Vẽ khung.
     */
    public void border(
            float x,
            float y,
            float width,
            float height
    ) throws IOException {

        rectangle(
                x,
                y,
                width,
                height
        );
    }

    // =========================================================
    // IMAGE
    // =========================================================

    /**
     * Vẽ image từ file.
     */
    public void image(
            String imagePath,
            float x,
            float y,
            float width,
            float height
    ) throws IOException {

        if (imagePath == null) {
            throw new IllegalArgumentException(
                    "imagePath không được null"
            );
        }

        PDImageXObject image =
                PDImageXObject.createFromFile(
                        imagePath,
                        document
                );

        contentStream.drawImage(
                image,
                x,
                y,
                width,
                height
        );
    }

    /**
     * Vẽ image từ BufferedImage.
     *
     * PDFBox 3.0.8:
     *
     * LosslessFactory.createFromImage(...)
     */
    public void image(
            BufferedImage image,
            float x,
            float y,
            float width,
            float height
    ) throws IOException {

        if (image == null) {
            throw new IllegalArgumentException(
                    "BufferedImage không được null"
            );
        }

        PDImageXObject pdImage =
                LosslessFactory.createFromImage(
                        document,
                        image
                );

        contentStream.drawImage(
                pdImage,
                x,
                y,
                width,
                height
        );
    }

    // =========================================================
    // SIGNATURE
    // =========================================================

    /**
     * Vẽ chữ ký.
     *
     * Hiện tại chữ ký được xử lý dưới dạng image.
     */
    public void signature(
            String imagePath,
            float x,
            float y,
            float width,
            float height
    ) throws IOException {

        image(
                imagePath,
                x,
                y,
                width,
                height
        );
    }

    // =========================================================
    // STAMP
    // =========================================================

    /**
     * Vẽ con dấu.
     *
     * Hiện tại con dấu được xử lý dưới dạng image.
     */
    public void stamp(
            String imagePath,
            float x,
            float y,
            float width,
            float height
    ) throws IOException {

        image(
                imagePath,
                x,
                y,
                width,
                height
        );
    }

    // =========================================================
    // CHECKBOX
    // =========================================================

    /**
     * Vẽ checkbox.
     *
     * checked = true:
     *
     * ┌───┐
     * │ ✓ │
     * └───┘
     */
    public void checkbox(
            float x,
            float y,
            float size,
            boolean checked
    ) throws IOException {

        rectangle(
                x,
                y,
                size,
                size
        );

        if (!checked) {
            return;
        }

        // Dấu tick
        line(
                x + size * 0.20f,
                y + size * 0.50f,
                x + size * 0.42f,
                y + size * 0.20f
        );

        line(
                x + size * 0.42f,
                y + size * 0.20f,
                x + size * 0.80f,
                y + size * 0.80f
        );
    }

    // =========================================================
    // RADIO
    // =========================================================

    /**
     * Vẽ radio button.
     *
     * selected = true:
     *
     *   ◉
     *
     * selected = false:
     *
     *   ○
     */
    public void radio(
            float centerX,
            float centerY,
            float radius,
            boolean selected
    ) throws IOException {

        drawCircle(
                centerX,
                centerY,
                radius
        );

        if (selected) {

            drawFilledCircle(
                    centerX,
                    centerY,
                    radius * 0.5f
            );
        }
    }

    /**
     * Vẽ circle outline.
     */
    private void drawCircle(
            float centerX,
            float centerY,
            float radius
    ) throws IOException {

        final float kappa =
                0.5522848f;

        float offset =
                radius * kappa;

        contentStream.moveTo(
                centerX + radius,
                centerY
        );

        contentStream.curveTo(
                centerX + radius,
                centerY + offset,
                centerX + offset,
                centerY + radius,
                centerX,
                centerY + radius
        );

        contentStream.curveTo(
                centerX - offset,
                centerY + radius,
                centerX - radius,
                centerY + offset,
                centerX - radius,
                centerY
        );

        contentStream.curveTo(
                centerX - radius,
                centerY - offset,
                centerX - offset,
                centerY - radius,
                centerX,
                centerY - radius
        );

        contentStream.curveTo(
                centerX + offset,
                centerY - radius,
                centerX + radius,
                centerY - offset,
                centerX + radius,
                centerY
        );

        contentStream.closePath();

        contentStream.stroke();
    }

    /**
     * Vẽ circle được fill.
     */
    private void drawFilledCircle(
            float centerX,
            float centerY,
            float radius
    ) throws IOException {

        final float kappa =
                0.5522848f;

        float offset =
                radius * kappa;

        contentStream.moveTo(
                centerX + radius,
                centerY
        );

        contentStream.curveTo(
                centerX + radius,
                centerY + offset,
                centerX + offset,
                centerY + radius,
                centerX,
                centerY + radius
        );

        contentStream.curveTo(
                centerX - offset,
                centerY + radius,
                centerX - radius,
                centerY + offset,
                centerX - radius,
                centerY
        );

        contentStream.curveTo(
                centerX - radius,
                centerY - offset,
                centerX - offset,
                centerY - radius,
                centerX,
                centerY - radius
        );

        contentStream.curveTo(
                centerX + offset,
                centerY - radius,
                centerX + radius,
                centerY - offset,
                centerX + radius,
                centerY
        );

        contentStream.closePath();

        contentStream.fill();
    }

    // =========================================================
    // WATERMARK
    // =========================================================

    /**
     * Watermark mặc định.
     */
    public void watermark(
            String text
    ) throws IOException {

        watermark(
                text,
                60,
                45
        );
    }

    /**
     * Watermark có font size và rotation.
     *
     * PDFBox KHÔNG có:
     *
     * contentStream.translate()
     * contentStream.rotate()
     *
     * mà phải sử dụng Matrix.
     */
    public void watermark(
            String text,
            float fontSize,
            float rotation
    ) throws IOException {

        if (text == null) {
            return;
        }

        PDRectangle pageSize =
                getPageSize();

        float centerX =
                pageSize.getWidth() / 2;

        float centerY =
                pageSize.getHeight() / 2;

        PDFont font =
                getFont(config.getDefaultFont());

        // Lưu graphics state hiện tại
        contentStream.saveGraphicsState();

        // =====================================================
        // TRANSLATE
        // =====================================================

        contentStream.transform(
                Matrix.getTranslateInstance(
                        centerX,
                        centerY
                )
        );

        // =====================================================
        // ROTATE
        // =====================================================

        contentStream.transform(
                Matrix.getRotateInstance(
                        (float) Math.toRadians(rotation),
                        0,
                        0
                )
        );

        // =====================================================
        // TEXT
        // =====================================================

        float textWidth =
                font.getStringWidth(text)
                        / 1000
                        * fontSize;

        contentStream.beginText();

        contentStream.setFont(
                font,
                fontSize
        );

        // Căn giữa watermark
        contentStream.newLineAtOffset(
                -textWidth / 2,
                -fontSize / 2
        );

        contentStream.showText(text);

        contentStream.endText();

        // Khôi phục graphics state
        contentStream.restoreGraphicsState();
    }

    // =========================================================
    // PAGE NUMBER
    // =========================================================

    /**
     * Vẽ page number tại vị trí chỉ định.
     */
    public void pageNumber(
            int pageNumber,
            float x,
            float y
    ) throws IOException {

        textAt(
                String.valueOf(pageNumber),
                x,
                y
        );
    }

    /**
     * Vẽ:
     *
     * Page 1
     *
     * ở góc dưới bên phải.
     */
    public void pageNumber(
            int pageNumber
    ) throws IOException {

        PDRectangle pageSize =
                getPageSize();

        String text =
                "Page " + pageNumber;

        PDFont font =
                getFont(config.getDefaultFont());

        float fontSize =
                config.getDefaultFontSize();

        float textWidth =
                font.getStringWidth(text)
                        / 1000
                        * fontSize;

        float x =
                pageSize.getWidth()
                        - config.getMarginRight()
                        - textWidth;

        float y =
                config.getMarginBottom()
                        - fontSize;

        textAt(
                text,
                x,
                y,
                config.getDefaultFont(),
                fontSize
        );
    }

    // =========================================================
    // CURRENT POSITION
    // =========================================================

    public float getCurrentX() {
        return currentX;
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void setCurrentY(float currentY) {
        this.currentY = currentY;
    }

    // =========================================================
    // PDF DOCUMENT
    // =========================================================

    public PDDocument getDocument() {
        return document;
    }

    public PDPage getCurrentPage() {
        return currentPage;
    }

    public PDPageContentStream getContentStream() {
        return contentStream;
    }

    // =========================================================
    // SAVE
    // =========================================================

    /**
     * Lưu PDF ra file.
     */
    public void save(
            String filePath
    ) throws IOException {

        closeContentStream();

        document.save(filePath);
    }

    /**
     * Đóng content stream hiện tại.
     */
    private void closeContentStream()
            throws IOException {

        if (contentStream != null) {

            contentStream.close();

            contentStream = null;
        }
    }

    public void pageNumber() throws IOException {

        String text = "Page " + currentPageNumber;

        PDFont font = getFont(config.getDefaultFont());
        float fontSize = 8;

        float pageWidth = currentPage.getMediaBox().getWidth();

        float textWidth =
                font.getStringWidth(text) / 1000f * fontSize;

        float x = (pageWidth - textWidth) / 2;
        float y = 20;

        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    public void watermark(String text, float rotation) throws IOException {

        PDFont font = getFont(config.getDefaultFont());
        float fontSize = 50;

        float pageWidth = currentPage.getMediaBox().getWidth();
        float pageHeight = currentPage.getMediaBox().getHeight();

        float textWidth =
                font.getStringWidth(text)
                        / 1000f
                        * fontSize;

        float x = (pageWidth - textWidth) / 2;
        float y = pageHeight / 2;

        contentStream.saveGraphicsState();

        contentStream.transform(
                Matrix.getTranslateInstance(x, y)
        );

        contentStream.transform(
                Matrix.getRotateInstance(
                        (float) Math.toRadians(rotation),
                        0,
                        0
                )
        );

        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(0, 0);
        contentStream.showText(text);
        contentStream.endText();

        contentStream.restoreGraphicsState();
    }

    public float getPageWidth() {
        return currentPage.getMediaBox().getWidth();
    }

    public float getPageHeight() {
        return currentPage.getMediaBox().getHeight();
    }

    public float getContentWidth() {
        return currentPage.getMediaBox().getWidth()
                - config.getMarginLeft()
                - config.getMarginRight();
    }

    public float getContentHeight() {
        return currentPage.getMediaBox().getHeight()
                - config.getMarginTop()
                - config.getMarginBottom();
    }

    // =========================================================
    // CLOSE
    // =========================================================

    @Override
    public void close() {

        try {

            closeContentStream();

            document.close();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Không thể đóng PDF document",
                    e
            );
        }
    }
}
