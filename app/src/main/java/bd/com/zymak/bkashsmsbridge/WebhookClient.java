package bd.com.zymak.bkashsmsbridge;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class WebhookClient {
    private WebhookClient() {}

    public static void sendBkashSms(Context context, String sender, String rawSms, BkashSmsParser.ParsedSms parsed) {
        SharedPreferences prefs = BridgeConfig.prefs(context);
        String webhookUrl = prefs.getString(BridgeConfig.KEY_WEBHOOK_URL, "");
        String deviceId = prefs.getString(BridgeConfig.KEY_DEVICE_ID, "samsung_m31_shop_phone");
        if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
            saveStatus(context, "Webhook URL is empty. SMS not forwarded.");
            return;
        }
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                JSONObject json = new JSONObject();
                json.put("source", "android_sms_bridge");
                json.put("device_id", deviceId);
                json.put("sender", sender == null ? "" : sender);
                json.put("raw_sms", rawSms == null ? "" : rawSms);
                json.put("amount", parsed.amount);
                json.put("payer_phone", parsed.payerPhone);
                json.put("reference", parsed.reference);
                json.put("trx_id", parsed.trxId);
                json.put("sms_received_at", parsed.smsTime);

                URL url = new URL(webhookUrl.trim());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(15000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                byte[] body = json.toString().getBytes("UTF-8");
                conn.setFixedLengthStreamingMode(body.length);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body);
                }
                int code = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(code >= 200 && code < 400 ? conn.getInputStream() : conn.getErrorStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                saveStatus(context, "HTTP " + code + ": " + response.toString());
            } catch (Exception e) {
                saveStatus(context, "Send failed: " + e.getMessage());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private static void saveStatus(Context context, String status) {
        BridgeConfig.prefs(context).edit().putString(BridgeConfig.KEY_LAST_STATUS, status).apply();
    }
}
