# Zymak bKash SMS Bridge Android App

Private Android app for forwarding bKash payment received SMS messages to the Zymak Modern Orders Dashboard plugin.

## Important
- This app needs `RECEIVE_SMS`, `READ_SMS`, and `INTERNET` permission.
- Use as a private APK on your own shop phone. Publishing on Google Play with SMS permissions may require special approval.
- Disable battery optimization for this app on Samsung/Android settings for reliable background SMS receiving.

## Build APK
Open this folder in Android Studio, then use:

`Build > Build Bundle(s) / APK(s) > Build APK(s)`

The debug APK will be created inside `app/build/outputs/apk/debug/`.

## App setup
1. Install the updated WordPress plugin.
2. Go to `Dashboard Settings > bKash SMS Bridge`.
3. Enable the bridge.
4. Copy the Plugin Webhook URL.
5. Paste it into the Android app Webhook URL field.
6. Keep Device ID as `samsung_m31_shop_phone` or set the same value in plugin/app.
7. Save settings and allow SMS permission.

## Matching rule summary
The plugin auto-confirms only if:
- Sender is allowed.
- TrxID is unique.
- Amount equals the order delivery charge.
- Delivery charge is above TK 70.
- Reference contains the order number, or a phone number that uniquely matches one eligible order.
- Order status is Order Received or Order Verified.
