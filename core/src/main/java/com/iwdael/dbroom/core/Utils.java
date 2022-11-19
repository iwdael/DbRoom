package com.iwdael.dbroom.core;

import static com.iwdael.dbroom.core.Constant.AND;
import static com.iwdael.dbroom.core.Constant.FROM;
import static com.iwdael.dbroom.core.Constant.SELECT;
import static com.iwdael.dbroom.core.Constant.SPACE;
import static com.iwdael.dbroom.core.Constant.WHERE;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public class Utils {
    private static final String classNameOfObservable = "com.iwdael.dbroom.RoomObservableCreator";


    @NotNull
    protected static BaseObservable createObserver(Object obj) {
        try {
            ObservableCreator creator = (ObservableCreator) Class.forName(classNameOfObservable).newInstance();
            return creator.create(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//
//    public static String toWhere(List<Where<?, ?, ?>> wheres) {
//        StringBuilder builder = new StringBuilder();
//        if (!wheres.isEmpty()) {
//            builder.append(WHERE).append(SPACE);
//        }
//        for (Where<?, ?, ?> where : wheres) {
//            if (Objects.equals(where.assign, Where.BETWEEN)) {
//                builder.append(where.column).append(SPACE)
//                        .append(where.assign).append(SPACE)
//                        .append('?').append(SPACE)
//                        .append(AND).append(SPACE)
//                        .append('?').append(SPACE);
//            } else if (Objects.equals(where.assign, Where.IN)) {
//                builder.append(where.column).append(SPACE)
//                        .append(where.assign).append(SPACE)
//                        .append('(');
//                int count = where.value.size();
//                for (int index = 0; index < count; index++) {
//                    builder.append("?");
//                    if (index != count - 1)
//                        builder.append(SPACE).append(",").append(SPACE);
//                }
//                builder.append(')').append(SPACE);
//            } else {
//                builder.append(where.column).append(SPACE)
//                        .append(where.assign).append(SPACE)
//                        .append('?').append(SPACE);
//            }
//            if (where.next != null) {
//                builder.append(where.next.operator).append(SPACE);
//            }
//        }
//        return builder.toString();
//    }
//
//    public static String toSelectCondition(String tableName, Object[] fields, List<Where<?, ?, ?>> wheres) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(SELECT).append(SPACE);
//        if (fields.length == 0) {
//            builder.append("*").append(SPACE);
//        } else {
//            for (Object field : fields) {
//                builder.append(field).append(SPACE);
//            }
//        }
//        builder.append(FROM).append(SPACE).append(tableName).append(SPACE);
//        return builder.append(toWhere(wheres)).toString();
//    }
//
//    public static Object[] toSelectValue(List<Where<?, ?, ?>> wheres) {
//        List<Object> objects = new ArrayList<>();
//        for (Where<?, ?, ?> where : wheres) {
//            objects.add(where.value);
//        }
//        return objects.toArray();
//    }
}
