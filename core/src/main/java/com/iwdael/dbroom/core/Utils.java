package com.iwdael.dbroom.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class Utils {
    protected static final String DELETE = "DELETE";

    protected static final String SELECT = "SELECT";

    protected static final String UPDATE = "UPDATE";

    protected static final String INSERT = "INSERT";

    protected static final String WHERE = "WHERE";

    protected static final String FROM = "FROM";

    protected static final String INTO = "INTO";

    protected static final String VALUES = "VALUES";

    protected static final String SPACE = " ";

    protected static final String SET = "SET";

    protected static final String AND = "AND";

    protected static final String ORDER = "ORDER";

    protected static final String BY = "BY";

    protected static final String LIMIT = "LIMIT";

    protected static final String OFFSET = "OFFSET";

    public static String toFinderSelection(String tableName, Object[] columns, List<SqlUnit<?, ?, ?, ?, ?>> wheres) {
        StringBuilder builder = new StringBuilder();
        builder.append(SELECT).append(SPACE);
        int count = columns.length;
        for (int index = 0; index < count; index++) {
            builder.append(columns[index]).append(SPACE);
            if (index < count - 1) builder.append(",").append(SPACE);
        }
        if (columns.length == 0) {
            builder.append("*").append(SPACE);
        }
        builder.append(FROM).append(SPACE).append(tableName).append(SPACE);
        if (!wheres.isEmpty()) {
            builder.append(toWhere(wheres));
        }
        return builder.toString();
    }

    public static String toDeleterSelection(String tableName, Object[] columns, List<SqlUnit<?, ?, ?, ?, ?>> wheres) {
        StringBuilder builder = new StringBuilder();
        builder.append(DELETE).append(SPACE);
        int count = columns.length;
        for (int index = 0; index < count; index++) {
            builder.append(columns[index]).append(SPACE);
            if (index < count - 1) builder.append(",").append(SPACE);
        }
        builder.append(FROM).append(SPACE).append(tableName).append(SPACE);
        if (!wheres.isEmpty()) {
            builder.append(toWhere(wheres));
        }
        return builder.toString();
    }

    public static String toUpdaterSelection(String tableName, Map<Column<?>, Object> columns, List<SqlUnit<?, ?, ?, ?, ?>> wheres) {
        StringBuilder builder = new StringBuilder();
        builder.append(UPDATE).append(SPACE).append(tableName).append(SPACE).append(SET).append(SPACE);
        Iterator<Column<?>> iterator = columns.keySet().iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next()).append(SPACE).append("=").append(SPACE).append("?").append(SPACE);
            if (iterator.hasNext()) builder.append(",").append(SPACE);
        }
        if (!wheres.isEmpty()) {
            builder.append(toWhere(wheres));
        }
        return builder.toString();
    }

    public static String toInserterSelection(String tableName, Map<Column<?>, Object> columns) {
        StringBuilder builder = new StringBuilder();
        builder.append(INSERT).append(SPACE).append(INTO).append(SPACE).append(tableName).append(SPACE).append("(");
        Iterator<Column<?>> columnIterator = columns.keySet().iterator();
        while (columnIterator.hasNext()) {
            builder.append(columnIterator.next());
            if (columnIterator.hasNext()) builder.append(SPACE).append(",").append(SPACE);
            else builder.append(")").append(SPACE);
        }
        builder.append(VALUES).append(SPACE).append("(");
        Iterator<Column<?>> columnIterator2 = columns.keySet().iterator();
        while (columnIterator2.hasNext()) {
            columnIterator2.next();
            builder.append("?");
            if (columnIterator2.hasNext()) builder.append(SPACE).append(",").append(SPACE);
            else builder.append(")");
        }
        return builder.toString();
    }

    public static Object[] toBindArgs(List<SqlUnit<?, ?, ?, ?, ?>> wheres) {
        List<Object> bindArgs = new ArrayList<>();
        for (SqlUnit<?, ?, ?, ?, ?> where : wheres) {
            bindArgs.addAll(where.value);
        }
        return bindArgs.toArray();
    }

    public static Object[] toBindArgs(Map<Column<?>, Object> columns) {
        List<Object> bindArgs = new ArrayList<>();
        for (Column<?> column : columns.keySet()) {
            bindArgs.add(columns.get(column));
        }
        return bindArgs.toArray();
    }

    private static boolean containWhere(List<SqlUnit<?, ?, ?, ?, ?>> units) {
        List<SqlUnit<?, ?, ?, ?, ?>> sqlUnits = new ArrayList<>();
        for (SqlUnit<?, ?, ?, ?, ?> unit : units) {
            if (unit.getClass() != SqlUnit.class) {
                sqlUnits.add(unit);
            }
        }
        return !sqlUnits.isEmpty();
    }

    private static String toWhere(List<SqlUnit<?, ?, ?, ?, ?>> units) {
        StringBuilder builder = new StringBuilder();
        if (containWhere(units)) {
            builder.append(WHERE).append(SPACE);
        }
        for (SqlUnit<?, ?, ?, ?, ?> unit : units) {
            if (unit.getClass() == SqlUnit.class) {
            } else if (Objects.equals(unit.assign, SqlUnit.BETWEEN)) {
                builder.append(unit.column).append(SPACE).append(unit.assign).append(SPACE).append('?').append(SPACE).append(AND).append(SPACE).append('?').append(SPACE);
            } else if (Objects.equals(unit.assign, SqlUnit.IN)) {
                builder.append(unit.column).append(SPACE).append(unit.assign).append(SPACE).append('(');
                int count = unit.value.size();
                for (int index = 0; index < count; index++) {
                    builder.append("?");
                    if (index != count - 1) {
                        builder.append(SPACE).append(",").append(SPACE);
                    }
                }
                builder.append(')').append(SPACE);
            } else {
                builder.append(unit.column).append(SPACE).append(unit.assign).append(SPACE).append('?').append(SPACE);
            }
            if (unit.operator != null) {
                builder.append(unit.operator.operator).append(SPACE);
            }
            if (unit.order != null) {
                SqlOrder order = (SqlOrder) unit.order;
                builder.append(ORDER).append(SPACE)
                        .append(BY).append(SPACE)
                        .append(unit.column.toString()).append(SPACE)
                        .append(order.operator).append(SPACE);
            }
            if (unit.limit != null) {
                SqlLimit limit = (SqlLimit) unit.limit;
                builder.append(LIMIT).append(SPACE).append(limit.limit).append(SPACE);
            }
            if (unit.offset != null) {
                SqlOffset offset = (SqlOffset) unit.offset;
                builder.append(OFFSET).append(SPACE).append(offset.offset);
            }
        }
        return builder.toString().trim();
    }

    public static <C, T> List<T> collectionConvert(List<C> sources, SelectionCreator<C, T> selectionCreator) {
        List<T> collection = new ArrayList<>(sources.size());
        for (C source : sources) {
            collection.add(selectionCreator.create(source));
        }
        return collection;
    }

    public static <TARGET, SOURCE, CONVERT, FIELD> void setWhereLimit(SqlUnit<CONVERT, SOURCE, FIELD, TARGET, ?> where, SqlUnion<CONVERT, SOURCE, FIELD, TARGET> limit) {
        where.limit = limit;
    }

    public static <FIELD, SOURCE, CONVERT, TARGET> void setWhereOffset(SqlUnit<CONVERT, SOURCE, FIELD, TARGET, ?> where, SqlUnion<CONVERT, SOURCE, FIELD, TARGET> offset) {
        where.offset = offset;
    }

    public static <FIELD, SOURCE, CONVERT, TARGET> void setWhereOrder(SqlUnit<CONVERT, SOURCE, FIELD, TARGET, ?> where, SqlUnion<CONVERT, SOURCE, FIELD, TARGET> order) {
        where.order = order;
    }

    public static <FIELD, SOURCE, CONVERT, TARGET> TARGET makeTarget(SqlUnit<CONVERT, SOURCE, FIELD, TARGET, ?> where) {
        return where.selectionCreator.create(where.target);
    }


    public static <SOURCE, TARGET, CONVERT, FIELD> SqlUnit<CONVERT,SOURCE, FIELD,TARGET,?> createOrder(SqlUnit  where,  Column<FIELD> column) {
        SqlUnit ext = new SqlUnit(where.target, column, where.call, where.builder, where.selectionCreator, null);
        where.call.call(ext);
        return ext;
    }
}
