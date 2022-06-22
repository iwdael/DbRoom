package com.iwdael.dbroom.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public class SqlUnit<CONVERT, SOURCE, FIELD, TARGET, MAPPER> {
    protected static final String EQUAL = "=";

    protected static final String UNEQUAL = "<>";

    protected static final String GREATER = ">";

    protected static final String GREATER_EQUAL = ">=";

    protected static final String LESS = "<";

    protected static final String LESS_EQUAL = "<=";

    protected static final String BETWEEN = "BETWEEN";

    protected static final String LIKE = "LIKE";

    protected static final String IN = "IN";

    protected final SOURCE target;

    protected final CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> call;

    protected final SelectionCreator<SOURCE, TARGET> selectionCreator;

    protected final SelectionCreator<SqlUnit<CONVERT, SOURCE, FIELD, TARGET, MAPPER>, MAPPER> operatorCreator;

    protected final Column<FIELD> column;

    protected final List<FIELD> value = new ArrayList<>();

    protected final SqlCreator<CONVERT, SOURCE, TARGET> builder;

    protected String assign = null;

    protected SqlUnion<CONVERT, SOURCE, FIELD, TARGET> operator = null;

    protected SqlUnion<CONVERT, SOURCE, FIELD, TARGET> order = null;

    protected SqlUnion<CONVERT, SOURCE, FIELD, TARGET> limit = null;

    protected SqlUnion<CONVERT, SOURCE, FIELD, TARGET> offset = null;

    public SqlUnit(SOURCE target, Column<FIELD> column, CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> call, SqlCreator<CONVERT, SOURCE, TARGET> builder, SelectionCreator<SOURCE, TARGET> selectionCreator, SelectionCreator<SqlUnit<CONVERT, SOURCE, FIELD, TARGET, MAPPER>, MAPPER> operatorCreator) {
        this.column = column;
        this.target = target;
        this.call = call;
        this.builder = builder;
        this.selectionCreator = selectionCreator;
        this.operatorCreator = operatorCreator;
    }
}
