package pw.chew.chanserv.util;

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
        try {
            // Wait for PK rate limiting
            final long now = Instant.now().toEpochMilli();
            final long compare = lastPkLookup.toEpochMilli()+500;
            if (compare > now) {
                Thread.sleep(compare-now);
            }

            // Read data to string
            final URL url = new URL(BASE_URL+"messages/"+messageId);
            final InputStream stream = url.openStream();
            final byte[] bytes = stream.readAllBytes();
            final String json = new String(bytes, Charset.defaultCharset());

            lastPkLookup = Instant.now();

            // Quick and dirty check; PK's API will return this on failure.
            return !json.equals("{\"message\":\"Message not found.\",\"code\":20006}");
        } catch (InterruptedException | IOException e) {
            return false;
        }
    }
}
