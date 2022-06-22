package com.iwdael.dbroom.core;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
public abstract class SqlOffset<CONVERT, SOURCE, FIELD, TARGET> extends SqlUnion<CONVERT, SOURCE, FIELD, TARGET> {
    protected long offset;

    public SqlOffset(SqlUnit<CONVERT, SOURCE, FIELD, TARGET, ?> where) {
        super(where);
    }
}
