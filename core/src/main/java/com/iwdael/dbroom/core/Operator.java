package com.iwdael.dbroom.core;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public class Operator<N, T, R, Q> extends SqlUnion<N, T, R, Q> {
    protected static final String AND = "AND";

    protected static final String OR = "OR";

    public Operator(SqlUnit<N, T, R, Q, ?> where) {
        super(where);
    }

    public final N and() {
        operator = AND;
        where.operator = this;
        return where.builder.build(where.target, where.call, where.builder, where.selectionCreator);
    }

    public final N or() {
        operator = OR;
        where.operator = this;
        return where.builder.build(where.target, where.call, where.builder, where.selectionCreator);
    }

}
