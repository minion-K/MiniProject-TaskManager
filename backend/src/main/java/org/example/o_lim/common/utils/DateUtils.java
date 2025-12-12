package org.example.o_lim.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");

    private static final DateTimeFormatter KST_FORMAT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter ISO_UTC
            = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static String toKstString(LocalDateTime utcLocalDateTime) {
        if (utcLocalDateTime == null) return null;

        ZonedDateTime zdtUtc = utcLocalDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime zdtKst = zdtUtc.withZoneSameInstant(ZONE_KST);

        return zdtKst.format(KST_FORMAT);
    }

    public static String toUtcString(LocalDateTime utcLocalDateTime) {
        if (utcLocalDateTime == null) return null;

        OffsetDateTime odt = utcLocalDateTime.atOffset(ZoneOffset.UTC);

        return ISO_UTC.format(odt);
    }

    public static LocalDateTime kstToUtc(LocalDateTime kstDateTime) {
        if (kstDateTime == null) return null;

        return kstDateTime.atZone(ZONE_KST)
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
    }
}

