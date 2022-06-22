package com.iwdael.dbroom.core;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public abstract class SqlNode<CONVERT, SOURCE, TARGET> {
    protected final SOURCE target;

    protected final CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> call;

    protected final SelectionCreator<SOURCE, TARGET> selectionCreator;

    protected final SqlCreator<CONVERT, SOURCE, TARGET> builder;

    public SqlNode(SOURCE target, CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> call, SqlCreator<CONVERT, SOURCE, TARGET> builder, SelectionCreator<SOURCE, TARGET> selectionCreator) {
        this.target = target;
        this.call = call;
        this.builder = builder;
        this.selectionCreator = selectionCreator;
    }
}
