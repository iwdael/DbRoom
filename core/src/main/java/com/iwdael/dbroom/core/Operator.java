package com.iwdael.dbroom.core;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public class Operator<CONVERT, SOURCE, FIELD, TARGET> extends SqlUnion<CONVERT, SOURCE, FIELD, TARGET> {
    protected static final String AND = "AND";

    protected static final String OR = "OR";

    public Operator(SqlUnit<CONVERT, SOURCE, FIELD, TARGET, ?> where) {
        super(where);
    }

    public final CONVERT and() {
        operator = AND;
        where.operator = this;
        return where.builder.build(where.target, where.call, where.builder, where.selectionCreator);
    }

    public final CONVERT or() {
        operator = OR;
        where.operator = this;
        return where.builder.build(where.target, where.call, where.builder, where.selectionCreator);
    }

    public TARGET build() {
        return Utils.makeTarget(where);
    }

}
