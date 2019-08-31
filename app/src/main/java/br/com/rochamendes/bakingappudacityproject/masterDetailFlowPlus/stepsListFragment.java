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

import br.com.rochamendes.bakingappudacityproject.adapters.StepsListAdapter;
import br.com.rochamendes.bakingappudacityproject.dataPersist.recipesViewModel;
import br.com.rochamendes.bakingappudacityproject.entities.Recipes;
import br.com.rochamendes.bakingappudacityproject.entities.Steps;
import br.com.rochamendes.bakingappudacityproject.R;

public class stepsListFragment extends Fragment {

    public static final String string_step_id = "id";
    public static final String string_step_short_description = "shortDescription";
    public static final String string_step_description = "description";
    public static final String string_step_videoURL = "videoURL";
    public static final String string_step_position = "position";
    public static final String string_step_size = "size";
    private RecyclerView StepsRecycler;
    private StepsListAdapter stepsListAdapter;
    private Steps[] stepsArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_steps_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
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
            StepsRecycler = getView().findViewById(R.id.steps_recyclerview);
            RecyclerView.LayoutManager stepsLayout = new LinearLayoutManager(this.getContext());
            StepsRecycler.setLayoutManager(stepsLayout);
            recipesViewModel.getRecipe(idRecipe).observe(this, new Observer<List<Recipes>>() {
                @Override
                public void onChanged(List<Recipes> recipe) {
                    stepsArray = recipe.get(0).getStepsList();
                    stepsListAdapter = new StepsListAdapter(stepsArray, getContext());
                    StepsRecycler.setAdapter(stepsListAdapter);
                    stepsListAdapter.setOnItemClickListener(new StepsListAdapter.stepsListener() {
                        @Override
                        public void stepsClick(int position) {
                            Log.i("Mensagem", "Clicked on step #" + position);
                            Fragment selectedStep = new stepsDetailFragment();
                            Bundle args = new Bundle();
                            for (int i=0; i < stepsArray.length; i++) {
                                args.putInt(string_step_id +i, stepsArray[i].getId());
                                args.putString(string_step_short_description+i, stepsArray[i].getShortDescription());
                                args.putString(string_step_description+i, stepsArray[i].getDescription());
                                args.putString(string_step_videoURL+i, stepsArray[i].getVideoURL());
                            }
                            args.putInt(string_step_position, position);
                            args.putInt(string_step_size, stepsArray.length);
                            selectedStep.setArguments(args);
                            if (getActivity().findViewById(R.id.fragment_container_ingredients) != null){
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container_detail, selectedStep).commit();
                            } else {
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, selectedStep).commit();
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Mensagem", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("Mensagem", "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Mensagem", "onResume");
    }
}