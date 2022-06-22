package com.iwdael.dbroom.core;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class SqlUnitBasicByte<CONVERT, SOURCE, TARGET, MAPPER> extends SqlUnit<CONVERT, SOURCE, Byte, TARGET, MAPPER> {

    public SqlUnitBasicByte(SOURCE target, Column<Byte> column, CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> callBack, SqlCreator<CONVERT, SOURCE, TARGET> builder, SelectionCreator<SOURCE, TARGET> selectionCreator, SelectionCreator<SqlUnit<CONVERT, SOURCE, Byte, TARGET, MAPPER>, MAPPER> operatorCreator) {
        super(target, column, callBack, builder, selectionCreator, operatorCreator);
    }

    public MAPPER equal(byte value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER unequal(byte value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = UNEQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER greater(byte value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = GREATER;
        return operatorCreator.create(this);
    }

    public MAPPER greaterEqual(byte value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = GREATER_EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER less(byte value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = LESS;
        return operatorCreator.create(this);
    }

    public MAPPER lessEqual(byte value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = LESS_EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER between(byte value1, byte value2) {
        this.value.add(value1);
        this.value.add(value2);
        this.call.call(this);
        this.assign = BETWEEN;
        return operatorCreator.create(this);
    }

    public MAPPER in(byte... values) {
        for (byte value : values) {
            this.value.add(value);
        }
        this.call.call(this);
        this.assign = IN;
        return operatorCreator.create(this);
    }
}
