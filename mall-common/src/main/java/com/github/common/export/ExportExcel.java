package com.github.common.export;

import com.github.common.util.A;
import com.github.common.util.U;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

final class ExportExcel {

    // excel 2003 的最大行数是 65536 行, 2007 开始的版本是 1048576 行.
    // excel 2003 的最大列数是 256 列, 2007 及以上版本是 16384 列.
    /** excel 单个 sheet 能处理的最大行数 (2 << 15) - 1 */
    private static final int EXCEL_TOTAL = 65535;

    /** 标题行的字体大小 */
    private static final short HEAD_FONT_SIZE = 11;

    /** 其他内容的字体大小 */
    private static final short FONT_SIZE = 10;

    /** 行高. 要比上面的字体大一点! */
    private static final short ROW_HEIGHT = 15;

    /**
     * 返回一个 excel 工作簿
     *
     * @param excel07  是否返回 microsoft excel 2007 的版本
     * @param titleMap 属性名为 key, 对应的标题为 value, 为了处理显示时的顺序, 因此使用 linkedHashMap
     * @param dataMap  以「sheet 名」为 key, 对应的数据为 value(每一行的数据为一个 Object)
     */
    static Workbook handle(boolean excel07, LinkedHashMap<String, String> titleMap,
                                   LinkedHashMap<String, List<?>> dataMap) {
        // 声明一个工作薄. HSSFWorkbook 是 Office 2003 的版本, XSSFWorkbook 是 2007
        Workbook workbook = excel07 ? new XSSFWorkbook() : new HSSFWorkbook();
        // 没有标题直接返回
        if (A.isEmpty(titleMap)) {
            return workbook;
        }
        // 如果数据为空, 构建一个空字典(确保导出的文件有标题头)
        if (dataMap == null) {
            dataMap = new LinkedHashMap<>();
        }

        // 头样式
        CellStyle headStyle = createHeadStyle(workbook);
        // 内容样式
        CellStyle contentStyle = createContentStyle(workbook);
        // 数字样式
        CellStyle numberStyle = createNumberStyle(workbook);

        Sheet sheet;
        //  行
        Row row;
        //   列
        Cell cell;
        //  表格数       行索引      列索引
        int sheetCount, rowIndex, cellIndex;
        //  数据总条数  数据起始索引  数据结束索引
        int size, fromIndex, toIndex;
        //      数据
        List<?> dataList;
        List<?> sheetList;

        // 标题头, 这里跟数据中的属性相对应
        Set<String> titleKey = titleMap.keySet();
        // 标题显示名
        Collection<String> titleValue = titleMap.values();
        // 列数量
        int titleLen = titleMap.size();
        String cellData;
        String[] titleValues;
        DataFormat dataFormat = workbook.createDataFormat();

        for (Map.Entry<String, List<?>> entry : dataMap.entrySet()) {
            // 当前 sheet 的数据
            dataList = entry.getValue();
            size = A.isEmpty(dataList) ? 0 : dataList.size();
            // 一个 sheet 数据过多 excel 处理会出错, 分多个 sheet
            sheetCount = ((size % EXCEL_TOTAL == 0) ? (size / EXCEL_TOTAL) : (size / EXCEL_TOTAL + 1));
            if (sheetCount == 0) {
                // 如果没有记录时也至少构建一个(确保导出的文件有标题头)
                sheetCount = 1;
            }
            for (int i = 0; i < sheetCount; i++) {
                // 构建 sheet, 带名字
                sheet = workbook.createSheet(entry.getKey() + (sheetCount > 1 ? ("-" + (i + 1)) : U.EMPTY));

                // 每个 sheet 的标题行
                rowIndex = 0;
                cellIndex = 0;
                row = sheet.createRow(rowIndex);
                row.setHeightInPoints(ROW_HEIGHT);
                // 每个 sheet 的标题行
                for (String header : titleValue) {
                    cell = row.createCell(cellIndex);
                    cell.setCellStyle(headStyle);
                    cell.setCellValue(U.getNil(header));
                    if (size == 0) {
                        sheet.autoSizeColumn(cellIndex, true);
                    }
                    cellIndex++;
                }
                // 冻结第一行
                sheet.createFreezePane(0, 1, 0, 1);

                if (size > 0) {
                    if (sheetCount > 1) {
                        // 每个 sheet 除标题行以外的数据
                        fromIndex = EXCEL_TOTAL * i;
                        toIndex = (i + 1 == sheetCount) ? size : EXCEL_TOTAL;
                        sheetList = dataList.subList(fromIndex, toIndex);
                    } else {
                        sheetList = dataList;
                    }
                    for (Object data : sheetList) {
                        if (data != null) {
                            rowIndex++;
                            // 每行
                            row = sheet.createRow(rowIndex);
                            row.setHeightInPoints(ROW_HEIGHT);

                            cellIndex = 0;
                            for (String title : titleKey) {
                                // 每列
                                cell = row.createCell(cellIndex);
                                cellIndex++;

                                cellData = U.getField(data, title);

                                titleValues = titleMap.get(title).split("\\|");
                                if (NumberUtils.isNumber(cellData)) {
                                    cell.setCellType(CellType.NUMERIC);
                                    cell.setCellValue(NumberUtils.toDouble(cellData));
                                    if (titleValues.length > 1) {
                                        numberStyle.setDataFormat(dataFormat.getFormat(titleValues[1]));
                                    }
                                    cell.setCellStyle(numberStyle);
                                } else {
                                    cell.setCellType(CellType.STRING);
                                    cell.setCellValue(cellData);
                                    if (titleValues.length > 1) {
                                        contentStyle.setDataFormat(dataFormat.getFormat(titleValues[1]));
                                    }
                                    cell.setCellStyle(contentStyle);
                                }
                            }
                        }
                    }
                    // 让列的宽度自适应
                    for (int j = 0; j < titleLen; j++) {
                        sheet.autoSizeColumn(j, true);
                    }
                }
            }
        }
        return workbook;
    }

    /** 头样式 */
    private static CellStyle createHeadStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 水平居左
        style.setAlignment(HorizontalAlignment.LEFT);
        // 垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        // 粗体
        font.setBold(true);
        font.setFontHeightInPoints(HEAD_FONT_SIZE);
        style.setFont(font);
        return style;
    }

    /** 内容样式 */
    private static CellStyle createContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setFontHeightInPoints(FONT_SIZE);
        style.setFont(font);
        return style;
    }

    /** 数字样式 */
    private static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setFontHeightInPoints(FONT_SIZE);
        style.setFont(font);
        return style;
    }
}
