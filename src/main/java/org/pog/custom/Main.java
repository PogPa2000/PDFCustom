package org.pog.custom;

import java.io.IOException;

public class Main {
    /**
     *
     * Ý tưởng: Tạo ra 1 lib vẽ pdf dựa trên table làm layout và vẽ nội dung pdf. có thể vẽ
     * đọc lập trong môi trường đa luồng
     * <p>
     * 1: PdfDraw: Đây la class chính chịu trách nhiệm vẽ table thêm nội dung(text, image, chữ ký, con dấu, checkbox,
     * radio, đánh số trang, vẽ khung, watermarsk).
     * 2: PdfConfig: Chứa các giá trị cần setup ban đầu cho 1 file Pdf(cỡ chữ, kiểu chữ, khổ giấy, lề, khoản cách giữa
     * các dòng và các cốt ) những giá trị trong cofig sẽ áp dụng chung cho toàn bộ file pdf nếu không có can thiệp
     * trực tiếp.
     * 3: PdfCellStyle: Class chứa các thuộc tính quy định style của 1 cell trong table(dùng trong trường hơpj muốn
     * thay đổi style của 1 cell bất kỳ trong layout)
     * 4: PdfFont: Class chứa các Font có sẵn để tránh việc tạo lại font(sửa dụng enum) bao gồm font chữ và font size
     *
     */


    public static void main(String[] args) {

        try {

            // =========================================================
            // 1. CONFIG
            // =========================================================

            PdfConfig config = PdfConfig.builder()
                    .pageSize(PdfConfig.PageSize.A4)
//                    .orientation(PdfConfig.Orientation.PORTRAIT)

//                    // Margin
//                    .margin(40)
//
//                    // Font
//                    .defaultFont(PdfFont.TIMES_NEW_ROMAN)
//
//                    // Font size
//                    .defaultFontSize(10)
//
//                    // Line spacing
//                    .lineSpacing(1.2f)

                    .build();


            // =========================================================
            // 2. CREATE PDF
            // =========================================================

            PdfDraw pdf = new PdfDraw(config);


            // =========================================================
            // 3. TEXT
            // =========================================================

            pdf.text("DEMO PDF RENDERING MODULE");

            pdf.newLine();

            pdf.text("Đây là nội dung text thông thường.");

            pdf.newLine();

            pdf.text("Hệ thống bảo hiểm - Policy Issuance");

            pdf.newLine();
            pdf.newLine();


            // =========================================================
            // 4. TEXT INLINE
            // =========================================================

            pdf.textInline("Policy No: ");
            pdf.textInline("POL-2026-000001");

            pdf.newLine();

            pdf.textInline("Premium: ");
            pdf.textInline("100,000,000 VND");

            pdf.newLine();
            pdf.newLine();


            // =========================================================
            // 5. DRAW LINE
            // =========================================================

            pdf.line(
                    40,
                    680,
                    555,
                    680
            );


            // =========================================================
            // 6. DRAW RECTANGLE
            // =========================================================

            pdf.rectangle(
                    40,
                    620,
                    515,
                    40
            );


            // =========================================================
            // 7. DRAW BORDER
            // =========================================================

            pdf.border(
                    40,
                    550,
                    515,
                    50
            );


            // =========================================================
            // 8. CHECKBOX DIRECT
            // =========================================================

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


            // =========================================================
            // 9. RADIO DIRECT
            // =========================================================

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


            // =========================================================
            // 10. WATERMARK
            // =========================================================

            pdf.watermark(
                    "CONFIDENTIAL",
                    45
            );


            // =========================================================
            // 11. PAGE NUMBER
            // =========================================================

            pdf.pageNumber();


            // =========================================================
            // 12. NEW PAGE
            // =========================================================

            pdf.addPage();


            // =========================================================
            // 13. TABLE
            // =========================================================

            PdfTable table = pdf.table()
                    .width(pdf.getContentWidth())
                    .columnsPercent(50, 50);


            // ---------------------------------------------------------
            // TABLE HEADER
            // ---------------------------------------------------------

            PdfCell cell = new PdfCell(PdfCellStyle.builder().build());
            cell.addElement(
                    new PdfTextElement(
                            "THÔNG TIN HỢP ĐỒNG",
                            PdfCellStyle.header()
                    )
            );
            cell.colSpan(2);
            table.newRow()
                    .addCell(cell);

            table.newRow()
                    .addText(
                            "Loại",
                            PdfCellStyle.header()
                    )
                    .addText(
                            "Nội dung",
                            PdfCellStyle.header()
                    );


            // ---------------------------------------------------------
            // TEXT
            // ---------------------------------------------------------

            table.newRow()
                    .addText("Text")
                    .addText("Nội dung text trong table");


            // ---------------------------------------------------------
            // CHECKBOX - LEFT
            // ---------------------------------------------------------

            table.newRow()
                    .addText("Checkbox LEFT")
                    .addCheckbox(
                            "Đã chọn",
                            true,
                            12,
                            PdfCheckboxElement.Position.LEFT
                    );


            // ---------------------------------------------------------
            // CHECKBOX - RIGHT
            // ---------------------------------------------------------

            table.newRow()
                    .addText("Checkbox RIGHT")
                    .addCheckbox(
                            "Chọn",
                            false,
                            12,
                            PdfCheckboxElement.Position.RIGHT
                    );


            // ---------------------------------------------------------
            // RADIO - LEFT
            // ---------------------------------------------------------

            table.newRow()
                    .addText("Radio LEFT")
                    .addRadio(
                            "Nam",
                            true,
                            6,
                            PdfRadioElement.Position.LEFT
                    );


            // ---------------------------------------------------------
            // RADIO - RIGHT
            // ---------------------------------------------------------

            table.newRow()
                    .addText("Radio RIGHT")
                    .addRadio(
                            "Nữ",
                            false,
                            6,
                            PdfRadioElement.Position.RIGHT
                    );


            // ---------------------------------------------------------
            // NUMBER
            // ---------------------------------------------------------

            table.newRow()
                    .addText("Number")
                    .addNumber(
                            1,
                            "Điều khoản bảo hiểm"
                    );


            table.newRow()
                    .addText("Number")
                    .addNumber(
                            2,
                            "Quyền lợi bảo hiểm"
                    );


            table.newRow()
                    .addText("Number")
                    .addNumber(
                            3,
                            "Điều kiện bảo hiểm"
                    );


            // =========================================================
            // 14. IMAGE
            // =========================================================
            //
            // Sửa đường dẫn thành file image thực tế trên máy.
            //
            // Ví dụ:
            // D:/back_end_java/image/logo.png
            //
            // =========================================================

            String imagePath =
                    "D:/back_end_java/image/logo.png";


//            table.newRow()
//                    .addText("Image")
//                    .addImage(
//                            imagePath,
//                            100,
//                            50
//                    );


            // =========================================================
            // 15. DRAW TABLE
            // =========================================================

            pdf.drawTable(table);


            // =========================================================
            // 16. DIRECT IMAGE
            // =========================================================

//            pdf.image(
//                    imagePath,
//                    40,
//                    300,
//                    120,
//                    60
//            );


            // =========================================================
            // 17. SIGNATURE
            // =========================================================
            //
            // Sửa đường dẫn thành chữ ký thật.
            //
            // =========================================================

            String signaturePath =
                    "D:/back_end_java/image/signature.png";


//            pdf.signature(
//                    signaturePath,
//                    350,
//                    280,
//                    120,
//                    60
//            );


            // =========================================================
            // 18. STAMP
            // =========================================================

            String stampPath =
                    "D:/back_end_java/image/stamp.png";


//            pdf.stamp(
//                    stampPath,
//                    400,
//                    200,
//                    100,
//                    100
//            );


            // =========================================================
            // 19. MORE DIRECT TEXT
            // =========================================================

            pdf.textAt(
                    "Người yêu cầu bảo hiểm",
                    350,
                    260
            );


            // =========================================================
            // 20. PAGE NUMBER
            // =========================================================

            pdf.pageNumber();


            // =========================================================
            // 21. SAVE
            // =========================================================

            String output =
                    "D:/back_end_java/output/pdf-demo"+System.currentTimeMillis()+".pdf";

            pdf.save(output);


            // =========================================================
            // 22. CLOSE
            // =========================================================

            pdf.close();


            System.out.println(
                    "PDF created successfully:"
            );

            System.out.println(output);


        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}
