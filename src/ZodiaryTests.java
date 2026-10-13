import java.time.LocalDateTime;

public class ZodiaryTests {

    public static void main(String[] args) {

        System.out.println("--- Zodiary Calculation Tests ---");

        // Check the start of the zodiac.
        check("0 degrees is Aries",
                observation(0, 9, 23, "Northern")
                        .getZodiacSector().equals("Aries"));

        // Check both sides of the Aries/Taurus boundary.
        check("29.999 degrees is Aries",
                observation(29.999, 9, 23, "Northern")
                        .getZodiacSector().equals("Aries"));

        check("30 degrees is Taurus",
                observation(30, 9, 23, "Northern")
                        .getZodiacSector().equals("Taurus"));

        // A full turn returns to the start of the zodiac.
        check("360 degrees wraps to Aries",
                observation(360, 9, 23, "Northern")
                        .getZodiacSector().equals("Aries"));

        // Check opposite seasons for the same month.
        check("September in the north is Autumn",
                observation(10, 9, 23, "Northern")
                        .getSeason().equals("Autumn"));

        check("September in the south is Spring",
                observation(10, 9, 23, "Southern")
                        .getSeason().equals("Spring"));

        MovementComparison comparison = new MovementComparison();

        // Crossing zero in the increasing direction means +2 degrees.
        double directChange = comparison.calculateChange(
                observation(359, 9, 23, "Northern"),
                observation(1, 9, 24, "Northern")
        );

        check("359 to 1 degrees means +2",
                Math.abs(directChange - 2) < 0.000001);

        // Crossing zero in the decreasing direction means -2 degrees.
        double retrogradeChange = comparison.calculateChange(
                observation(1, 9, 23, "Northern"),
                observation(359, 9, 24, "Northern")
        );

        check("1 to 359 degrees means -2",
                Math.abs(retrogradeChange + 2) < 0.000001);

        // Equal recorded positions should produce zero change.
        double noChange = comparison.calculateChange(
                observation(10, 9, 23, "Northern"),
                observation(10, 9, 24, "Northern")
        );

        check("Equal longitudes mean no change",
                Math.abs(noChange) < 0.000001);

        System.out.println();
        System.out.println("All 9 tests passed.");
    }

    // Create artificial observations for testing, separate from the CSV.
    private static Observation observation(double longitude,
                                           int month,
                                           int day,
                                           String hemisphere) {
        String location = hemisphere.equals("Northern")
                ? "Berlin" : "Buenos Aires";

        return new Observation(
                new Saturn(),
                LocalDateTime.of(2026, month, day, 0, 0),
                location,
                hemisphere,
                longitude
        );
    }

    // Stop and report the test name if an expected result is incorrect.
    private static void check(String name, boolean passed) {
        if (!passed) {
            throw new AssertionError("FAIL: " + name);
        }

        System.out.println("PASS: " + name);
    }
}