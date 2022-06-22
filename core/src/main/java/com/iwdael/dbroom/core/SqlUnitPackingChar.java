package com.iwdael.dbroom.core;

import java.util.Arrays;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class SqlUnitPackingChar<CONVERT, SOURCE, TARGET, MAPPER> extends SqlUnit<CONVERT, SOURCE, Character, TARGET, MAPPER> {

    public SqlUnitPackingChar(SOURCE target, Column<Character> column, CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> callBack, SqlCreator<CONVERT, SOURCE, TARGET> builder, SelectionCreator<SOURCE, TARGET> selectionCreator, SelectionCreator<SqlUnit<CONVERT, SOURCE, Character, TARGET, MAPPER>, MAPPER> operatorCreator) {
        super(target, column, callBack, builder, selectionCreator, operatorCreator);
    }

    public MAPPER equal(Character value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER unequal(Character value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = UNEQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER greater(Character value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = GREATER;
        return operatorCreator.create(this);
    }

    public MAPPER greaterEqual(Character value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = GREATER_EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER less(Character value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = LESS;
        return operatorCreator.create(this);
    }

    public MAPPER lessEqual(Character value) {
        this.value.add(value);
        this.call.call(this);
        this.assign = LESS_EQUAL;
        return operatorCreator.create(this);
    }

    public MAPPER between(Character value1, Character value2) {
        this.value.add(value1);
        this.value.add(value2);
        this.call.call(this);
        this.assign = BETWEEN;
        return operatorCreator.create(this);
    }

    public MAPPER in(Character... value) {
        this.value.addAll(Arrays.asList(value));
        this.call.call(this);
        this.assign = IN;
        return operatorCreator.create(this);
    }
}
