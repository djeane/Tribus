package apptribus.com.tribus.util;

import java.util.regex.Pattern;

/**
 * Created by User on 12/15/2017.
 */

public class FindUrl {

    public static final Pattern urlPattern = Pattern.compile(
            /*"\\b(?:(?:(?:https?|ftp|file)://|www\\.|ftp\\.)[-A-Z0-9+&@#/%?=~_|$!:,.;]*[-A-Z0-9+&@#/%=~_|$]\n" +
                    "| ((?:mailto:)?[A-Z0-9._%+-]+@[A-Z0-9._%-]+\\.[A-Z]{2,4})\\b)\n" +
                    "|\"(?:(?:https?|ftp|file)://|www\\.|ftp\\.)[^\"\\r\\n]+\"?\n" +
                    "|'(?:(?:https?|ftp|file)://|www\\.|ftp\\.)[^'\\r\\n]+'?"*/
                    "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);



}
