package com.github.search.util;

import com.github.common.util.U;

public final class Sqls {

    public static String countSql(String pageSql, String incrementColumn, String param) {
        StringBuilder querySql = new StringBuilder();
        querySql.append(pageSql.trim().replaceFirst("(?i)select (.*?) from (.*?)", "SELECT COUNT(*) FROM $2"));
        appendWhere(incrementColumn, param, querySql);

        return querySql.toString();
    }

    private static void appendWhere(String incrementColumn, String param, StringBuilder querySql) {
        if (U.isNotBlank(param)) {
            querySql.append(querySql.toString().toUpperCase().contains(" WHERE ") ? " AND" : " WHERE");
            querySql.append(String.format(" `%s` > '%s'", incrementColumn, param));
        }
    }

    public static String querySql(String pageSql, String incrementColumn, String param, int page, int limit) {
        StringBuilder querySql = new StringBuilder();
        querySql.append(pageSql);
        appendWhere(incrementColumn, param, querySql);

        querySql.append(" ORDER BY").append(String.format(" `%s` ASC", incrementColumn));
        querySql.append(" LIMIT ").append(page * limit).append(", ").append(limit);
        return querySql.toString();
    }
}
