// Generated code from Butter Knife. Do not modify!
package com.speed.user.fragments;

import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.speed.user.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class UserMapFragment_ViewBinding implements Unbinder {
  private UserMapFragment target;

  @UiThread
  public UserMapFragment_ViewBinding(UserMapFragment target, View source) {
    this.target = target;

    target.llFlow = Utils.findRequiredViewAsType(source, R.id.llFlow, "field 'llFlow'", FrameLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    UserMapFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.llFlow = null;
  }
}
