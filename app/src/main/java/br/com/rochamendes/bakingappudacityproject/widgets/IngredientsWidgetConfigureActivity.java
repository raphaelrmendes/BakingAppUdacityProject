package br.com.rochamendes.bakingappudacityproject.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import br.com.rochamendes.bakingappudacityproject.dataPersist.recipesViewModel;
import br.com.rochamendes.bakingappudacityproject.entities.Ingredients;
import br.com.rochamendes.bakingappudacityproject.entities.Recipes;
import br.com.rochamendes.bakingappudacityproject.R;

public class IngredientsWidgetConfigureActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "br.com.rochamendes.bakingappudacityproject.Widgets.IngredientsAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner recipesSpinner;
    private Recipes selectedRecipe;

    public IngredientsWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.ingredients_widget_configure);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        setResult(RESULT_CANCELED);
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        recipesViewModel recipesViewModel = ViewModelProviders.of(this)
                .get(br.com.rochamendes.bakingappudacityproject.dataPersist.recipesViewModel.class);
        recipesSpinner = findViewById(R.id.spinner_widget_config);
        List<Recipes> recipesList = new ArrayList<>();
        recipesViewModel.getListRecipes().observe(this, new Observer<List<Recipes>>() {
            @Override
            public void onChanged(List<Recipes> recipes) {
                recipesList.addAll(recipes);
                ArrayAdapter<Recipes> arrayAdapter =
                        new ArrayAdapter<Recipes>(IngredientsWidgetConfigureActivity.this, R.layout.support_simple_spinner_dropdown_item, recipesList);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                recipesSpinner.setAdapter(arrayAdapter);
                recipesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedRecipe = (Recipes) adapterView.getSelectedItem();
                        Log.i("Mensagem", "onItemSelected: " + selectedRecipe.getName());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
        });
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = IngredientsWidgetConfigureActivity.this;

            saveRecipeIdPref(context, mAppWidgetId, selectedRecipe.getIngredientsList(), selectedRecipe.getName());

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            IngredientsAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    static void saveRecipeIdPref(Context context, int appWidgetId, Ingredients[] ingredientsArray, String name) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        int numberOfIngredients = ingredientsArray.length;
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_name", name);
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, numberOfIngredients);
        for (int i = 0; i < numberOfIngredients; i++) {
            prefs.putFloat("quantity_" + appWidgetId + "_" + i, ingredientsArray[i].getQuantity());
            prefs.putString("measure_" + appWidgetId + "_" + i, ingredientsArray[i].getMeasure());
            prefs.putString("ingredient_" + appWidgetId + "_" + i, ingredientsArray[i].getIngredient());
        }
        prefs.apply();
    }

    static StringBuilder loadRecipeIdPref(Context context, int appWidgetId) {
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            int numberOfIngredients = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0);
            if (numberOfIngredients > 0) {
                Ingredients[] ingredientsArray = new Ingredients[numberOfIngredients];
                for (int i = 0; i < numberOfIngredients; i++) {
                    ingredientsArray[i] = new Ingredients(0, null, null);
                    ingredientsArray[i].setQuantity(prefs.getFloat("quantity_" + appWidgetId + "_" + i, 0));
                    ingredientsArray[i].setMeasure(prefs.getString("measure_" + appWidgetId + "_" + i, ""));
                    ingredientsArray[i].setIngredient(prefs.getString("ingredient_" + appWidgetId + "_" + i, ""));
                }
                StringBuilder widgetText = new StringBuilder();
                widgetText.append("Recipe: ")
                    .append(prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_name",""))
                    .append("\n\n");
                for (int i = 0; i < ingredientsArray.length; i++) {
                    widgetText.append("• ")
                            .append(ingredientsArray[i].getQuantity())
                            .append(" ")
                            .append(ingredientsArray[i].getMeasure())
                            .append(" of ")
                            .append(ingredientsArray[i].getIngredient())
                            .append(";")
                            .append("\n");
                }
                return widgetText;
            }
        }
        return null;
    }

    static void deleteRecipeIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        int numberOfIngredients = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0);
        for (int i = 0; i < numberOfIngredients; i++) {
            prefsEdit.remove("quantity_" + appWidgetId + "_" + i);
            prefsEdit.remove("measure_" + appWidgetId + "_" + i);
            prefsEdit.remove("ingredient_" + appWidgetId + "_" + i);
        }
        prefsEdit.remove(PREF_PREFIX_KEY + appWidgetId);
        prefsEdit.remove(PREF_PREFIX_KEY + appWidgetId + "_name");
        prefsEdit.apply();
    }
}

