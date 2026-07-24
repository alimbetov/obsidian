package lab.b01;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAdjusters;

public final class DateTimeProof {

    private DateTimeProof() {
    }

    public static void main(String[] args) {
        LocalDate januaryEnd = LocalDate.of(2025, 1, 31);
        check(januaryEnd.plusMonths(1).equals(LocalDate.of(2025, 2, 28)),
                "plusMonths must resolve to the last valid target-month day");
        check(januaryEnd.plusMonths(1).plusMonths(1).equals(LocalDate.of(2025, 3, 28)),
                "sequential month arithmetic must start from the adjusted date");
        check(januaryEnd.plusMonths(2).equals(LocalDate.of(2025, 3, 31)),
                "direct two-month arithmetic must resolve independently");

        Period normalized = Period.of(1, 14, 3).normalized();
        check(normalized.equals(Period.of(2, 2, 3)),
                "Period normalization must combine years and months only");

        boolean unsupportedDurationDetected = false;
        try {
            LocalDate.of(2026, 7, 24).plus(Duration.ofHours(1));
        } catch (DateTimeException expected) {
            unsupportedDurationDetected = true;
        }
        check(unsupportedDurationDetected, "LocalDate must reject second-based Duration units");

        check(Instant.EPOCH.equals(Instant.parse("1970-01-01T00:00:00Z")),
                "Instant epoch must be UTC 1970-01-01T00:00:00Z");

        ZoneId almaty = ZoneId.of("Asia/Almaty");
        ZonedDateTime almatyNoon = ZonedDateTime.of(2026, 7, 24, 12, 0, 0, 0, almaty);
        ZonedDateTime utcSameInstant = almatyNoon.withZoneSameInstant(ZoneOffset.UTC);
        check(almatyNoon.toInstant().equals(utcSameInstant.toInstant()),
                "withZoneSameInstant must preserve the timeline point");
        check(!almatyNoon.toLocalTime().equals(utcSameInstant.toLocalTime()),
                "same-instant conversion must update local clock fields when offsets differ");

        ZoneId berlin = ZoneId.of("Europe/Berlin");
        ZonedDateTime gap = ZonedDateTime.of(2026, 3, 29, 2, 30, 0, 0, berlin);
        check(gap.getHour() == 3 && gap.getMinute() == 30,
                "DST gap must shift nonexistent local time forward");
        check(gap.getOffset().equals(ZoneOffset.ofHours(2)),
                "DST gap result must use the later offset");

        ZonedDateTime overlapEarlier = ZonedDateTime.of(2026, 10, 25, 2, 30, 0, 0, berlin);
        ZonedDateTime overlapLater = overlapEarlier.withLaterOffsetAtOverlap();
        check(overlapEarlier.getOffset().equals(ZoneOffset.ofHours(2)),
                "overlap default must use the earlier offset");
        check(overlapLater.getOffset().equals(ZoneOffset.ofHours(1)),
                "withLaterOffsetAtOverlap must select the later occurrence");
        check(!overlapEarlier.toInstant().equals(overlapLater.toInstant()),
                "overlap occurrences must map to different instants");

        ZonedDateTime beforeSpring = ZonedDateTime.of(2026, 3, 28, 12, 0, 0, 0, berlin);
        check(beforeSpring.plus(Period.ofDays(1)).getHour() == 12,
                "calendar-day addition must preserve local noon");
        check(beforeSpring.plus(Duration.ofHours(24)).getHour() == 13,
                "24 elapsed hours must reflect spring offset change");

        DateTimeFormatter smart = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        DateTimeFormatter strict = smart.withResolverStyle(ResolverStyle.STRICT);
        check(LocalDate.parse("2025-02-30", smart).equals(LocalDate.of(2025, 2, 28)),
                "SMART resolver must adjust invalid February day");

        boolean strictFailureDetected = false;
        try {
            LocalDate.parse("2025-02-30", strict);
        } catch (DateTimeParseException expected) {
            strictFailureDetected = true;
        }
        check(strictFailureDetected, "STRICT resolver must reject invalid calendar date");

        LocalDate monday = LocalDate.of(2026, 7, 27);
        check(monday.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.MONDAY)).equals(monday),
                "nextOrSame must retain an already matching day");
        check(monday.with(TemporalAdjusters.next(java.time.DayOfWeek.MONDAY))
                        .equals(LocalDate.of(2026, 8, 3)),
                "next must move to a later matching day");

        System.out.println("JAVA-B01 DateTimeProof PASS");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
