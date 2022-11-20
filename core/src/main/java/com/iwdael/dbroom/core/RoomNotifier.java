// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import android.os.Handler;
import android.os.HandlerThread;

public final class RoomNotifier {
  private static volatile Handler handler;

  private static void checkAndInit() {
    if (handler == null) {
      synchronized (RoomNotifier.class) {
        if (handler == null) {
          HandlerThread thread = new HandlerThread("room-update");
          thread.start();
          handler = new Handler(thread.getLooper());
        }
      }
    }
  }

  public static void notifyRoom(Notifier notifier) {
    checkAndInit();
    handler.post(notifier::notifier);
  }

  public interface Notifier {
    void notifier();
  }
}
