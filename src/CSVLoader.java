import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class CSVLoader {

    // Receives a file path and returns the observations read from it.
    public ArrayList<Observation> loadObservations(String filePath)
            throws IOException {

        // Holds the observation objects created from the CSV rows.
        ArrayList<Observation> observations = new ArrayList<>();

        // Matches the CSV's date format, such as 2026-Sep-23 00:00.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "uuuu-MMM-dd HH:mm", Locale.ENGLISH
        );

        // Opens the file and closes it automatically after reading.
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filePath))) {

            // Skip the header containing the column names.
            reader.readLine();

            String line;

            // Read one row at a time until the file ends.
            while ((line = reader.readLine()) != null) {

                // Ignore blank lines.
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Separate the fields in this supplied CSV row.
                String[] columns = line.split(",", -1);

                // Column numbering starts at zero.
                LocalDateTime dateTime =
                        LocalDateTime.parse(columns[0].trim(), formatter);

                String location = columns[1].trim();
                String hemisphere = columns[3].trim();
                Planet planet = createPlanet(columns[4].trim());

                // Convert the longitude from text into a decimal number.
                double longitude =
                        Double.parseDouble(columns[9].trim());

                // Use the row's values to create an observation object.
                Observation observation = new Observation(
                        planet,
                        dateTime,
                        location,
                        hemisphere,
                        longitude
                );

                observations.add(observation);
            }
        }

        return observations;
    }

    // Match the CSV's planet name to the appropriate child class.
    private Planet createPlanet(String name) {

        switch (name) {
            case "Venus":
                return new Venus();

            case "Mars":
                return new Mars();

            case "Jupiter":
                return new Jupiter();

            case "Saturn":
                return new Saturn();

            default:
                throw new IllegalArgumentException(
                        "Unknown planet: " + name
                );
        }
    }
}