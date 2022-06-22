package com.iwdael.dbroom.core;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class SqlUnitBasicBoolean<CONVERT, SOURCE, TARGET, MAPPER> extends SqlUnit<CONVERT, SOURCE, Boolean, TARGET, MAPPER> {

    public SqlUnitBasicBoolean(SOURCE target, Column<Boolean> column, CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> callBack, SqlCreator<CONVERT, SOURCE, TARGET> builder, SelectionCreator<SOURCE, TARGET> selectionCreator, SelectionCreator<SqlUnit<CONVERT, SOURCE, Boolean, TARGET, MAPPER>, MAPPER> operatorCreator) {
        super(target, column, callBack, builder, selectionCreator, operatorCreator);
    }

    public MAPPER equal(boolean value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER unequal(boolean value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = UNEQUAL;
        return operatorCreator.create(this);
    }
}
