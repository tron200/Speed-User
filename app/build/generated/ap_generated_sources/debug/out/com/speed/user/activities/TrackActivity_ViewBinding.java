// Generated code from Butter Knife. Do not modify!
package com.speed.user.activities;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.speed.user.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TrackActivity_ViewBinding implements Unbinder {
  private TrackActivity target;

  private View view7f0a043f;

  private View view7f0a01d9;

  private View view7f0a01ec;

  private View view7f0a01eb;

  private View view7f0a0277;

  private View view7f0a0271;

  private View view7f0a00de;

  private View view7f0a026a;

  private View view7f0a01e6;

  private View view7f0a01ea;

  private View view7f0a00df;

  private View view7f0a0265;

  private View view7f0a00e8;

  private View view7f0a026c;

  private View view7f0a01e8;

  private View view7f0a00ee;

  private View view7f0a00e1;

  private View view7f0a00dc;

  private View view7f0a00e0;

  private View view7f0a01dd;

  @UiThread
  public TrackActivity_ViewBinding(TrackActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public TrackActivity_ViewBinding(final TrackActivity target, View source) {
    this.target = target;

    View view;
    target.layoutdriverstatus = Utils.findRequiredViewAsType(source, R.id.layoutdriverstatus, "field 'layoutdriverstatus'", LinearLayout.class);
    target.driveraccepted = Utils.findRequiredViewAsType(source, R.id.driveraccepted, "field 'driveraccepted'", LinearLayout.class);
    target.txtdriveraccpted = Utils.findRequiredViewAsType(source, R.id.txtdriveraccpted, "field 'txtdriveraccpted'", TextView.class);
    target.driverArrived = Utils.findRequiredViewAsType(source, R.id.driverArrived, "field 'driverArrived'", LinearLayout.class);
    target.txtdriverArrived = Utils.findRequiredViewAsType(source, R.id.txtdriverArrived, "field 'txtdriverArrived'", TextView.class);
    target.imgarrived = Utils.findRequiredViewAsType(source, R.id.imgarrived, "field 'imgarrived'", ImageView.class);
    target.driverPicked = Utils.findRequiredViewAsType(source, R.id.driverPicked, "field 'driverPicked'", LinearLayout.class);
    target.txtdriverpicked = Utils.findRequiredViewAsType(source, R.id.txtdriverpicked, "field 'txtdriverpicked'", TextView.class);
    target.imgPicked = Utils.findRequiredViewAsType(source, R.id.imgPicked, "field 'imgPicked'", ImageView.class);
    target.driverCompleted = Utils.findRequiredViewAsType(source, R.id.driverCompleted, "field 'driverCompleted'", LinearLayout.class);
    target.txtdrivercompleted = Utils.findRequiredViewAsType(source, R.id.txtdrivercompleted, "field 'txtdrivercompleted'", TextView.class);
    target.imgDropped = Utils.findRequiredViewAsType(source, R.id.imgDropped, "field 'imgDropped'", ImageView.class);
    target.lnrFlow = Utils.findRequiredViewAsType(source, R.id.lnrFlow, "field 'lnrFlow'", LinearLayout.class);
    target.promoLayout = Utils.findRequiredViewAsType(source, R.id.promoLayout, "field 'promoLayout'", LinearLayout.class);
    target.lblDistancePrice = Utils.findRequiredViewAsType(source, R.id.lblDistancePrice, "field 'lblDistancePrice'", TextView.class);
    target.txtDiscount = Utils.findRequiredViewAsType(source, R.id.txtDiscount, "field 'txtDiscount'", TextView.class);
    view = Utils.findRequiredView(source, R.id.txtPickUpNotes, "field 'txtPickUpNotes' and method 'spcialiNotesClcik'");
    target.txtPickUpNotes = Utils.castView(view, R.id.txtPickUpNotes, "field 'txtPickUpNotes'", TextView.class);
    view7f0a043f = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.spcialiNotesClcik();
      }
    });
    view = Utils.findRequiredView(source, R.id.imgBack, "field 'imgBack' and method 'imgBackClick'");
    target.imgBack = Utils.castView(view, R.id.imgBack, "field 'imgBack'", ImageView.class);
    view7f0a01d9 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.imgBackClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.imgSos, "field 'imgSos' and method 'imgSosClick'");
    target.imgSos = Utils.castView(view, R.id.imgSos, "field 'imgSos'", ImageView.class);
    view7f0a01ec = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.imgSosClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.imgShareRide, "field 'imgShareRide' and method 'imgShareRideClick'");
    target.imgShareRide = Utils.castView(view, R.id.imgShareRide, "field 'imgShareRide'", ImageView.class);
    view7f0a01eb = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.imgShareRideClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.mapfocus, "field 'mapfocus' and method 'mapfocusClick'");
    target.mapfocus = Utils.castView(view, R.id.mapfocus, "field 'mapfocus'", ImageView.class);
    view7f0a0277 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.mapfocusClick();
      }
    });
    target.shadowBack = Utils.findRequiredViewAsType(source, R.id.shadowBack, "field 'shadowBack'", ImageView.class);
    view = Utils.findRequiredView(source, R.id.lnrWaitingForProviders, "field 'lnrWaitingForProviders' and method 'lnrWaitingForProvidersClick'");
    target.lnrWaitingForProviders = Utils.castView(view, R.id.lnrWaitingForProviders, "field 'lnrWaitingForProviders'", RelativeLayout.class);
    view7f0a0271 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.lnrWaitingForProvidersClick();
      }
    });
    target.lblNoMatch = Utils.findRequiredViewAsType(source, R.id.lblNoMatch, "field 'lblNoMatch'", TextView.class);
    view = Utils.findRequiredView(source, R.id.btnCancelRide, "field 'btnCancelRide' and method 'btnCancelRideClick'");
    target.btnCancelRide = Utils.castView(view, R.id.btnCancelRide, "field 'btnCancelRide'", Button.class);
    view7f0a00de = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnCancelRideClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.lnrProviderAccepted, "field 'lnrProviderAccepted' and method 'lnrProviderAcceptedClick'");
    target.lnrProviderAccepted = Utils.castView(view, R.id.lnrProviderAccepted, "field 'lnrProviderAccepted'", LinearLayout.class);
    view7f0a026a = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.lnrProviderAcceptedClick();
      }
    });
    target.AfterAcceptButtonLayout = Utils.findRequiredViewAsType(source, R.id.AfterAcceptButtonLayout, "field 'AfterAcceptButtonLayout'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.imgProvider, "field 'imgProvider' and method 'imgProviderClick'");
    target.imgProvider = Utils.castView(view, R.id.imgProvider, "field 'imgProvider'", CircleImageView.class);
    view7f0a01e6 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.imgProviderClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.imgServiceRequested, "field 'imgServiceRequested' and method 'serviceDetails'");
    target.imgServiceRequested = Utils.castView(view, R.id.imgServiceRequested, "field 'imgServiceRequested'", ImageView.class);
    view7f0a01ea = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.serviceDetails();
      }
    });
    target.lblProvider = Utils.findRequiredViewAsType(source, R.id.lblProvider, "field 'lblProvider'", TextView.class);
    target.lblSurgePrice = Utils.findRequiredViewAsType(source, R.id.lblSurgePrice, "field 'lblSurgePrice'", TextView.class);
    target.lblServiceRequested = Utils.findRequiredViewAsType(source, R.id.lblServiceRequested, "field 'lblServiceRequested'", TextView.class);
    target.lblModelNumber = Utils.findRequiredViewAsType(source, R.id.lblModelNumber, "field 'lblModelNumber'", TextView.class);
    target.ratingProvider = Utils.findRequiredViewAsType(source, R.id.ratingProvider, "field 'ratingProvider'", RatingBar.class);
    view = Utils.findRequiredView(source, R.id.btnCancelTrip, "field 'btnCancelTrip' and method 'btnCancelTripClick'");
    target.btnCancelTrip = Utils.castView(view, R.id.btnCancelTrip, "field 'btnCancelTrip'", TextView.class);
    view7f0a00df = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnCancelTripClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.lnrInvoice, "field 'lnrInvoice' and method 'lnrInvoiceClick'");
    target.lnrInvoice = Utils.castView(view, R.id.lnrInvoice, "field 'lnrInvoice'", LinearLayout.class);
    view7f0a0265 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.lnrInvoiceClick();
      }
    });
    target.lblBasePrice = Utils.findRequiredViewAsType(source, R.id.lblBasePrice, "field 'lblBasePrice'", TextView.class);
    target.lblExtraPrice = Utils.findRequiredViewAsType(source, R.id.lblExtraPrice, "field 'lblExtraPrice'", TextView.class);
    target.lblTaxPrice = Utils.findRequiredViewAsType(source, R.id.lblTaxPrice, "field 'lblTaxPrice'", TextView.class);
    target.lblTotalPrice = Utils.findRequiredViewAsType(source, R.id.lblTotalPrice, "field 'lblTotalPrice'", TextView.class);
    target.lblPaymentTypeInvoice = Utils.findRequiredViewAsType(source, R.id.lblPaymentTypeInvoice, "field 'lblPaymentTypeInvoice'", TextView.class);
    target.imgPaymentTypeInvoice = Utils.findRequiredViewAsType(source, R.id.imgPaymentTypeInvoice, "field 'imgPaymentTypeInvoice'", ImageView.class);
    view = Utils.findRequiredView(source, R.id.btnPayNow, "field 'btnPayNow' and method 'btnPayNowClick'");
    target.btnPayNow = Utils.castView(view, R.id.btnPayNow, "field 'btnPayNow'", Button.class);
    view7f0a00e8 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnPayNowClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.lnrRateProvider, "field 'lnrRateProvider' and method 'lnrRateProviderClick'");
    target.lnrRateProvider = Utils.castView(view, R.id.lnrRateProvider, "field 'lnrRateProvider'", LinearLayout.class);
    view7f0a026c = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.lnrRateProviderClick();
      }
    });
    target.lblProviderName = Utils.findRequiredViewAsType(source, R.id.lblProviderName, "field 'lblProviderName'", TextView.class);
    view = Utils.findRequiredView(source, R.id.imgProviderRate, "field 'imgProviderRate' and method 'imgProviderRateClick'");
    target.imgProviderRate = Utils.castView(view, R.id.imgProviderRate, "field 'imgProviderRate'", CircleImageView.class);
    view7f0a01e8 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.imgProviderRateClick();
      }
    });
    target.txtComments = Utils.findRequiredViewAsType(source, R.id.txtComments, "field 'txtComments'", EditText.class);
    target.ratingProviderRate = Utils.findRequiredViewAsType(source, R.id.ratingProviderRate, "field 'ratingProviderRate'", RatingBar.class);
    view = Utils.findRequiredView(source, R.id.btnSubmitReview, "field 'btnSubmitReview' and method 'btnSubmitReviewClick'");
    target.btnSubmitReview = Utils.castView(view, R.id.btnSubmitReview, "field 'btnSubmitReview'", Button.class);
    view7f0a00ee = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnSubmitReviewClick();
      }
    });
    target.rtlStaticMarker = Utils.findRequiredViewAsType(source, R.id.rtlStaticMarker, "field 'rtlStaticMarker'", RelativeLayout.class);
    target.imgDestination = Utils.findRequiredViewAsType(source, R.id.imgDestination, "field 'imgDestination'", ImageView.class);
    view = Utils.findRequiredView(source, R.id.btnDone, "field 'btnDone' and method 'btnDoneClick'");
    target.btnDone = Utils.castView(view, R.id.btnDone, "field 'btnDone'", Button.class);
    view7f0a00e1 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnDoneClick();
      }
    });
    target.booking_id = Utils.findRequiredViewAsType(source, R.id.booking_id, "field 'booking_id'", TextView.class);
    target.tvPaymentLabel = Utils.findRequiredViewAsType(source, R.id.tvPaymentLabel, "field 'tvPaymentLabel'", TextView.class);
    view = Utils.findRequiredView(source, R.id.btnCall, "field 'btnCall' and method 'callbtnCall'");
    target.btnCall = Utils.castView(view, R.id.btnCall, "field 'btnCall'", ImageButton.class);
    view7f0a00dc = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.callbtnCall();
      }
    });
    view = Utils.findRequiredView(source, R.id.btnChat, "field 'btnChat' and method 'callbtnChat'");
    target.btnChat = Utils.castView(view, R.id.btnChat, "field 'btnChat'", ImageButton.class);
    view7f0a00e0 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.callbtnChat();
      }
    });
    view = Utils.findRequiredView(source, R.id.imgEditDestination, "field 'imgEditDestination' and method 'changeDestination'");
    target.imgEditDestination = Utils.castView(view, R.id.imgEditDestination, "field 'imgEditDestination'", ImageView.class);
    view7f0a01dd = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.changeDestination();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    TrackActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.layoutdriverstatus = null;
    target.driveraccepted = null;
    target.txtdriveraccpted = null;
    target.driverArrived = null;
    target.txtdriverArrived = null;
    target.imgarrived = null;
    target.driverPicked = null;
    target.txtdriverpicked = null;
    target.imgPicked = null;
    target.driverCompleted = null;
    target.txtdrivercompleted = null;
    target.imgDropped = null;
    target.lnrFlow = null;
    target.promoLayout = null;
    target.lblDistancePrice = null;
    target.txtDiscount = null;
    target.txtPickUpNotes = null;
    target.imgBack = null;
    target.imgSos = null;
    target.imgShareRide = null;
    target.mapfocus = null;
    target.shadowBack = null;
    target.lnrWaitingForProviders = null;
    target.lblNoMatch = null;
    target.btnCancelRide = null;
    target.lnrProviderAccepted = null;
    target.AfterAcceptButtonLayout = null;
    target.imgProvider = null;
    target.imgServiceRequested = null;
    target.lblProvider = null;
    target.lblSurgePrice = null;
    target.lblServiceRequested = null;
    target.lblModelNumber = null;
    target.ratingProvider = null;
    target.btnCancelTrip = null;
    target.lnrInvoice = null;
    target.lblBasePrice = null;
    target.lblExtraPrice = null;
    target.lblTaxPrice = null;
    target.lblTotalPrice = null;
    target.lblPaymentTypeInvoice = null;
    target.imgPaymentTypeInvoice = null;
    target.btnPayNow = null;
    target.lnrRateProvider = null;
    target.lblProviderName = null;
    target.imgProviderRate = null;
    target.txtComments = null;
    target.ratingProviderRate = null;
    target.btnSubmitReview = null;
    target.rtlStaticMarker = null;
    target.imgDestination = null;
    target.btnDone = null;
    target.booking_id = null;
    target.tvPaymentLabel = null;
    target.btnCall = null;
    target.btnChat = null;
    target.imgEditDestination = null;

    view7f0a043f.setOnClickListener(null);
    view7f0a043f = null;
    view7f0a01d9.setOnClickListener(null);
    view7f0a01d9 = null;
    view7f0a01ec.setOnClickListener(null);
    view7f0a01ec = null;
    view7f0a01eb.setOnClickListener(null);
    view7f0a01eb = null;
    view7f0a0277.setOnClickListener(null);
    view7f0a0277 = null;
    view7f0a0271.setOnClickListener(null);
    view7f0a0271 = null;
    view7f0a00de.setOnClickListener(null);
    view7f0a00de = null;
    view7f0a026a.setOnClickListener(null);
    view7f0a026a = null;
    view7f0a01e6.setOnClickListener(null);
    view7f0a01e6 = null;
    view7f0a01ea.setOnClickListener(null);
    view7f0a01ea = null;
    view7f0a00df.setOnClickListener(null);
    view7f0a00df = null;
    view7f0a0265.setOnClickListener(null);
    view7f0a0265 = null;
    view7f0a00e8.setOnClickListener(null);
    view7f0a00e8 = null;
    view7f0a026c.setOnClickListener(null);
    view7f0a026c = null;
    view7f0a01e8.setOnClickListener(null);
    view7f0a01e8 = null;
    view7f0a00ee.setOnClickListener(null);
    view7f0a00ee = null;
    view7f0a00e1.setOnClickListener(null);
    view7f0a00e1 = null;
    view7f0a00dc.setOnClickListener(null);
    view7f0a00dc = null;
    view7f0a00e0.setOnClickListener(null);
    view7f0a00e0 = null;
    view7f0a01dd.setOnClickListener(null);
    view7f0a01dd = null;
  }
}
