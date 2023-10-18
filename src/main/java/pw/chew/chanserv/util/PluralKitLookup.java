package pw.chew.chanserv.util;

import org.json.JSONObject;
import pw.chew.chewbotcca.util.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Instant;

/**
 * Handles basic lookup for PK's API
 * @author Oliver-makes-code
 * */
public class PluralKitLookup {
    private static final String BASE_URL = "https://api.pluralkit.me/v2/";

    // Precatuion for rate limiting
    private static Instant lastPkLookup = Instant.EPOCH;

    // Checks if a message has been proxied by PK
    public static boolean isMessageProxied(String messageId) {
        return getMessage(messageId) != null;
    }

    // Gets a message from PK
    public static JSONObject getMessage(String messageId) {
        try {
            // Wait for PK rate limiting
            final long now = Instant.now().toEpochMilli();
            final long compare = lastPkLookup.toEpochMilli()+500;
            if (compare > now) {
                Thread.sleep(compare-now);
            }

            // Read data
            final String data = RestClient.get(BASE_URL+"messages/"+messageId);
            final JSONObject json = new JSONObject(data);

            lastPkLookup = Instant.now();

            // Key won't be here on failure
            if (json.has("system")) return json;
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }
}
