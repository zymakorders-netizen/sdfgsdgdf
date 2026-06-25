package bd.com.zymak.bkashsmsbridge;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BkashSmsParser {
    public static class ParsedSms {
        public String amount = "";
        public String payerPhone = "";
        public String reference = "";
        public String trxId = "";
        public String smsTime = "";
    }

    private BkashSmsParser() {}

    public static ParsedSms parse(String sms) {
        ParsedSms out = new ParsedSms();
        if (sms == null) sms = "";
        out.amount = match(sms, "(?i)received\\s+payment\\s+Tk\\s*([0-9,.]+)");
        out.payerPhone = normalizePhone(match(sms, "(?i)\\bfrom\\s+(?:\\+?88)?(0?1[0-9]{9})\\b"));
        out.reference = match(sms, "(?i)\\bRef\\s+(.+?)\\s*\\.\\s*Fee\\s+Tk").trim();
        out.trxId = match(sms, "(?i)\\bTrxID\\s+([A-Z0-9]+)").toUpperCase();
        out.smsTime = match(sms, "(?i)\\bat\\s+([0-9/\\- :]+)\\s*$").trim();
        return out;
    }

    private static String match(String input, String regex) {
        Matcher m = Pattern.compile(regex).matcher(input);
        return m.find() ? m.group(1) : "";
    }

    private static String normalizePhone(String raw) {
        if (raw == null) return "";
        String digits = raw.replaceAll("\\D+", "");
        if (digits.length() == 13 && digits.startsWith("880")) digits = "0" + digits.substring(3);
        if (digits.length() == 12 && digits.startsWith("88")) digits = digits.substring(2);
        if (digits.length() == 10 && digits.startsWith("1")) digits = "0" + digits;
        return digits.matches("01[0-9]{9}") ? digits : "";
    }
}
