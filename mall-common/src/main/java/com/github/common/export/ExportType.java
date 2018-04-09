package com.github.common.export;

import com.github.common.util.U;

enum ExportType {

    Xls03, Xls07, Csv;

    boolean is03() {
        return this == Xls03;
    }
    boolean is07() {
        return this == Xls07;
    }
    boolean isExcel() {
        return is03() || is07();
    }

    boolean isCsv() {
        return this == Csv;
    }

    static ExportType to(String type) {
        if (U.isNotBlank(type)) {
            for (ExportType exportType : values()) {
                if (type.equalsIgnoreCase(exportType.name())) {
                    return exportType;
                }
            }
        }
        return Xls07;
    }
}
