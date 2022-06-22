package com.iwdael.dbroom.core;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public class SqlUnion<CONVERT, SOURCE, FIELD, TARGET> {
    protected final SqlUnit<CONVERT, SOURCE, FIELD, TARGET, ?> where;
    protected String operator = null;

    public SqlUnion(SqlUnit<CONVERT, SOURCE, FIELD, TARGET, ?> where) {
        this.where = where;
    }

}
