package bd.com.zymak.bkashsmsbridge;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText webhookUrl, deviceId, allowedSenders, testSms;
    private CheckBox enabled;
    private TextView lastStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = BridgeConfig.prefs(this);
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 28, 28, 28);
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("Zymak bKash SMS Bridge");
        title.setTextSize(22);
        root.addView(title);

        enabled = new CheckBox(this);
        enabled.setText("Enable SMS forwarding");
        enabled.setChecked(prefs.getBoolean(BridgeConfig.KEY_ENABLED, false));
        root.addView(enabled);

        webhookUrl = input(root, "Plugin Webhook URL", prefs.getString(BridgeConfig.KEY_WEBHOOK_URL, ""));
        deviceId = input(root, "Device ID", prefs.getString(BridgeConfig.KEY_DEVICE_ID, "samsung_m31_shop_phone"));
        allowedSenders = input(root, "Allowed SMS senders", prefs.getString(BridgeConfig.KEY_ALLOWED_SENDERS, "bKash,BKash,bkash,16247"));

        Button save = new Button(this);
        save.setText("Save Settings");
        save.setOnClickListener(v -> {
            prefs.edit()
                    .putBoolean(BridgeConfig.KEY_ENABLED, enabled.isChecked())
                    .putString(BridgeConfig.KEY_WEBHOOK_URL, webhookUrl.getText().toString().trim())
                    .putString(BridgeConfig.KEY_DEVICE_ID, deviceId.getText().toString().trim())
                    .putString(BridgeConfig.KEY_ALLOWED_SENDERS, allowedSenders.getText().toString().trim())
                    .apply();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        });
        root.addView(save);

        Button perms = new Button(this);
        perms.setText("Request SMS Permissions");
        perms.setOnClickListener(v -> requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 100));
        root.addView(perms);

        TextView testLabel = new TextView(this);
        testLabel.setText("Test SMS text");
        root.addView(testLabel);
        testSms = new EditText(this);
        testSms.setMinLines(4);
        testSms.setText("You have received payment Tk 150.00 from 01314762176. Ref 23154. Fee Tk 0.00. Balance Tk 150.00. TrxID DF34V2HBXU at 03/06/2026 12:33");
        root.addView(testSms);

        Button sendTest = new Button(this);
        sendTest.setText("Send Test To Plugin");
        sendTest.setOnClickListener(v -> {
            prefs.edit()
                    .putString(BridgeConfig.KEY_WEBHOOK_URL, webhookUrl.getText().toString().trim())
                    .putString(BridgeConfig.KEY_DEVICE_ID, deviceId.getText().toString().trim())
                    .putString(BridgeConfig.KEY_ALLOWED_SENDERS, allowedSenders.getText().toString().trim())
                    .apply();
            BkashSmsParser.ParsedSms parsed = BkashSmsParser.parse(testSms.getText().toString());
            WebhookClient.sendBkashSms(this, "bKash", testSms.getText().toString(), parsed);
            Toast.makeText(this, "Test sent. Refresh status in a few seconds.", Toast.LENGTH_LONG).show();
        });
        root.addView(sendTest);

        Button refresh = new Button(this);
        refresh.setText("Refresh Last Status");
        refresh.setOnClickListener(v -> lastStatus.setText(prefs.getString(BridgeConfig.KEY_LAST_STATUS, "No status yet")));
        root.addView(refresh);

        lastStatus = new TextView(this);
        lastStatus.setText(prefs.getString(BridgeConfig.KEY_LAST_STATUS, "No status yet"));
        lastStatus.setTextIsSelectable(true);
        root.addView(lastStatus);

        setContentView(scroll);
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 100);
        }
    }

    private EditText input(LinearLayout root, String label, String value) {
        TextView tv = new TextView(this);
        tv.setText(label);
        root.addView(tv);
        EditText input = new EditText(this);
        input.setSingleLine(false);
        input.setText(value == null ? "" : value);
        root.addView(input);
        return input;
    }
}
