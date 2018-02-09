package com.github.common.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 文件导出. 在 Controller 中调用 */
public final class ExportUtil {

    /**
     * 导出 txt 格式文件! 在 Controller 中调用!
     *
     * @param name     导出的文件名(不带后缀)
     * @param content  文件内容
     */
    public static void exportTxt(String name, String content, HttpServletResponse response) throws IOException {
        // 导出的文件名
        String fileName = encodeName(name) + ".txt";

        response.setContentType("application/octet-stream; charset=utf-8");
        // 导出 txt
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.getOutputStream().write(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 导出 csv 格式文件! 在 Controller 中调用!
     *
     * @param name     导出的文件名(不带后缀)
     * @param titleMap 标题(key 为英文, value 为标题内容)
     * @param dataList 导出的数据(数组中的每个 object 都是一行, object 中的属性名与标题中的 key 相对)
     */
    public static void exportCsv(String name, LinkedHashMap<String, String> titleMap,
                                 List<?> dataList, HttpServletResponse response) throws IOException {
        // 导出的文件名
        String fileName = encodeName(name) + ".csv";

        response.setContentType("application/octet-stream; charset=utf-8");
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        // 没有数据或没有标题, 返回一个内容为空的文件
        String content = U.EMPTY;
        if (A.isNotEmpty(titleMap) && A.isNotEmpty(dataList)) {
            // csv 用英文逗号(,)隔开列, 用换行(\n)隔开行, 内容中包含了逗号的需要用双引号包裹, 若内容中包含了双引号则需要用两个双引号表示.
            StringBuilder sbd = new StringBuilder();
            int i = 0;
            for (String title : titleMap.values()) {
                sbd.append(handleCsvContent(title));
                i++;
                if (i != titleMap.size()) {
                    sbd.append(",");
                }
            }
            if (A.isNotEmpty(dataList)) {
                for (Object data : dataList) {
                    if (sbd.length() > 0) {
                        sbd.append("\n");
                    }
                    i = 0;
                    for (String title : titleMap.keySet()) {
                        sbd.append(handleCsvContent(U.getField(data, title)));
                        i++;
                        if (i != titleMap.size()) {
                            sbd.append(",");
                        }
                    }
                }
            }
            content = sbd.toString();
        }
        response.getOutputStream().write(content.getBytes(StandardCharsets.UTF_8));
    }
    private static String handleCsvContent(String content) {
        return "\"" + content.replace("\"", "\"\"") + "\"";
    }

    /**
     * 导出 2003 excel 文件! 在 Controller 中调用!
     *
     * @param name     导出的文件名(不带后缀)
     * @param titleMap 标题(key 为英文, value 为标题内容)
     * @param dataList 导出的数据(数组中的每个 object 都是一行, object 中的属性名与标题中的 key 相对)
     */
    public static void export03Excel(String name, LinkedHashMap<String, String> titleMap,
                                     List<?> dataList, HttpServletResponse response) throws IOException {
        exportExcel(false, name, titleMap, dataList, response);
    }

    /**
     * 导出 2007 excel 文件! 在 Controller 中调用!
     *
     * @param name     导出的文件名(不带后缀)
     * @param titleMap 标题(key 为英文, value 为标题内容)
     * @param dataList 导出的数据(数组中的每个 object 都是一行, object 中的属性名与标题中的 key 相对)
     */
    public static void export07Excel(String name, LinkedHashMap<String, String> titleMap,
                                     List<?> dataList, HttpServletResponse response) throws IOException {
        exportExcel(true, name, titleMap, dataList, response);
    }

    private static void exportExcel(boolean excel07, String name, LinkedHashMap<String, String> titleMap,
                                    List<?> dataList, HttpServletResponse response) throws IOException {
        // 导出的文件名
        String fileName = encodeName(name) + "." + (excel07 ? "xlsx" : "xls");

        response.setContentType("application/octet-stream; charset=utf-8");
        response.setContentType("text/xls");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        ExportExcel.handle(excel07, titleMap, A.linkedMaps(name, dataList)).write(response.getOutputStream());
    }

    private static String encodeName(String name) {
        String fileName = name + "-" + (new SimpleDateFormat("yyMMdd-HHmmss").format(new Date()));
        String userAgent = RequestUtils.userAgent();
        if (U.isNotBlank(userAgent) && userAgent.contains("Mozilla")) {
            // Chrome, Firefox, Safari etc...
            return new String(fileName.getBytes(), StandardCharsets.ISO_8859_1);
        } else {
            return U.urlEncode(fileName);
        }
    }


    private static final class ExportExcel {

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
        private static Workbook handle(boolean excel07, LinkedHashMap<String, String> titleMap,
                                      LinkedHashMap<String, List<?>> dataMap) {
            // 声明一个工作薄. HSSFWorkbook 是 Office 2003 的版本, XSSFWorkbook 是 2007
            Workbook workbook = excel07 ? new XSSFWorkbook() : new HSSFWorkbook();
            // 没有数据, 或者没有标题, 都直接返回
            if (A.isEmpty(dataMap) || A.isEmpty(titleMap)) {
                return workbook;
            }

            // 头样式
            CellStyle headStyle = createHeadStyle(workbook);
            // 内容样式
            CellStyle contentStyle = createContentStyle(workbook);

            // sheet
            Sheet sheet;
            // 行
            Row row;
            // 列
            Cell cell;
            // 表格数        行索引     列索引      数据起始索引  数据结束索引
            int sheetCount, rowIndex, cellIndex, fromIndex, toIndex;
            //  大小
            int size;
            //      数据
            List<?> excelList;

            for (Map.Entry<String, List<?>> entry : dataMap.entrySet()) {
                // 当前 sheet 的数据
                excelList = entry.getValue();
                if (A.isNotEmpty(excelList)) {
                    // 一个 sheet 数据过多 excel 处理会出错, 分多个 sheet
                    size = excelList.size();
                    sheetCount = ((size % EXCEL_TOTAL == 0) ? (size / EXCEL_TOTAL) : (size / EXCEL_TOTAL + 1));
                    for (int i = 0; i < sheetCount; i++) {
                        // 构建 sheet, 带名字
                        sheet = workbook.createSheet(entry.getKey() + (sheetCount > 1 ? ("-" + (i + 1)) : U.EMPTY));

                        // 每个 sheet 的标题行
                        rowIndex = 0;
                        cellIndex = 0;
                        row = sheet.createRow(rowIndex);
                        row.setHeightInPoints(ROW_HEIGHT);
                        // 每个 sheet 的标题行
                        for (String header : titleMap.values()) {
                            // 宽度自适应
                            sheet.autoSizeColumn(cellIndex);
                            cell = row.createCell(cellIndex);
                            cell.setCellStyle(headStyle);
                            cell.setCellValue(U.getNil(header));
                            cellIndex++;
                        }
                        // 冻结第一行
                        sheet.createFreezePane(0, 1, 0, 1);

                        // 每个 sheet 除标题行以外的数据
                        fromIndex = EXCEL_TOTAL * i;
                        toIndex = (i + 1 == sheetCount) ? size : EXCEL_TOTAL;
                        for (Object data : excelList.subList(fromIndex, toIndex)) {
                            if (data != null) {
                                rowIndex++;
                                // 每行
                                row = sheet.createRow(rowIndex);
                                row.setHeightInPoints(ROW_HEIGHT);

                                cellIndex = 0;
                                for (String value : titleMap.keySet()) {
                                    // 每列
                                    cell = row.createCell(cellIndex);
                                    cell.setCellStyle(contentStyle);
                                    cell.setCellValue(U.getField(data, value));

                                    cellIndex++;
                                }
                            }
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
    }
}
