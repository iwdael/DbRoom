package com.iwdael.dbroom.core;

import java.util.Arrays;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class SqlUnitPackingLong<CONVERT, SOURCE, TARGET, MAPPER> extends SqlUnit<CONVERT, SOURCE, Long, TARGET, MAPPER> {

    public SqlUnitPackingLong(SOURCE target, Column<Long> column, CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> callBack, SqlCreator<CONVERT, SOURCE, TARGET> builder, SelectionCreator<SOURCE, TARGET> selectionCreator, SelectionCreator<SqlUnit<CONVERT, SOURCE, Long, TARGET, MAPPER>, MAPPER> operatorCreator) {
        super(target, column, callBack, builder, selectionCreator, operatorCreator);
    }

    public MAPPER equal(Long value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER unequal(Long value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = UNEQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER greater(Long value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = GREATER;
        return operatorCreator.create(this);
    }

    public MAPPER greaterEqual(Long value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = GREATER_EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER less(Long value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = LESS;
        return operatorCreator.create(this);
    }

    public MAPPER lessEqual(Long value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = LESS_EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER between(Long value1, Long value2) {
        this.value.add(value1);
        this.value.add(value2);
        this.call.call(this);
        this.assign = BETWEEN;
        return operatorCreator.create(this);
    }

    public MAPPER in(Long... value) {
        this.value.addAll(Arrays.asList(value));
        this.call.call(this);
        this.assign = IN;
        return operatorCreator.create(this);
    }
}
