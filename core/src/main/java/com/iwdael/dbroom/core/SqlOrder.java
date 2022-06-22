package com.iwdael.dbroom.core;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
public abstract class SqlOrder<CONVERT, SOURCE, FIELD, TARGET> extends SqlUnion<CONVERT, SOURCE, FIELD, TARGET> {
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    public SqlOrder(SqlUnit<CONVERT, SOURCE, FIELD, TARGET, ?> where) {
        super(where);
    }

}
