import java.time.LocalDateTime;

public class Observation {

    // Store the planet and details of one observation.
    private Planet planet;
    private LocalDateTime dateTime;
    private String location;
    private String hemisphere;
    private double eclipticLongitude;

    // Initialize the observation using values from the CSV.
    public Observation(Planet planet, LocalDateTime dateTime,
                       String location, String hemisphere,
                       double eclipticLongitude) {
        this.planet = planet;
        this.dateTime = dateTime;
        this.location = location;
        this.hemisphere = hemisphere;
        this.eclipticLongitude = eclipticLongitude;
    }

    // Getters allow other classes to read the private fields.
    public Planet getPlanet() {
        return planet;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getLocation() {
        return location;
    }

    public String getHemisphere() {
        return hemisphere;
    }

    public double getEclipticLongitude() {
        return eclipticLongitude;
    }

    // Calculate the zodiac sector from the observation's longitude.
    public String getZodiacSector() {

        // The twelve sectors in order, starting at zero degrees.
        String[] sectors = {
                "Aries", "Taurus", "Gemini", "Cancer",
                "Leo", "Virgo", "Libra", "Scorpio",
                "Sagittarius", "Capricorn", "Aquarius", "Pisces"
        };

        // Reject invalid numeric values.
        if (!Double.isFinite(eclipticLongitude)) {
            throw new IllegalArgumentException(
                    "Longitude must be a finite number."
            );
        }

        // Wrap the angle into the range 0 to less than 360 degrees.
        double longitude = eclipticLongitude % 360;

        if (longitude < 0) {
            longitude += 360;
        }

        // Each sector covers 30 degrees.
        // Converting to int removes the decimal part.
        int index = (int) (longitude / 30);

        return sectors[index];
    }

    // Determine the season using the month and hemisphere.
    // This uses calendar months rather than astronomical calculations.
    public String getSeason() {

        // Months are numbered from 1 (January) to 12 (December).
        int month = dateTime.getMonthValue();

        boolean northern = hemisphere.equalsIgnoreCase("Northern");
        boolean southern = hemisphere.equalsIgnoreCase("Southern");

        // Avoid assigning a season to an unrecognized hemisphere.
        if (!northern && !southern) {
            return "Unknown hemisphere";
        }

        // March, April, and May.
        if (month >= 3 && month <= 5) {
            return northern ? "Spring" : "Autumn";

            // June, July, and August.
        } else if (month >= 6 && month <= 8) {
            return northern ? "Summer" : "Winter";

            // September, October, and November.
        } else if (month >= 9 && month <= 11) {
            return northern ? "Autumn" : "Spring";

            // December, January, and February.
        } else {
            return northern ? "Winter" : "Summer";
        }
    }
}