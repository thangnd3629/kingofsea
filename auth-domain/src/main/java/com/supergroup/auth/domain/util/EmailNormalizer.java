package com.supergroup.auth.domain.util;

import java.util.HashMap;
import java.util.Map;

public class EmailNormalizer {

    public static Map<String, String> mailAlias = new HashMap<>();

    static {
        mailAlias.put("googlemail.com", "gmail.com");
    }

    /**
     * References:
     * https://en.wikipedia.org/wiki/Email_address
     * https://github.com/johno/normalize-email
     */
    public static String normalize(String email) {
        // trim
        email = email.trim();
        // lowercase
        email = email.toLowerCase();
        // split username and domain
        String[] split = email.split("@");
        var username = split[0];
        username = normalizeUsername(username);
        var domain = split[1];
        domain = normalizeDomain(domain);
        return username + "@" + domain;
    }

    public static String normalizeUsername(String username) {
        // remove dot
        username = username.replace(".", "");
        // cut plus
        if (username.contains("+")) {
            username = username.substring(0, username.indexOf("+"));
        }
        return username;
    }

    public static String normalizeDomain(String domain) {
        if (mailAlias.containsKey(domain)) {
            domain = mailAlias.get(domain);
        }
        return domain;
    }

}
