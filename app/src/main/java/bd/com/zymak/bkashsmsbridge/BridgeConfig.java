package bd.com.zymak.bkashsmsbridge;

import android.content.Context;
import android.content.SharedPreferences;

public final class BridgeConfig {
    public static final String PREFS = "zymak_bkash_sms_bridge";
    public static final String KEY_ENABLED = "enabled";
    public static final String KEY_WEBHOOK_URL = "webhook_url";
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_ALLOWED_SENDERS = "allowed_senders";
    public static final String KEY_LAST_STATUS = "last_status";

    private BridgeConfig() {}

    public static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean isSenderAllowed(String sender, String csv) {
        if (sender == null) return false;
        String normalizedSender = sender.trim().toLowerCase();
        if (normalizedSender.isEmpty()) return false;
        for (String part : csv.split(",")) {
            if (normalizedSender.equals(part.trim().toLowerCase())) return true;
        }
        return false;
    }
}
