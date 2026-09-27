# PDF Rendering Module

Reusable PDF Rendering Module được xây dựng bằng Java + Apache PDFBox 3.0.8.

Module được thiết kế để tạo các tài liệu PDF nghiệp vụ như:

- Hợp đồng bảo hiểm
- Policy
- Endorsement
- Đơn yêu cầu bảo hiểm
- Biểu mẫu nghiệp vụ
- Báo cáo
- Table
- Checkbox / Radio
- Image
- Signature / Stamp
- Watermark

---

# 1. Mục tiêu

Mục tiêu của module là tách việc render PDF khỏi business logic.

Business code chỉ cần truyền dữ liệu:

    Policy
    Customer
    Premium
    Product
    Coverage
    Payment
    ...

PDF module chịu trách nhiệm:

    Data
      ↓
    PdfDraw
      ↓
    PdfTable
      ↓
    PdfRow
      ↓
    PdfCell
      ↓
    PdfElement
      ↓
    PDF

Business code không cần trực tiếp xử lý:

- PDPage
- PDPageContentStream
- tọa độ PDF
- font
- text position
- border
- table position
- checkbox
- image
- merge cell

---

# 2. Công nghệ

- Java
- Apache PDFBox 3.0.8
- Maven
- IntelliJ IDEA

Maven dependency:

    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>3.0.8</version>
    </dependency>

---

# 3. Kiến trúc

    PdfDraw
     │
     ├── PdfConfig
     │
     ├── PdfFont
     │
     ├── PdfTable
     │      │
     │      ├── PdfRow
     │      │      │
     │      │      └── PdfCell
     │      │              │
     │      │              └── PdfElement
     │      │                     ├── PdfTextElement
     │      │                     ├── PdfCheckboxElement
     │      │                     ├── PdfRadioElement
     │      │                     ├── PdfImageElement
     │      │                     └── PdfNumberElement
     │      │
     │      └── Grid / Occupancy
     │
     └── PDDocument

---

# 4. Trách nhiệm của từng class

| Class | Trách nhiệm |
|---|---|
| PdfDraw | Điều khiển PDF, page và các thao tác vẽ |
| PdfConfig | Cấu hình PDF |
| PdfFont | Định nghĩa font |
| PdfTable | Quản lý table |
| PdfRow | Quản lý row |
| PdfCell | Quản lý cell |
| PdfCellStyle | Style của cell |
| PdfElement | Contract cho nội dung trong cell |
| PdfTextElement | Text |
| PdfCheckboxElement | Checkbox |
| PdfRadioElement | Radio |
| PdfImageElement | Image |
| PdfNumberElement | Number / list |

---

# 5. PdfConfig

PdfConfig chứa cấu hình mặc định của PDF.

Ví dụ:

    PdfConfig config = PdfConfig.builder()
            .pageSize(PdfConfig.PageSize.A4)
            .orientation(PdfConfig.Orientation.PORTRAIT)
            .margin(40)
            .defaultFont(PdfFont.TIMES_NEW_ROMAN)
            .defaultFontSize(10)
            .lineSpacing(1.2f)
            .build();

---

## 5.1 Page Size

Các page size:

    PdfConfig.PageSize.A4
    PdfConfig.PageSize.A5
    PdfConfig.PageSize.LETTER

Ví dụ:

    .pageSize(PdfConfig.PageSize.A4)

---

## 5.2 Orientation

    PdfConfig.Orientation.PORTRAIT
    PdfConfig.Orientation.LANDSCAPE

Ví dụ:

    .orientation(
            PdfConfig.Orientation.PORTRAIT
    )

---

## 5.3 Margin

Thiết lập cùng một margin:

    .margin(40)

Tương đương:

    Top    = 40
    Right  = 40
    Bottom = 40
    Left   = 40

Hoặc cấu hình riêng:

    .margins(
            40, // top
            40, // right
            40, // bottom
            40  // left
    )

---

# 6. PdfFont

PdfFont định nghĩa các font được sử dụng trong PDF.

Ví dụ:

    PdfFont.TIMES_NEW_ROMAN
    PdfFont.TIMES_NEW_ROMAN_BOLD
    PdfFont.ARIAL
    PdfFont.ARIAL_BOLD

Ví dụ enum:

    public enum PdfFont {

        TIMES_NEW_ROMAN(
                "Times New Roman",
                "D:/back_end_java/font/times.ttf"
        ),

        TIMES_NEW_ROMAN_BOLD(
                "Times New Roman Bold",
                "D:/back_end_java/font/timesbd.ttf"
        ),

        ARIAL(
                "Arial",
                "D:/back_end_java/font/arial.ttf"
        ),

        ARIAL_BOLD(
                "Arial Bold",
                "D:/back_end_java/font/arialbd.ttf"
        );

        private final String name;
        private final String path;

        PdfFont(
                String name,
                String path
        ) {
            this.name = name;
            this.path = path;
        }
    }

---

## 6.1 Lưu ý về PDFont

PDFont của PDFBox thuộc về một PDDocument cụ thể.

Không nên:

    static PDFont font;

để dùng chung cho nhiều PDDocument.

Nên cache PDFont theo từng PdfDraw / PDDocument:

    PdfDraw
      │
      └── PDDocument
            │
            └── fontCache
                  ├── TIMES_NEW_ROMAN
                  └── ARIAL

PdfFont chỉ là metadata / cấu hình font.

PDFont là object thực tế được load vào document.

---

# 7. PdfDraw

PdfDraw là class chính của module.

Khởi tạo:

    PdfDraw pdf = new PdfDraw(config);

Các nhóm chức năng:

    Text
    TextAt
    TextInline
    NewLine
    Line
    Rectangle
    Border
    Image
    Signature
    Stamp
    Checkbox
    Radio
    Watermark
    PageNumber
    AddPage
    Table
    DrawTable
    Save
    Close

---

# 8. Text

## 8.1 Text thông thường

    pdf.text("Hello PDF");

Xuống dòng:

    pdf.newLine();

Ví dụ:

    pdf.text("DEMO PDF");
    pdf.newLine();

    pdf.text("Insurance Policy");
    pdf.newLine();

    pdf.text("Policy No: POL-001");
    pdf.newLine();

---

# 9. Text tại vị trí cụ thể

    pdf.textAt(
            "Policy No: POL-001",
            100,
            700
    );

Trong đó:

    x = 100
    y = 700

---

# 10. Text Inline

Dùng để viết nhiều phần text trên cùng một dòng.

    pdf.textInline("Policy No: ");
    pdf.textInline("POL-2026-000001");
    pdf.newLine();

Kết quả:

    Policy No: POL-2026-000001

---

# 11. Line

    pdf.line(
            40,
            680,
            555,
            680
    );

---

# 12. Rectangle

    pdf.rectangle(
            40,
            620,
            515,
            40
    );

---

# 13. Border

    pdf.border(
            40,
            550,
            515,
            50
    );

---

# 14. Checkbox

Checkbox trực tiếp trên PDF:

    pdf.checkbox(
            50,
            520,
            12,
            true
    );

Tham số:

    x       = 50
    y       = 520
    size    = 12
    checked = true

Ví dụ:

    pdf.checkbox(50, 520, 12, true);
    pdf.textAt("Đã thanh toán", 70, 520);

    pdf.checkbox(50, 490, 12, false);
    pdf.textAt("Chưa thanh toán", 70, 490);

---

# 15. Radio

    pdf.radio(
            50,
            460,
            6,
            true
    );

Ví dụ:

    pdf.radio(50, 460, 6, true);
    pdf.textAt("Nam", 70, 460);

    pdf.radio(50, 430, 6, false);
    pdf.textAt("Nữ", 70, 430);

---

# 16. Image

    pdf.image(
            "D:/back_end_java/image/logo.png",
            40,
            300,
            120,
            60
    );

---

# 17. Signature

    pdf.signature(
            "D:/back_end_java/image/signature.png",
            350,
            280,
            120,
            60
    );

---

# 18. Stamp

    pdf.stamp(
            "D:/back_end_java/image/stamp.png",
            400,
            200,
            100,
            100
    );

---

# 19. Watermark

    pdf.watermark(
            "CONFIDENTIAL",
            45
    );

Trong đó:

    CONFIDENTIAL = nội dung watermark
    45           = góc xoay

---

# 20. Page Number

    pdf.pageNumber();

---

# 21. Page Management

Tạo page mới:

    pdf.addPage();

Ví dụ:

    pdf.text("Page 1");

    pdf.addPage();

    pdf.text("Page 2");

---

# 22. Content Width

Không nên hard-code:

    515

Nên sử dụng:

    pdf.getContentWidth()

Ví dụ:

    PdfTable table = pdf.table()
            .width(pdf.getContentWidth());

Điều này giúp table tự thích ứng với page size và margin.

---

# 23. PdfTable

Tạo table:

    PdfTable table = pdf.table();

Hoặc:

    PdfTable table = pdf.table()
            .width(pdf.getContentWidth());

---

# 24. Column Width

Có 2 cách định nghĩa width.

## 24.1 Absolute Width

    .columns(
            100,
            200,
            215
    )

Ví dụ:

    PdfTable table = pdf.table()
            .width(pdf.getContentWidth())
            .columns(
                    100,
                    200,
                    215
            );

Tổng width:

    100 + 200 + 215 = 515

---

## 24.2 Percentage Width

Khuyến nghị sử dụng khi table cần thích ứng theo content width.

    .columnsPercent(
            20,
            50,
            30
    )

Tổng percentage phải bằng:

    100%

Ví dụ hợp lệ:

    .columnsPercent(50, 50)

    .columnsPercent(25, 25, 50)

    .columnsPercent(20, 30, 50)

Ví dụ không hợp lệ:

    .columnsPercent(20, 20, 20)

Vì:

    20 + 20 + 20 = 60%

---

# 25. PdfRow

Có 2 cách tạo row.

## 25.1 newRow()

Dùng cho table thông thường:

    table.newRow()
            .addText("Loại")
            .addText("Nội dung");

---

## 25.2 startRow()

Dùng khi cần thao tác trực tiếp với PdfCell.

    table.startRow()
            .addCell(cell);

Hoặc:

    PdfRow row = table.startRow();

    row.addCell(cell);

Cách này phù hợp khi sử dụng:

    colSpan
    rowSpan
    multiple elements

---

# 26. PdfCell

Tạo cell:

    PdfCell cell = new PdfCell(
            PdfCellStyle.defaultStyle()
    );

Thêm element:

    cell.addElement(
            new PdfTextElement(
                    "Hello",
                    PdfCellStyle.defaultStyle()
            )
    );

Một PdfCell có thể chứa nhiều PdfElement:

    PdfCell
      │
      ├── Text
      ├── Checkbox
      ├── Radio
      └── Image

---

# 27. PdfElement

PdfElement là interface chung cho các nội dung bên trong cell.

    public interface PdfElement {
    }

Các element:

    PdfTextElement
    PdfCheckboxElement
    PdfRadioElement
    PdfImageElement
    PdfNumberElement

Các class này phải implements PdfElement.

Ví dụ:

    public final class PdfTextElement
            implements PdfElement {

        private final String text;
        private final PdfCellStyle style;

        public PdfTextElement(
                String text,
                PdfCellStyle style
        ) {
            this.text = text;
            this.style = style;
        }
    }

---

# 28. PdfTextElement

Ví dụ:

    new PdfTextElement(
            "THÔNG TIN HỢP ĐỒNG",
            PdfCellStyle.header()
    )

---

# 29. PdfCheckboxElement

Checkbox trong cell:

    new PdfCheckboxElement(
            "Đã chọn",
            true,
            12,
            PdfCheckboxElement.Position.LEFT
    )

Position:

    PdfCheckboxElement.Position.LEFT
    PdfCheckboxElement.Position.RIGHT

Ví dụ:

    PdfCheckboxElement(
            "Đã thanh toán",
            true,
            12,
            Position.LEFT
    )

Kết quả:

    ☑ Đã thanh toán

RIGHT:

    Đã thanh toán ☑

---

# 30. PdfRadioElement

Radio trong cell:

    new PdfRadioElement(
            "Nam",
            true,
            6,
            PdfRadioElement.Position.LEFT
    )

Position:

    PdfRadioElement.Position.LEFT
    PdfRadioElement.Position.RIGHT

---

# 31. PdfImageElement

Image trong cell:

    new PdfImageElement(
            "D:/back_end_java/image/logo.png",
            100,
            50
    )

---

# 32. PdfNumberElement

Number / list trong cell:

    new PdfNumberElement(
            1,
            "Điều khoản bảo hiểm"
    )

Ví dụ:

    1. Điều khoản bảo hiểm
    2. Quyền lợi bảo hiểm
    3. Điều kiện bảo hiểm

---

# 33. PdfCellStyle

PdfCellStyle dùng để cấu hình style cho từng cell.

Ví dụ:

    PdfCellStyle style =
            PdfCellStyle.builder()
                    .font(PdfFont.TIMES_NEW_ROMAN)
                    .fontSize(10f)
                    .bold(false)
                    .horizontalAlign(
                            PdfCellStyle.HorizontalAlign.LEFT
                    )
                    .verticalAlign(
                            PdfCellStyle.VerticalAlign.MIDDLE
                    )
                    .build();

---

# 34. Header Style

Có thể sử dụng style có sẵn:

    PdfCellStyle.header()

Ví dụ:

    table.newRow()
            .addText(
                    "Loại",
                    PdfCellStyle.header()
            )
            .addText(
                    "Nội dung",
                    PdfCellStyle.header()
            );

---

# 35. Cell Padding

Cell có thể có padding:

    paddingTop
    paddingRight
    paddingBottom
    paddingLeft

Ví dụ:

    PdfCellStyle.builder()
            .paddingTop(5)
            .paddingRight(5)
            .paddingBottom(5)
            .paddingLeft(5)
            .build();

---

# 36. Horizontal Alignment

Các giá trị:

    PdfCellStyle.HorizontalAlign.LEFT
    PdfCellStyle.HorizontalAlign.CENTER
    PdfCellStyle.HorizontalAlign.RIGHT

Ví dụ:

    .horizontalAlign(
            PdfCellStyle.HorizontalAlign.CENTER
    )

---

# 37. Vertical Alignment

Các giá trị:

    PdfCellStyle.VerticalAlign.TOP
    PdfCellStyle.VerticalAlign.MIDDLE
    PdfCellStyle.VerticalAlign.BOTTOM

Ví dụ:

    .verticalAlign(
            PdfCellStyle.VerticalAlign.MIDDLE
    )

---

# 38. Multiple Elements trong một Cell

Một cell không chỉ chứa một text.

Có thể:

    PdfCell cell = new PdfCell(
            PdfCellStyle.defaultStyle()
    );

    cell.addElement(
            new PdfTextElement(
                    "Thanh toán",
                    PdfCellStyle.defaultStyle()
            )
    );

    cell.addElement(
            new PdfCheckboxElement(
                    "",
                    true,
                    12,
                    PdfCheckboxElement.Position.LEFT
            )
    );

Mô hình:

    PdfCell
       │
       ├── PdfTextElement
       │
       └── PdfCheckboxElement

---

# 39. Fluent API cho Table

Table hỗ trợ API ngắn gọn:

    table.newRow()
            .addText("Loại")
            .addText("Nội dung");

Checkbox:

    table.newRow()
            .addText("Payment")
            .addCheckbox(
                    "Đã thanh toán",
                    true,
                    12,
                    PdfCheckboxElement.Position.LEFT
            );

Radio:

    table.newRow()
            .addText("Gender")
            .addRadio(
                    "Nam",
                    true,
                    6,
                    PdfRadioElement.Position.LEFT
            );

Image:

    table.newRow()
            .addText("Logo")
            .addImage(
                    "D:/back_end_java/image/logo.png",
                    100,
                    50
            );

Number:

    table.newRow()
            .addText("Điều khoản")
            .addNumber(
                    1,
                    "Điều khoản bảo hiểm"
            );

---

# 40. ColSpan

colSpan dùng để merge nhiều column thành một cell.

Ví dụ table:

    .columnsPercent(
            50,
            50
    );

Tạo cell:

    PdfCell cell = new PdfCell(
            PdfCellStyle.defaultStyle()
    );

    cell.addElement(
            new PdfTextElement(
                    "THÔNG TIN HỢP ĐỒNG",
                    PdfCellStyle.header()
            )
    );

    cell.colSpan(2);

Sau đó:

    table.newRow()
            .addCell(cell);

Kết quả:

    ┌───────────────────────────────────────────────┐
    │              THÔNG TIN HỢP ĐỒNG               │
    │                   colSpan=2                   │
    ├────────────────────────┬──────────────────────┤
    │          Loại          │       Nội dung       │
    └────────────────────────┴──────────────────────┘

---

# 41. RowSpan

rowSpan dùng để merge nhiều row thành một cell.

Ví dụ:

    PdfTable table = pdf.table()
            .width(pdf.getContentWidth())
            .columnsPercent(
                    30,
                    35,
                    35
            );

Tạo cell:

    PdfCell policyCell = new PdfCell(
            PdfCellStyle.header()
    );

    policyCell.addElement(
            new PdfTextElement(
                    "POLICY",
                    PdfCellStyle.header()
            )
    );

    policyCell.rowSpan(2);

Row đầu:

    table.newRow()
            .addCell(policyCell)
            .addText(
                    "Policy No",
                    PdfCellStyle.header()
            )
            .addText(
                    "POL-001"
            );

Row thứ hai:

    table.newRow()
            .addText(
                    "Premium",
                    PdfCellStyle.header()
            )
            .addText(
                    "100,000,000"
            );

Kết quả:

    ┌───────────────┬────────────────┬────────────────┐
    │               │   Policy No     │    POL-001     │
    │    POLICY     ├────────────────┼────────────────┤
    │   rowSpan=2   │    Premium     │   100,000,000  │
    │               │                │                │
    └───────────────┴────────────────┴────────────────┘

Cell POLICY chiếm:

    row 0
    row 1

---

# 42. ColSpan + RowSpan

Có thể sử dụng đồng thời:

    cell.colSpan(2);
    cell.rowSpan(2);

Ví dụ:

    PdfCell cell = new PdfCell(
            PdfCellStyle.header()
    );

    cell.addElement(
            new PdfTextElement(
                    "THÔNG TIN",
                    PdfCellStyle.header()
            )
    );

    cell.colSpan(2);
    cell.rowSpan(2);

Cell sẽ chiếm:

    2 column
    2 row

---

# 43. Grid / Occupancy

Để hỗ trợ rowSpan và colSpan chính xác, renderer sử dụng mô hình Grid / Occupancy.

Ví dụ:

    A.colSpan(2)

    ┌─────────┬─────────┐
    │    X    │    X    │
    └─────────┴─────────┘

Renderer đánh dấu:

    occupied[row][column] = true

Nếu:

    A.rowSpan(2)

thì:

    ┌─────────┬─────────┐
    │    X    │         │
    ├─────────┤         │
    │    X    │         │
    └─────────┴─────────┘

---

# 44. Vì sao cần Occupancy Grid?

Không thể chỉ dùng:

    row.getCells().get(i)

để xác định column.

Ví dụ:

    ┌──────────────┬───────┐
    │              │   B   │
    │      A       ├───────┤
    │   rowSpan=2  │   C   │
    │              │       │
    └──────────────┴───────┘

A chiếm column 0 ở cả 2 row.

Khi renderer xử lý C:

    column 0 = occupied
    column 1 = free

C phải tự động được đặt vào column 1.

Do đó renderer cần:

    findFreePosition()

và:

    markOccupied()

---

# 45. drawTable()

drawTable chịu trách nhiệm:

1. Tính column width
2. Tính table position
3. Tạo occupancy grid
4. Tìm vị trí cell
5. Tính cell width
6. Tính cell height
7. Set bounds cho cell
8. Đánh dấu vùng occupied
9. Render cell
10. Cập nhật currentY

Luồng:

    drawTable()
        │
        ├── calculateColumnWidths()
        │
        ├── create occupancy grid
        │
        ├── findFreePosition()
        │
        ├── calculate cell bounds
        │
        ├── markOccupied()
        │
        └── drawCell()

---

# 46. findFreePosition()

Ý tưởng:

    private int findFreePosition(
            boolean[][] occupied,
            int startRow,
            int colSpan,
            int rowSpan,
            int columnCount
    ) {

        int rowCount =
                occupied.length;

        for (int column = 0;
             column <= columnCount - colSpan;
             column++) {

            boolean available = true;

            for (int row = startRow;
                 row < startRow + rowSpan;
                 row++) {

                if (row >= rowCount) {
                    available = false;
                    break;
                }

                for (int col = column;
                     col < column + colSpan;
                     col++) {

                    if (occupied[row][col]) {
                        available = false;
                        break;
                    }
                }

                if (!available) {
                    break;
                }
            }

            if (available) {
                return column;
            }
        }

        return -1;
    }

---

# 47. markOccupied()

Ý tưởng:

    private void markOccupied(
            boolean[][] occupied,
            int startRow,
            int startColumn,
            int rowSpan,
            int colSpan
    ) {

        for (int row = startRow;
             row < startRow + rowSpan;
             row++) {

            for (int col = startColumn;
                 col < startColumn + colSpan;
                 col++) {

                occupied[row][col] = true;
            }
        }
    }

---

# 48. Tính Width của ColSpan

Ví dụ:

    columnsPercent(20, 30, 50)

Table width:

    515

Column width:

    Column 0 = 103
    Column 1 = 154.5
    Column 2 = 257.5

Nếu:

    cell.colSpan(2)

thì:

    cellWidth =
        columnWidth[0]
        + columnWidth[1]

    cellWidth =
        103 + 154.5

    cellWidth =
        257.5

Nếu:

    cell.colSpan(3)

thì:

    cellWidth =
        103 + 154.5 + 257.5

    cellWidth =
        515

---

# 49. Tính Height của RowSpan

Nếu:

    rowHeight[0] = 25
    rowHeight[1] = 25
    rowHeight[2] = 25

và:

    cell.rowSpan(2)

thì:

    cellHeight =
        rowHeight[0]
        + rowHeight[1]

    cellHeight =
        50

Nếu:

    cell.rowSpan(3)

thì:

    cellHeight = 75

---

# 50. Ví dụ Table cơ bản

    PdfTable table = pdf.table()
            .width(pdf.getContentWidth())
            .columnsPercent(
                    20,
                    50,
                    30
            );

    table.newRow()
            .addText(
                    "Loại",
                    PdfCellStyle.header()
            )
            .addText(
                    "Nội dung",
                    PdfCellStyle.header()
            )
            .addText(
                    "Giá trị",
                    PdfCellStyle.header()
            );

    table.newRow()
            .addText("Policy No")
            .addText("Số hợp đồng")
            .addText("POL-001");

    table.newRow()
            .addText("Premium")
            .addText("Phí bảo hiểm")
            .addText("100,000,000");

    pdf.drawTable(table);

---

# 51. Ví dụ Header Merge

    PdfTable table = pdf.table()
            .width(pdf.getContentWidth())
            .columnsPercent(
                    50,
                    50
            );

    PdfCell titleCell = new PdfCell(
            PdfCellStyle.defaultStyle()
    );

    titleCell.addElement(
            new PdfTextElement(
                    "THÔNG TIN HỢP ĐỒNG",
                    PdfCellStyle.header()
            )
    );

    titleCell.colSpan(2);

    table.newRow()
            .addCell(titleCell);

    table.newRow()
            .addText(
                    "Loại",
                    PdfCellStyle.header()
            )
            .addText(
                    "Nội dung",
                    PdfCellStyle.header()
            );

    pdf.drawTable(table);

---

# 52. Ví dụ RowSpan

    PdfTable table = pdf.table()
            .width(pdf.getContentWidth())
            .columnsPercent(
                    30,
                    35,
                    35
            );

    PdfCell policyCell = new PdfCell(
            PdfCellStyle.header()
    );

    policyCell.addElement(
            new PdfTextElement(
                    "POLICY",
                    PdfCellStyle.header()
            )
    );

    policyCell.rowSpan(2);

    table.newRow()
            .addCell(policyCell)
            .addText(
                    "Policy No",
                    PdfCellStyle.header()
            )
            .addText(
                    "POL-001"
            );

    table.newRow()
            .addText(
                    "Premium",
                    PdfCellStyle.header()
            )
            .addText(
                    "100,000,000"
            );

    pdf.drawTable(table);

---

# 53. Ví dụ Table Insurance

    PdfTable table = pdf.table()
            .width(pdf.getContentWidth())
            .columnsPercent(
                    25,
                    35,
                    40
            );

    // Header merge
    PdfCell titleCell = new PdfCell(
            PdfCellStyle.defaultStyle()
    );

    titleCell.addElement(
            new PdfTextElement(
                    "THÔNG TIN HỢP ĐỒNG BẢO HIỂM",
                    PdfCellStyle.header()
            )
    );

    titleCell.colSpan(3);

    table.newRow()
            .addCell(titleCell);

    // Header
    table.newRow()
            .addText(
                    "Loại",
                    PdfCellStyle.header()
            )
            .addText(
                    "Tên trường",
                    PdfCellStyle.header()
            )
            .addText(
                    "Giá trị",
                    PdfCellStyle.header()
            );

    // Policy
    table.newRow()
            .addText("Policy")
            .addText("Policy No")
            .addText("POL-2026-000001");

    // Product
    table.newRow()
            .addText("Product")
            .addText("Product Code")
            .addText("P6");

    // Premium
    table.newRow()
            .addText("Premium")
            .addText("Premium")
            .addText("100,000,000 VND");

    // Payment
    table.newRow()
            .addText("Payment")
            .addText("Payment Status")
            .addCheckbox(
                    "Đã thanh toán",
                    true,
                    12,
                    PdfCheckboxElement.Position.LEFT
            );

    pdf.drawTable(table);

---

# 54. Ví dụ Table có Checkbox và Radio

    PdfTable table = pdf.table()
            .width(pdf.getContentWidth())
            .columnsPercent(
                    30,
                    70
            );

    table.newRow()
            .addText(
                    "Thanh toán",
                    PdfCellStyle.header()
            )
            .addCheckbox(
                    "Đã thanh toán",
                    true,
                    12,
                    PdfCheckboxElement.Position.LEFT
            );

    table.newRow()
            .addText(
                    "Trạng thái"
            )
            .addCheckbox(
                    "Active",
                    true,
                    12,
                    PdfCheckboxElement.Position.RIGHT
            );

    table.newRow()
            .addText(
                    "Giới tính"
            )
            .addRadio(
                    "Nam",
                    true,
                    6,
                    PdfRadioElement.Position.LEFT
            );

    table.newRow()
            .addText(
                    "Giới tính"
            )
            .addRadio(
                    "Nữ",
                    false,
                    6,
                    PdfRadioElement.Position.LEFT
            );

    pdf.drawTable(table);

---

# 55. Ví dụ Table có Image

    String imagePath =
            "D:/back_end_java/image/logo.png";

    PdfTable table = pdf.table()
            .width(pdf.getContentWidth())
            .columnsPercent(
                    30,
                    70
            );

    table.newRow()
            .addText("Logo")
            .addImage(
                    imagePath,
                    100,
                    50
            );

    pdf.drawTable(table);

---

# 56. Ví dụ Multiple Elements

Một cell có thể chứa text + checkbox:

    PdfCell paymentCell = new PdfCell(
            PdfCellStyle.defaultStyle()
    );

    paymentCell.addElement(
            new PdfTextElement(
                    "Đã thanh toán",
                    PdfCellStyle.defaultStyle()
            )
    );

    paymentCell.addElement(
            new PdfCheckboxElement(
                    "",
                    true,
                    12,
                    PdfCheckboxElement.Position.LEFT
            )
    );

Sau đó:

    table.newRow()
            .addCell(paymentCell);

---

# 57. Demo hoàn chỉnh

    public static void main(String[] args) {

        try {

            PdfConfig config =
                    PdfConfig.builder()
                            .pageSize(
                                    PdfConfig.PageSize.A4
                            )
                            .orientation(
                                    PdfConfig.Orientation.PORTRAIT
                            )
                            .margin(40)
                            .defaultFont(
                                    PdfFont.TIMES_NEW_ROMAN
                            )
                            .defaultFontSize(10)
                            .lineSpacing(1.2f)
                            .build();


            PdfDraw pdf =
                    new PdfDraw(config);


            // =================================================
            // BASIC TEXT
            // =================================================

            pdf.text(
                    "DEMO PDF RENDERING MODULE"
            );

            pdf.newLine();

            pdf.text(
                    "Hệ thống bảo hiểm - Policy Issuance"
            );

            pdf.newLine();

            pdf.newLine();


            // =================================================
            // INLINE TEXT
            // =================================================

            pdf.textInline(
                    "Policy No: "
            );

            pdf.textInline(
                    "POL-2026-000001"
            );

            pdf.newLine();

            pdf.textInline(
                    "Premium: "
            );

            pdf.textInline(
                    "100,000,000 VND"
            );

            pdf.newLine();

            pdf.newLine();


            // =================================================
            // BASIC DRAWING
            // =================================================

            pdf.line(
                    40,
                    680,
                    555,
                    680
            );

            pdf.rectangle(
                    40,
                    620,
                    515,
                    40
            );

            pdf.border(
                    40,
                    550,
                    515,
                    50
            );


            // =================================================
            // CHECKBOX
            // =================================================

            pdf.checkbox(
                    50,
                    520,
                    12,
                    true
            );

            pdf.textAt(
                    "Đã thanh toán",
                    70,
                    520
            );


            pdf.checkbox(
                    50,
                    490,
                    12,
                    false
            );

            pdf.textAt(
                    "Chưa thanh toán",
                    70,
                    490
            );


            // =================================================
            // RADIO
            // =================================================

            pdf.radio(
                    50,
                    460,
                    6,
                    true
            );

            pdf.textAt(
                    "Nam",
                    70,
                    460
            );


            pdf.radio(
                    50,
                    430,
                    6,
                    false
            );

            pdf.textAt(
                    "Nữ",
                    70,
                    430
            );


            // =================================================
            // WATERMARK
            // =================================================

            pdf.watermark(
                    "CONFIDENTIAL",
                    45
            );


            // =================================================
            // PAGE NUMBER
            // =================================================

            pdf.pageNumber();


            // =================================================
            // NEW PAGE
            // =================================================

            pdf.addPage();


            // =================================================
            // TABLE
            // =================================================

            PdfTable table =
                    pdf.table()
                            .width(
                                    pdf.getContentWidth()
                            )
                            .columnsPercent(
                                    20,
                                    50,
                                    30
                            );


            // =================================================
            // TABLE HEADER - COLSPAN
            // =================================================

            PdfCell titleCell =
                    new PdfCell(
                            PdfCellStyle.defaultStyle()
                    );

            titleCell.addElement(
                    new PdfTextElement(
                            "THÔNG TIN HỢP ĐỒNG",
                            PdfCellStyle.header()
                    )
            );

            titleCell.colSpan(3);

            table.newRow()
                    .addCell(titleCell);


            // =================================================
            // HEADER
            // =================================================

            table.newRow()
                    .addText(
                            "Loại",
                            PdfCellStyle.header()
                    )
                    .addText(
                            "Nội dung",
                            PdfCellStyle.header()
                    )
                    .addText(
                            "Giá trị",
                            PdfCellStyle.header()
                    );


            // =================================================
            // TEXT
            // =================================================

            table.newRow()
                    .addText("Policy")
                    .addText("Policy No")
                    .addText("POL-2026-000001");


            // =================================================
            // CHECKBOX
            // =================================================

            table.newRow()
                    .addText("Payment")
                    .addText("Payment Status")
                    .addCheckbox(
                            "Đã thanh toán",
                            true,
                            12,
                            PdfCheckboxElement.Position.LEFT
                    );


            // =================================================
            // RADIO
            // =================================================

            table.newRow()
                    .addText("Customer")
                    .addText("Gender")
                    .addRadio(
                            "Nam",
                            true,
                            6,
                            PdfRadioElement.Position.LEFT
                    );


            // =================================================
            // NUMBER
            // =================================================

            table.newRow()
                    .addText("Clause")
                    .addNumber(
                            1,
                            "Điều khoản bảo hiểm"
                    )
                    .addText("Required");


            // =================================================
            // IMAGE
            // =================================================

            String imagePath =
                    "D:/back_end_java/image/logo.png";

            table.newRow()
                    .addText("Image")
                    .addImage(
                            imagePath,
                            100,
                            50
                    )
                    .addText("Company Logo");


            // =================================================
            // DRAW TABLE
            // =================================================

            pdf.drawTable(table);


            // =================================================
            // IMAGE OUTSIDE TABLE
            // =================================================

            pdf.image(
                    imagePath,
                    40,
                    300,
                    120,
                    60
            );


            // =================================================
            // SIGNATURE
            // =================================================

            String signaturePath =
                    "D:/back_end_java/image/signature.png";

            pdf.signature(
                    signaturePath,
                    350,
                    280,
                    120,
                    60
            );


            // =================================================
            // STAMP
            // =================================================

            String stampPath =
                    "D:/back_end_java/image/stamp.png";

            pdf.stamp(
                    stampPath,
                    400,
                    200,
                    100,
                    100
            );


            pdf.textAt(
                    "Người yêu cầu bảo hiểm",
                    350,
                    260
            );


            // =================================================
            // SAVE
            // =================================================

            String output =
                    "D:/back_end_java/output/pdf-demo.pdf";

            pdf.save(output);

            pdf.close();


            System.out.println(
                    "PDF created successfully:"
            );

            System.out.println(output);


        } catch (Exception e) {

            e.printStackTrace();
        }
    }

---

# 58. Quy tắc sử dụng Table

## Table đơn giản

Sử dụng:

    table.newRow()
            .addText(...)
            .addText(...);

Không cần tạo PdfCell thủ công.

---

## Table có merge

Sử dụng:

    PdfCell cell = new PdfCell(...);

    cell.colSpan(2);

    table.newRow()
            .addCell(cell);

Hoặc:

    cell.rowSpan(2);

---

## Table có nhiều element

Sử dụng:

    PdfCell cell = new PdfCell(...);

    cell.addElement(...);
    cell.addElement(...);
    cell.addElement(...);

---

# 59. Khi nào sử dụng newRow() và startRow()?

## newRow()

Dùng cho trường hợp thông thường:

    table.newRow()
            .addText("A")
            .addText("B")
            .addText("C");

Ưu điểm:

- Code ngắn
- Dễ đọc
- Phù hợp table thông thường

---

## startRow()

Dùng khi cần custom cell:

    PdfCell cell = new PdfCell(...);

    cell.colSpan(2);

    table.startRow()
            .addCell(cell);

Phù hợp:

- colSpan
- rowSpan
- nhiều element
- custom cell style
- cell đặc biệt

---

# 60. Tư duy thiết kế API

API của module nên hướng tới:

    Simple case
        ↓
    Fluent API

Ví dụ:

    table.newRow()
            .addText(...)
            .addText(...)
            .addCheckbox(...);

Advanced case:

    PdfCell cell = new PdfCell(...);

    cell.addElement(...);
    cell.addElement(...);
    cell.colSpan(2);
    cell.rowSpan(2);

    table.startRow()
            .addCell(cell);

Như vậy vừa dễ sử dụng cho case đơn giản, vừa đủ flexible cho PDF nghiệp vụ phức tạp.

---

# 61. Nguyên tắc không hard-code Page Width

Không nên:

    table.width(515);

Nên:

    table.width(
            pdf.getContentWidth()
    );

Lý do:

Nếu page thay đổi:

    A4
    A5
    LETTER
    LANDSCAPE

hoặc margin thay đổi thì content width cũng thay đổi.

Table vẫn có thể sử dụng lại.

---

# 62. Nguyên tắc Cell Style

Global config:

    PdfConfig

chứa:

    defaultFont
    defaultFontSize
    lineSpacing

Cell có thể override:

    PdfCellStyle

Ví dụ:

    PdfConfig
        defaultFont = Times New Roman
        defaultSize = 10

Một cell:

    PdfCellStyle
        fontSize = 12
        bold = true
        align = CENTER

Do đó:

    Global Config
          ↓
    Cell Style Override

---

# 63. Nguyên tắc Element

PdfCell không nên chỉ chứa:

    String text

Mà nên chứa:

    List<PdfElement>

Ví dụ:

    PdfCell
       │
       ├── Text
       ├── Checkbox
       ├── Text
       └── Image

Điều này giúp module mở rộng dễ dàng.

Sau này có thể thêm:

    PdfSignatureElement
    PdfStampElement
    PdfBarcodeElement
    PdfQrCodeElement
    PdfDateElement
    PdfCurrencyElement

mà không cần thay đổi cấu trúc PdfCell.

---

# 64. Nguyên tắc PdfDraw và PdfTable

PdfDraw chịu trách nhiệm:

    PDF
    Page
    Coordinate
    Drawing
    Font
    Image
    Table Rendering

PdfTable chịu trách nhiệm:

    Rows
    Cells
    Columns
    Column Width
    Table structure

PdfCell chịu trách nhiệm:

    Elements
    Style
    ColSpan
    RowSpan
    Bounds

Không nên đưa:

    addCell()

vào PdfDraw.

addCell() thuộc về:

    PdfRow

Vì cell là thành phần của row:

    PdfTable
       ↓
    PdfRow
       ↓
    PdfCell

---

# 65. ColSpan / RowSpan Renderer

Renderer phải xử lý theo Grid.

Ví dụ:

    columnsPercent(25, 25, 25, 25)

Grid:

    Column 0   Column 1   Column 2   Column 3

    ┌─────────┬─────────┬─────────┬─────────┐
    │         │         │         │         │
    ├─────────┼─────────┼─────────┼─────────┤
    │         │         │         │         │
    ├─────────┼─────────┼─────────┼─────────┤
    │         │         │         │         │
    └─────────┴─────────┴─────────┴─────────┘

Nếu cell:

    colSpan(2)

thì chiếm:

    Column 0
    Column 1

Nếu:

    rowSpan(2)

thì chiếm:

    Row 0
    Row 1

Nếu:

    colSpan(2)
    rowSpan(2)

thì chiếm:

    Row 0, Column 0
    Row 0, Column 1
    Row 1, Column 0
    Row 1, Column 1

---

# 66. drawTable - Luồng xử lý

    drawTable(table)
            │
            ▼
    Validate table
            │
            ▼
    Calculate column widths
            │
            ▼
    Create occupancy grid
            │
            ▼
    Loop rows
            │
            ▼
    Loop cells
            │
            ▼
    Read colSpan / rowSpan
            │
            ▼
    Find free position
            │
            ▼
    Calculate X
            │
            ▼
    Calculate Y
            │
            ▼
    Calculate Width
            │
            ▼
    Calculate Height
            │
            ▼
    setBounds()
            │
            ▼
    markOccupied()
            │
            ▼
    drawCell()
            │
            ▼
    Update currentY

---

# 67. Những giới hạn hiện tại

Phiên bản hiện tại đã hỗ trợ:

- Text
- TextAt
- TextInline
- NewLine
- Line
- Rectangle
- Border
- Image
- Checkbox
- Radio
- Signature
- Stamp
- Watermark
- Page Number
- Page Management
- Table
- Absolute Column Width
- Percentage Column Width
- Multiple Elements
- ColSpan
- RowSpan

Các chức năng nâng cao có thể phát triển tiếp:

- Auto Row Height
- Text Wrapping
- Auto Cell Height
- Page Break
- Table Page Break
- Repeat Header
- RowSpan kết hợp page break
- ColSpan phức tạp
- Image auto-size
- Vertical text alignment
- Dynamic signature position
- Template PDF
- QR Code
- Barcode
- Number formatting
- Currency formatting
- Date formatting
- Conditional style
- Background color
- Border style
- Dashed border

---

# 68. Roadmap

Thứ tự phát triển đề xuất:

    Phase 1
        Basic Drawing
        Text
        Image
        Checkbox
        Radio

        ↓

    Phase 2
        PdfTable
        PdfRow
        PdfCell
        PdfElement

        ↓

    Phase 3
        Column Width
        columnsPercent()

        ↓

    Phase 4
        colSpan
        rowSpan
        Occupancy Grid

        ↓

    Phase 5
        Text Wrapping
        Auto Row Height

        ↓

    Phase 6
        Page Break
        Repeat Header

        ↓

    Phase 7
        Signature
        Stamp
        Template

        ↓

    Phase 8
        Insurance-specific components

---

# 69. Insurance Use Case

Module có thể được sử dụng để render:

    Insurance Policy
        │
        ├── Customer Information
        │
        ├── Policy Information
        │
        ├── Product Information
        │
        ├── Coverage
        │
        ├── Premium
        │
        ├── Payment
        │
        ├── Terms & Conditions
        │
        └── Signature / Stamp

Ví dụ:

    Customer
    ├── Name
    ├── Address
    └── ID

    Policy
    ├── Policy No
    ├── Product
    ├── Effective Date
    ├── Expiry Date
    └── Status

    Premium
    ├── Gross Premium
    ├── Tax
    └── Total Premium

---

# 70. Nguyên tắc thiết kế cuối cùng

Module nên giữ nguyên nguyên tắc:

    Simple API
        +
    Flexible API
        +
    Reusable Rendering Engine

Simple:

    table.newRow()
            .addText(...)
            .addText(...);

Flexible:

    PdfCell cell = new PdfCell(...);

    cell.addElement(...);
    cell.addElement(...);

    cell.colSpan(2);
    cell.rowSpan(2);

    table.startRow()
            .addCell(cell);

Renderer:

    PdfDraw
        ↓
    Grid / Occupancy
        ↓
    Cell Bounds
        ↓
    drawCell()

Mục tiêu cuối cùng là business code chỉ cần mô tả:

    "PDF cần hiển thị gì"

còn Pdf Rendering Module sẽ xử lý:

    "Hiển thị nó ở đâu và như thế nào".