package com.speed.user.helper;

public interface URLHelper {

    String BASE = "https://speed-limo.com/";
    String REDIRECT_URL = BASE;
    String REDIRECT_SHARE_URL = "http://maps.google.com/maps?q=loc:";
    String APP_URL = "https://play.google.com/store/apps/details?id=com.pinkcar.user";
    int client_id = 2;

    String client_secret = "WifS1rMi3LvuorP1G2UdtKZairUNSH2iMqrKivPf";
    String STRIPE_TOKEN = "pk_test_qWAKEoT8qG3a7CP41i3jZT8300aq3poee3";
    String image_url_signature = BASE + "public/";
    String CURRENT_TRIP = BASE + "api/user/trips/current";
    String login = BASE + "oauth/token";
    String register = BASE + "api/user/signup";
    String UserProfile = BASE + "api/user/details";
    String UseProfileUpdate = BASE + "api/user/update/profile";
    String getUserProfileUrl = BASE + "api/user/details";
    String GET_SERVICE_LIST_API = BASE + "api/user/services";
    String REQUEST_STATUS_CHECK_API = BASE + "api/user/request/check";
    String ESTIMATED_FARE_DETAILS_API = BASE + "api/user/estimated/fare";
    String SEND_REQUEST_API = BASE + "api/user/send/request";
    String VALID_ZONE = BASE + "api/user/getvalidzone";
    String CANCEL_REQUEST_API = BASE + "api/user/cancel/request";
    String PAY_NOW_API = BASE + "api/user/payment";
    String RATE_PROVIDER_API = BASE + "api/user/rate/provider";
    String GET_USERREVIEW = BASE + "api/user/review";

    String DELETE_CARD_FROM_ACCOUNT_API = BASE + "api/user/card/destory";
    String GET_HISTORY_API = BASE + "api/user/trips";
    String GET_HISTORY_DETAILS_API = BASE + "api/user/trip/details";
    String addCardUrl = BASE + "api/user/add/money";
    String COUPON_LIST_API = BASE + "api/user/promocodes";
    String ADD_COUPON_API = BASE + "api/user/promocode/add";
    String CHANGE_PASSWORD_API = BASE + "api/user/change/password";
    String UPCOMING_TRIP_DETAILS = BASE + "api/user/upcoming/trip/details";
    String UPCOMING_TRIPS = BASE + "api/user/upcoming/trips";
    String GET_PROVIDERS_LIST_API = BASE + "api/user/show/providers";
    String FORGET_PASSWORD = BASE + "api/user/forgot/password";
    String RESET_PASSWORD = BASE + "api/user/reset/password";

    String FACEBOOK_LOGIN = BASE + "api/user/auth/facebook";
    String GOOGLE_LOGIN = BASE + "api/user/auth/google";
    String LOGOUT = BASE + "api/user/logout";
    String HELP = BASE + "api/user/help";
    String SAVE_LOCATION = BASE + "api/user/createDefaultLocation";

    //    Safaricom Payment
    String PAYMENT_TOKEN = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";


    String ADD_CARD_TO_ACCOUNT_API = BASE + "api/user/card";
    String CARD_PAYMENT_LIST = BASE + "api/user/card";
    String GET_PAYMENT_CONFIRMATION = "api/user/payment/now?total_amount=";

    String ChatGetMessage = BASE + "api/user/firebase/getChat?request_id=";
    String NOTIFICATION_URL = BASE + "api/user/notification";
}
