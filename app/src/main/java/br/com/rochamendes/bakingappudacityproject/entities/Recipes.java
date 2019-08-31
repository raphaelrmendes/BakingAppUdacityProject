package br.com.rochamendes.bakingappudacityproject.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_recipes")
public class Recipes {
    @PrimaryKey
    private int idRecipes;
    private String name;
    private Ingredients[] ingredientsList;
    private Steps[] stepsList;
    private int servings;
    private byte[] image;
    private boolean imageAvailable;
    private boolean favorite;

    public Recipes(int idRecipes, String name, Ingredients[] ingredientsList, Steps[] stepsList, int servings, byte[] image, boolean imageAvailable, boolean favorite) {
        this.idRecipes = idRecipes;
        this.name = name;
        this.ingredientsList = ingredientsList;
        this.stepsList = stepsList;
        this.servings = servings;
        this.image = image;
        this.imageAvailable = imageAvailable;
        this.favorite = favorite;
    }

    public int getIdRecipes() {
        return idRecipes;
    }

    public void setIdRecipes(int idRecipes) {
        this.idRecipes = idRecipes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ingredients[] getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(Ingredients[] ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public Steps[] getStepsList() {
        return stepsList;
    }

    public void setStepsList(Steps[] stepsList) {
        this.stepsList = stepsList;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public boolean isImageAvailable() {
        return imageAvailable;
    }

    public void setImageAvailable(boolean imageAvailable) {
        this.imageAvailable = imageAvailable;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String toString(){ return name; }
}
