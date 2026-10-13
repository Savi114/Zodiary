public class Planet {

    //stores the planets name. Other classes access it through getName().

    private String name;

    public Planet(String name) {
        this.name = name;
        //Assigns the constructor’s argument to the object’s attribute.
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return "A planet observed by Zodiary.";
    }
}