package com.iwdael.dbroom.core;

import org.jetbrains.annotations.NotNull;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
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
}
