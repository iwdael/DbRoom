//package com.iwdael.dbroom.core;
//
///**
// * @author : iwdael
// * @mail : iwdael@outlook.com
// * @project : https://github.com/iwdael/DbRoom
// */
//public class Operator<T, R, Q> {
//    public static final String AND = "AND";
//    public static final String OR = "OR";
//    protected final Where<T, R, Q> where;
//    protected String operator;
//
//    protected Operator(Where<T, R, Q> where) {
//        this.where = where;
//    }
//
//    public Q build() {
//        return where.conditionCreator.create(where.target);
//    }
//
//    public Condition2<T, R, Q> and() {
//        this.operator = AND;
//        where.next = this;
//        return new Condition2<>(this);
//    }
//
//    public Condition2<T, R, Q> or() {
//        this.operator = OR;
//        where.next = this;
//        return new Condition2<>(this);
//    }
//}