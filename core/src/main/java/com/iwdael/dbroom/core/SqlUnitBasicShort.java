package com.iwdael.dbroom.core;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class SqlUnitBasicShort<CONVERT, SOURCE, TARGET, MAPPER> extends SqlUnit<CONVERT, SOURCE, Short, TARGET, MAPPER> {

    public SqlUnitBasicShort(SOURCE target, Column<Short> column, CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> callBack, SqlCreator<CONVERT, SOURCE, TARGET> builder, SelectionCreator<SOURCE, TARGET> selectionCreator, SelectionCreator<SqlUnit<CONVERT, SOURCE, Short, TARGET, MAPPER>, MAPPER> operatorCreator) {
        super(target, column, callBack, builder, selectionCreator, operatorCreator);
    }

    public MAPPER equal(short value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER unequal(short value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = UNEQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER greater(short value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = GREATER;
        return operatorCreator.create(this);
    }

    public MAPPER greaterEqual(short value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = GREATER_EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER less(short value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = LESS;
        return operatorCreator.create(this);
    }

    public MAPPER lessEqual(short value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = LESS_EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER between(short value1, short value2) {
        this.value.add(value1);
        this.value.add(value2);
        this.call.call(this);
        this.assign = BETWEEN;
        return operatorCreator.create(this);
    }

    public MAPPER in(short... values) {
        for (short value : values) {
            this.value.add(value);
        }
        this.call.call(this);
        this.assign = IN;
        return operatorCreator.create(this);
    }
}
