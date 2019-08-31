package br.com.rochamendes.bakingappudacityproject.masterDetailFlowPlus;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.MenuItem;

import java.util.List;

import br.com.rochamendes.bakingappudacityproject.dataPersist.recipesViewModel;
import br.com.rochamendes.bakingappudacityproject.entities.Recipes;
import br.com.rochamendes.bakingappudacityproject.R;
import br.com.rochamendes.bakingappudacityproject.databinding.ActivityRecipeBinding;

public class RecipeActivity extends AppCompatActivity {

    private int idRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Mensagem", "onCreate");
        idRecipe = getIntent().getIntExtra("id", -1);
        ActivityRecipeBinding activityRecipeBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe);

        if (activityRecipeBinding.fragmentContainerIngredients != null){
            Log.i("Mensagem", "Recipe: Large Panel");
            Fragment ingredientsFragment = new ingredientsFragment();
            Fragment stepsListFragment = new stepsListFragment();
            Bundle args = new Bundle();
            args.putInt("id", idRecipe);
            ingredientsFragment.setArguments(args);
            stepsListFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, stepsListFragment)
                    .replace(R.id.fragment_container_ingredients, ingredientsFragment)
                    .commit();
        } else {
            Log.i("Mensagem", "Recipe: Narrow Panel");
            BottomNavigationView navView = activityRecipeBinding.navView;
            navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            if (savedInstanceState == null){ navView.setSelectedItemId(R.id.navigation_ingredients); }
        }
        recipesViewModel recipesViewModel = ViewModelProviders.of(this).get(br.com.rochamendes.bakingappudacityproject.dataPersist.recipesViewModel.class);
        recipesViewModel.getRecipe(idRecipe).observe(this, new Observer<List<Recipes>>() {
            @Override
            public void onChanged(List<Recipes> recipes) {
                if (idRecipe != -1) {
                    RecipeActivity.this.setTitle(recipes.get(0).getName());
                }
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.i("Mensagem", "Recipe: navigation click listener");
            Fragment selectedFragment = new ingredientsFragment();
            switch (item.getItemId()) {
                case R.id.navigation_ingredients:
                    selectedFragment = new ingredientsFragment();
                    break;
                case R.id.navigation_steps:
                    selectedFragment = new stepsListFragment();
                    break;
            }
            Bundle args = new Bundle();
            args.putInt("id", idRecipe);
            selectedFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}