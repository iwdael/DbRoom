package com.iwdael.dbroom.annotations;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public @interface UseRoomNotifier {
    boolean generate() default false;
}
