package com.iwdael.dbroom.core;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public interface SqlCreator<CONVERT, SOURCE, TARGET> {
    CONVERT build(SOURCE target, CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> callback, SqlCreator<CONVERT, SOURCE, TARGET> builder, SelectionCreator<SOURCE, TARGET> selectionCreator);
}
