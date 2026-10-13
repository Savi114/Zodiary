public class MovementComparison {

    // Compare an earlier observation with a later observation.
    public double calculateChange(Observation earlier,
                                  Observation later) {

        // Comparisons must use the same planet and location.
        if (!earlier.getPlanet().getName().equals(
                later.getPlanet().getName())
                || !earlier.getLocation().equals(later.getLocation())) {
            throw new IllegalArgumentException(
                    "Choose the same planet and location."
            );
        }

        // Make sure the observations are in chronological order.
        if (!later.getDateTime().isAfter(earlier.getDateTime())) {
            throw new IllegalArgumentException(
                    "The second observation must be later."
            );
        }

        double change = later.getEclipticLongitude()
                - earlier.getEclipticLongitude();

        // Handle crossing zero degrees.
        // For example, 359 to 1 degrees means +2, rather than -358.
        change = change % 360;

        if (change > 180) {
            change -= 360;
        } else if (change < -180) {
            change += 360;
        }

        return change;
    }

    // Interpret the sign of the longitude change.
    public String getDirection(double change) {
        if (change > 0) {
            return "Direct (increasing longitude)";
        } else if (change < 0) {
            return "Retrograde (decreasing longitude)";
        } else {
            return "No change in recorded longitude";
        }
    }
}