package model.story;

public class Character {

    private String name;
    private int age;
    private String gender;
    private String species;
    private String description;

    public Character(String name, int age, String gender, String species, String description) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.species = species;
        this.description = description;
    }

    public Character() {}

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getSpecies() {
        return species;
    }

    public String getDescription() {
        return description;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\n" +
                "Age: " + age + "\n" +
                "Gender: " + gender + "\n" +
                "Species: " + species + "\n" +
                "Description: " + description;
    }
}
