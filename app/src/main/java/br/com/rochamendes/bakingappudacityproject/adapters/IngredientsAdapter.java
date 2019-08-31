package br.com.rochamendes.bakingappudacityproject.adapters;

import br.com.rochamendes.bakingappudacityproject.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import br.com.rochamendes.bakingappudacityproject.entities.Ingredients;
import br.com.rochamendes.bakingappudacityproject.databinding.FragmentIngredientsRecyclerItemBinding;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {

    private Ingredients[] ingredientsArray;

    public IngredientsAdapter(Ingredients[] ingredients) {
        ingredientsArray = ingredients;
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {
        FragmentIngredientsRecyclerItemBinding fragmentIngredientsRecyclerItemBinding;

        IngredientsViewHolder(@NonNull FragmentIngredientsRecyclerItemBinding itemView) {
            super(itemView.getRoot());
            fragmentIngredientsRecyclerItemBinding = itemView;
        }
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FragmentIngredientsRecyclerItemBinding fragmentIngredientsRecyclerItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.fragment_ingredients_recycler_item, parent, false);
        return new IngredientsViewHolder(fragmentIngredientsRecyclerItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final IngredientsViewHolder holder, int position) {
        Ingredients ingredient = ingredientsArray[position];
        StringBuilder ingredientsText = new StringBuilder();
        ingredientsText.append(ingredient.getQuantity()).append(" ")
                .append(ingredient.getMeasure()).append(" of ")
                .append(ingredient.getIngredient());
        holder.fragmentIngredientsRecyclerItemBinding.strRecyclerIngredientsText.setText(ingredientsText);
        if (position == ingredientsArray.length - 1) {
            holder.fragmentIngredientsRecyclerItemBinding.ingredientsRecyclerviewSpacing.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (ingredientsArray == null) return 0;
        return ingredientsArray.length;
    }
}
