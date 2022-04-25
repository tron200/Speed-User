// Generated code from Butter Knife. Do not modify!
package com.speed.user.activities.login;

import android.view.View;
import android.widget.EditText;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.speed.user.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LoginActivity_ViewBinding implements Unbinder {
  private LoginActivity target;

  private View view7f0a00e4;

  private View view7f0a00e3;

  private View view7f0a0443;

  private View view7f0a0439;

  private View view7f0a00e5;

  @UiThread
  public LoginActivity_ViewBinding(LoginActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public LoginActivity_ViewBinding(final LoginActivity target, View source) {
    this.target = target;

    View view;
    target.etEmail = Utils.findRequiredViewAsType(source, R.id.etEmail, "field 'etEmail'", EditText.class);
    target.etPassword = Utils.findRequiredViewAsType(source, R.id.etPassword, "field 'etPassword'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btnGoogle, "method 'btnGoogleClick'");
    view7f0a00e4 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnGoogleClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.btnFb, "method 'btnFbClick'");
    view7f0a00e3 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnFbClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.txtSignUp, "method 'txtSignUpClick'");
    view7f0a0443 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.txtSignUpClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.txtForget, "method 'txtForgetClick'");
    view7f0a0439 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.txtForgetClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.btnLogin, "method 'btnLoginClick'");
    view7f0a00e5 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnLoginClick();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    LoginActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.etEmail = null;
    target.etPassword = null;

    view7f0a00e4.setOnClickListener(null);
    view7f0a00e4 = null;
    view7f0a00e3.setOnClickListener(null);
    view7f0a00e3 = null;
    view7f0a0443.setOnClickListener(null);
    view7f0a0443 = null;
    view7f0a0439.setOnClickListener(null);
    view7f0a0439 = null;
    view7f0a00e5.setOnClickListener(null);
    view7f0a00e5 = null;
  }
}
