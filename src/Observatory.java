import java.time.LocalDate;
import java.util.ArrayList;

public class Observatory {

    // Store the collection of observations managed by this object.
    private ArrayList<Observation> observations;

    // Receive the loaded observations when the observatory is created.
    public Observatory(ArrayList<Observation> observations) {
        this.observations = new ArrayList<>(observations);
    }

    // Search for an observation matching all three requested values.
    public Observation findObservation(String planetName,
                                       String location,
                                       LocalDate date) {

        for (Observation observation : observations) {

            // Compare planet and location without requiring exact capitals.
            boolean samePlanet = observation.getPlanet().getName()
                    .equalsIgnoreCase(planetName);

            boolean sameLocation = observation.getLocation()
                    .equalsIgnoreCase(location);

            // Compare only the date part of the UTC timestamp.
            boolean sameDate = observation.getDateTime()
                    .toLocalDate().equals(date);

            // Return the record when all three conditions match.
            if (samePlanet && sameLocation && sameDate) {
                return observation;
            }
        }

        // No matching observation exists in the loaded dataset.
        return null;
    }
}