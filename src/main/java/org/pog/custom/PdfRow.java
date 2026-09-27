package org.pog.custom;

import java.util.ArrayList;
import java.util.List;

public class PdfRow {
    private final List<PdfCell> cells = new ArrayList<>();

    public void addCell(PdfCell cell) {
        cells.add(cell);
    }

    public List<PdfCell> getCells() {
        return cells;
    }
}
