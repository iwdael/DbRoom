//package com.iwdael.dbroom.core;
//
///**
// * @author : iwdael
// * @mail : iwdael@outlook.com
// * @project : https://github.com/iwdael/DbRoom
// */
//public class Condition2<T, R, Q> {
//    private final Operator<T, R, Q> operator;
//
//    public Condition2(Operator<T, R, Q> operator) {
//        this.operator = operator;
//    }
//
//
//    public <C> Where<T, C, Q> where(Column<C> column) {
//        return new Where<>(operator.where.target, column, operator.where.callBack, operator.where.conditionCreator);
//    }
//}
