// Generated code from Butter Knife. Do not modify!
package com.speed.user.activities.login;

import android.view.View;
import android.widget.RadioButton;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.speed.user.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class IntroActivity_ViewBinding implements Unbinder {
  private IntroActivity target;

  @UiThread
  public IntroActivity_ViewBinding(IntroActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public IntroActivity_ViewBinding(IntroActivity target, View source) {
    this.target = target;

    target.rdEng = Utils.findRequiredViewAsType(source, R.id.rdEng, "field 'rdEng'", RadioButton.class);
    target.rdArb = Utils.findRequiredViewAsType(source, R.id.rdArb, "field 'rdArb'", RadioButton.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    IntroActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rdEng = null;
    target.rdArb = null;
  }
}
