//package com.iwdael.dbroom.core;
//
///**
// * @author : iwdael
// * @mail : iwdael@outlook.com
// * @project : https://github.com/iwdael/DbRoom
// */
//public final class Condition<T, Q> {
//    private final T target;
//    private final CallBack<Where<T, ?, Q>> whereCallBack;
//    private final ConditionCreator<T, Q> conditionCreator;
//
//    public Condition(T target, CallBack<Where<T, ?, Q>> whereCallBack, ConditionCreator<T, Q> conditionCreator) {
//        this.target = target;
//        this.whereCallBack = whereCallBack;
//        this.conditionCreator = conditionCreator;
//    }
//
//    public <F> Where<T, F, Q> where(Column<F> column) {
//        return new Where<T, F, Q>(target, column, whereCallBack, conditionCreator);
//    }
//}
