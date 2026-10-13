import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {

        System.out.println("==============================");
        System.out.println("       Welcome to Zodiary");
        System.out.println("==============================");
        System.out.println("Type exit at any prompt to finish.");

        ArrayList<Observation> observations;

        // Load the supplied data before requesting user input.
        try {
            CSVLoader loader = new CSVLoader();

            observations =
                    loader.loadObservations("Data/Berlin.csv");

            observations.addAll(
                    loader.loadObservations("Data/BuenosAires.csv")
            );

        } catch (IOException error) {
            System.out.println("Could not read the CSV: "
                    + error.getMessage());
            return;

        } catch (IllegalArgumentException error) {
            System.out.println("Invalid CSV value: "
                    + error.getMessage());
            return;
        }

        if (observations.isEmpty()) {
            System.out.println("The dataset contains no observations.");
            return;
        }

        Observatory observatory = new Observatory(observations);

        // Build the planet choices from the actual loaded records.
        LinkedHashSet<String> planetNames = new LinkedHashSet<>();

        for (Observation observation : observations) {
            planetNames.add(observation.getPlanet().getName());
        }

        System.out.println("Observations loaded: "
                + observations.size());

        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {

                System.out.println();
                String planetName = readChoice(
                        scanner, "Enter planet", planetNames
                );

                if (planetName == null) {
                    break;
                }

                // Offer only locations containing the selected planet.
                LinkedHashSet<String> locations = new LinkedHashSet<>();

                for (Observation observation : observations) {
                    if (observation.getPlanet().getName()
                            .equalsIgnoreCase(planetName)) {
                        locations.add(observation.getLocation());
                    }
                }

                String location = readChoice(
                        scanner, "Enter location", locations
                );

                if (location == null) {
                    break;
                }

                // Collect available dates for this planet and location.
                // TreeSet keeps dates sorted and removes duplicates.
                TreeSet<LocalDate> availableDates = new TreeSet<>();

                for (Observation observation : observations) {
                    if (observation.getPlanet().getName()
                            .equalsIgnoreCase(planetName)
                            && observation.getLocation()
                            .equalsIgnoreCase(location)) {

                        availableDates.add(
                                observation.getDateTime().toLocalDate()
                        );
                    }
                }

                if (availableDates.size() < 2) {
                    System.out.println(
                            "This selection needs at least two observation dates."
                    );
                    continue;
                }

                System.out.println("Available dates: "
                        + availableDates.first() + " to "
                        + availableDates.last());

                LocalDate earlierDate = readDate(
                        scanner,
                        "Enter earlier date (YYYY-MM-DD)",
                        availableDates,
                        null
                );

                if (earlierDate == null) {
                    break;
                }

                LocalDate laterDate = readDate(
                        scanner,
                        "Enter later date (YYYY-MM-DD)",
                        availableDates,
                        earlierDate
                );

                if (laterDate == null) {
                    break;
                }

                Observation earlier = observatory.findObservation(
                        planetName, location, earlierDate
                );

                Observation later = observatory.findObservation(
                        planetName, location, laterDate
                );

                // Defensive check before using the returned records.
                if (earlier == null || later == null) {
                    System.out.println(
                            "Matching records could not be found."
                    );
                    continue;
                }

                try {
                    System.out.println();
                    System.out.println(
                            earlier.getPlanet().getDescription()
                    );

                    System.out.println();
                    System.out.println("--- Earlier observation ---");
                    printObservation(earlier);

                    System.out.println();
                    System.out.println("--- Later observation ---");
                    printObservation(later);

                    MovementComparison comparison =
                            new MovementComparison();

                    double change =
                            comparison.calculateChange(earlier, later);

                    System.out.println();
                    System.out.println("--- Movement comparison ---");

                    System.out.printf(
                            Locale.ENGLISH,
                            "Longitude change: %.6f degrees%n",
                            change
                    );

                    System.out.println("Movement: "
                            + comparison.getDirection(change));

                } catch (IllegalArgumentException error) {
                    System.out.println("Could not process observations: "
                            + error.getMessage());
                }

                // Validate the repeat answer instead of treating typos as no.
                LinkedHashSet<String> answers = new LinkedHashSet<>();
                answers.add("yes");
                answers.add("no");

                System.out.println();

                String answer = readChoice(
                        scanner,
                        "Compare another observation?",
                        answers
                );

                if (answer == null || answer.equals("no")) {
                    break;
                }
            }
        }

        System.out.println("Thank you for using Zodiary.");
    }

    // Read a line, remove extra spaces, and allow a clean exit.
    private static String readInput(Scanner scanner, String prompt) {

        System.out.print(prompt + ": ");

        // Finish cleanly if console input ends.
        if (!scanner.hasNextLine()) {
            return null;
        }

        String input = scanner.nextLine()
                .strip()
                .replaceAll("\\s+", " ");

        if (input.equalsIgnoreCase("exit")) {
            return null;
        }

        return input;
    }

    // Keep asking until the input matches one of the allowed choices.
    private static String readChoice(
            Scanner scanner,
            String prompt,
            LinkedHashSet<String> choices) {

        System.out.println("Choices: " + String.join(", ", choices));

        while (true) {
            String input = readInput(scanner, prompt);

            if (input == null) {
                return null;
            }

            if (input.isEmpty()) {
                System.out.println("Please enter a value.");
                continue;
            }

            for (String choice : choices) {
                if (choice.equalsIgnoreCase(input)) {
                    // Return the dataset's original spelling.
                    return choice;
                }
            }

            System.out.println("Invalid choice. Choose: "
                    + String.join(", ", choices));
        }
    }

    // Validate format, calendar date, record availability, and date order.
    private static LocalDate readDate(
            Scanner scanner,
            String prompt,
            TreeSet<LocalDate> availableDates,
            LocalDate earlierDate) {

        while (true) {
            String input = readInput(scanner, prompt);

            if (input == null) {
                return null;
            }

            if (input.isEmpty()) {
                System.out.println("Please enter a date.");
                continue;
            }

            // Require exactly four year digits, two month digits,
            // and two day digits, separated by hyphens.
            if (!input.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                System.out.println(
                        "Use YYYY-MM-DD, for example 2026-09-23."
                );
                continue;
            }

            LocalDate date;

            try {
                // LocalDate also rejects impossible dates such as February 30.
                date = LocalDate.parse(input);

            } catch (DateTimeParseException error) {
                System.out.println(
                        "That calendar date does not exist. Try again."
                );
                continue;
            }

            if (!availableDates.contains(date)) {
                System.out.println(
                        "No observation exists for this selection on that date."
                );
                System.out.println("Available date range: "
                        + availableDates.first() + " to "
                        + availableDates.last());
                continue;
            }

            // An earlier date must leave at least one later record available.
            if (earlierDate == null
                    && date.equals(availableDates.last())) {
                System.out.println(
                        "Choose an earlier date before "
                                + availableDates.last()
                                + " so a later record can be compared."
                );
                continue;
            }

            if (earlierDate != null && !date.isAfter(earlierDate)) {
                System.out.println(
                        "The second date must be after " + earlierDate + "."
                );
                continue;
            }

            return date;
        }
    }

    // Display the stored details and calculated results.
    private static void printObservation(Observation observation) {
        System.out.println("Planet: "
                + observation.getPlanet().getName());
        System.out.println("Date/time (UTC): "
                + observation.getDateTime());
        System.out.println("Location: "
                + observation.getLocation());
        System.out.println("Hemisphere: "
                + observation.getHemisphere());
        System.out.println("Longitude: "
                + observation.getEclipticLongitude());
        System.out.println("Zodiac sector: "
                + observation.getZodiacSector());
        System.out.println("Season: "
                + observation.getSeason());
    }
}