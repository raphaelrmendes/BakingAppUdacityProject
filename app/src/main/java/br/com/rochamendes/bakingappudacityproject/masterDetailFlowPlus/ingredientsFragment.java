package br.com.rochamendes.bakingappudacityproject.masterDetailFlowPlus;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.rochamendes.bakingappudacityproject.adapters.IngredientsAdapter;
import br.com.rochamendes.bakingappudacityproject.R;
import br.com.rochamendes.bakingappudacityproject.entities.Ingredients;
import br.com.rochamendes.bakingappudacityproject.entities.Recipes;
import br.com.rochamendes.bakingappudacityproject.dataPersist.recipesViewModel;

public class ingredientsFragment extends Fragment {

    private RecyclerView IngredientsRecycler;
    private IngredientsAdapter ingredientsAdapter;
    private Ingredients[] ingredientsArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingredients, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        recipesViewModel recipesViewModel = ViewModelProviders.of(this).get(br.com.rochamendes.bakingappudacityproject.dataPersist.recipesViewModel.class);
        super.onViewCreated(view, savedInstanceState);

        int idRecipe;
        try {
            Bundle args = getArguments();
            idRecipe = args.getInt("id", -1);
            Log.i("Mensagem", "idRecipe = " + idRecipe);
        } catch (Exception e) {
            Log.i("Mensagem", "Null Arguments");
            idRecipe = -1;
        }
        if (idRecipe != -1) {
            IngredientsRecycler = getView().findViewById(R.id.ingredients_recyclerview);
            RecyclerView.LayoutManager ingredientsLayout = new LinearLayoutManager(this.getContext());
            if (getActivity().findViewById(R.id.fragment_container_ingredients) != null){
                ((LinearLayoutManager) ingredientsLayout).setOrientation(RecyclerView.HORIZONTAL);
            }
            IngredientsRecycler.setLayoutManager(ingredientsLayout);
            recipesViewModel.getRecipe(idRecipe).observe(this, new Observer<List<Recipes>>() {
                @Override
                public void onChanged(List<Recipes> recipe) {
                    ingredientsArray = recipe.get(0).getIngredientsList();
                    ingredientsAdapter = new IngredientsAdapter(ingredientsArray);
                    IngredientsRecycler.setAdapter(ingredientsAdapter);
                }
            });
        }
    }
}