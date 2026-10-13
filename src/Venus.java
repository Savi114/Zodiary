// Venus inherits the shared attributes and methods from Planet.
public class Venus extends Planet {

    public Venus() {
        // Calls the Planet constructor and supplies the name.
        super("Venus");
    }

    @Override //Venus provides its own version of getDescription().
    public String getDescription() {
        return "Venus: one of the four planets observed by Zodiary.";
    }
}