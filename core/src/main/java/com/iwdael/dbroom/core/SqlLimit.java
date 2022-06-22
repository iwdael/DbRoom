package com.iwdael.dbroom.core;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
public abstract class SqlLimit<CONVERT, SOURCE, FIELD, TARGET> extends SqlUnion<CONVERT, SOURCE, FIELD, TARGET> {
    protected long limit = 1;

    public SqlLimit(SqlUnit<CONVERT, SOURCE, FIELD, TARGET, ?> where) {
        super(where);
    }
}
