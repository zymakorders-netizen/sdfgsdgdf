package bd.com.zymak.bkashsmsbridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) return;
        SharedPreferences prefs = BridgeConfig.prefs(context);
        if (!prefs.getBoolean(BridgeConfig.KEY_ENABLED, false)) return;
        String allowedSenders = prefs.getString(BridgeConfig.KEY_ALLOWED_SENDERS, "bKash,BKash,bkash,16247");
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (messages == null || messages.length == 0) return;
        String sender = messages[0].getDisplayOriginatingAddress();
        if (!BridgeConfig.isSenderAllowed(sender, allowedSenders)) return;
        StringBuilder body = new StringBuilder();
        for (SmsMessage msg : messages) {
            if (msg != null) body.append(msg.getMessageBody());
        }
        String rawSms = body.toString();
        if (!rawSms.toLowerCase().contains("received payment") || !rawSms.toLowerCase().contains("trxid")) {
            return;
        }
        BkashSmsParser.ParsedSms parsed = BkashSmsParser.parse(rawSms);
        WebhookClient.sendBkashSms(context, sender, rawSms, parsed);
    }
}
