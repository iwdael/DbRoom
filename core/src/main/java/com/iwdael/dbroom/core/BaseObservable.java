// author : iwdael
// e-mail : iwdael@outlook.com
package com.iwdael.dbroom.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.databinding.Observable;


/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public abstract class BaseObservable implements Observable {
  private static volatile Handler handler;

  @Override
  public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
  }

  @Override
  public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
  }

  private static void checkAndInit() {
    if (handler == null) {
      synchronized (BaseObservable.class) {
        if (handler == null) {
          HandlerThread thread = new HandlerThread("room-update");
          thread.start();
          handler = new Handler(thread.getLooper());
        }
      }
    }
  }

  protected void notifyRoom(RoomNotifier notifier) {
    checkAndInit();
    Log.v("DbRoom","notifier");
    handler.post(notifier::notifier);
  }

  public abstract void notifyPropertyChanged(int fieldId);

  public abstract void notifyPropertiesChange();

  public interface RoomNotifier {
    void notifier();
  }
}
