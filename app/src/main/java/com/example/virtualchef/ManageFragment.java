package com.example.virtualchef;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ManageFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ManageFragment() {
        // Required empty public constructor
    }
    public static ManageFragment newInstance(String param1, String param2) {
        ManageFragment fragment = new ManageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }






    //LIST VIEW
    ListView ingredientList;
    //String[] listItemPopulate = {"Item1","Item2","Item3"};
    ArrayAdapter<String> adapter;
    //LIST OF INGREDIENT CLASS
    private List<String> listItemPopulate;
    private List<String> fullNutrients;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_managebutton, container, false);
        ingredientList = view.findViewById(R.id.ingredientList);


        adapter = new ArrayAdapter<>(requireContext(), R.layout.ingredient_card, R.id.listText, new ArrayList<>());
        ingredientList.setAdapter(adapter);

        // Fetch data from the server and update the adapter
        fetchIngredientsFromServer();


        ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //GET THE STRING INSIDE THE ITEM
                // Create an instance of the fragment
                String nutrientsInfo = fullNutrients.get(position);

                IngredientNutrients_delete fragment = new IngredientNutrients_delete();
                // Pass data to the fragment using arguments
                Bundle args = new Bundle();
                args.putString("nutrientsInfo", nutrientsInfo);
                fragment.setArguments(args);

                // Add the fragment to the activity
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment); // R.id.fragmentContainer is the ID of a FrameLayout in your activity_main.xml
                fragmentTransaction.commit();

            }
        });
        return view;
    }

    //SEND REQUEST TO SERVER AND RECIEVE A JSON OF ALL INGREDIENT
    //WORKING
    private void fetchIngredientsFromServer() {
        String url = "http://10.0.2.2:5000/user/listIngredients"; // Replace with your actual server URL
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        fullNutrients = fullParseJsonResponse(response);
                        listItemPopulate = parseJsonResponse(response);

                        //CREATE NEW ADAPTER AND SET ADAPTER
                        //ingredientAdapter = new IngredientAdapter(ingredients);
                        //recyclerView.setAdapter(ingredientAdapter);
                        Log.d("RESPONSE",response.toString());
                        Log.d("LISTITEMPOPULATE",listItemPopulate.toString());
                        Log.d("FULL NUTRIENTS",fullNutrients.toString());


                        adapter.clear();
                        adapter.addAll(listItemPopulate);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error fetching data from server", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    private List<String> fullParseJsonResponse(JSONArray jsonArray) {
        List<String> full = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject foodItem = jsonArray.getJSONObject(i);
                String foodName = foodItem.optString("Item","");
                double serving_qty = foodItem.optDouble("Serving Quantity");
                double phosphorus = foodItem.optDouble("Phosphorus");
                String serving_unit = foodItem.optString("Serving Unit","");
                double serving_weight_grams = foodItem.optDouble("Serving Weight in grams");
                double calories = foodItem.optDouble("Calories");
                double totalFat = foodItem.optDouble("Total Fat");
                double saturatedFat = foodItem.optDouble("Saturated Fat");
                double cholesterol = foodItem.optDouble("Cholesterol");
                double sodium = foodItem.optDouble("Sodium");
                double totalCarbohydrate = foodItem.optDouble("Total Carbohydrate");
                double dietaryFiber = foodItem.optDouble("Dietary Fiber");
                double sugars = foodItem.optDouble("Sugars");
                double protein = foodItem.optDouble("Protein");
                double potassium = foodItem.optDouble("Potassium");


                String nutrientsInfo = "Item: " + foodName +
                        "\nCalories: " + calories +
                        "\nServing Quantity: " + serving_qty +
                        "\nServing Unit: " + serving_unit +
                        "\nServing Weight in grams: " + serving_weight_grams +
                        "\nTotal Fat: " + totalFat +
                        "\nSaturated Fat: " + saturatedFat +
                        "\nCholesterol: " + cholesterol +
                        "\nSodium: " + sodium +
                        "\nTotal Carbohydrate: " + totalCarbohydrate +
                        "\nDietary Fiber: " + dietaryFiber +
                        "\nSugars: " + sugars +
                        "\nProtein: " + protein +
                        "\nPotassium: " + potassium +
                        "\nPhosphorus: " + phosphorus;
                full.add(nutrientsInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return full;
    }

    //THIS SHOULD RETURN ARRAY OF STRING
    //WORKING
    private List<String> parseJsonResponse(JSONArray jsonArray) {
        List<String> ingredients = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String item = jsonObject.optString("Item", "");
                String servingUnit = jsonObject.optString("Serving Unit", "");
                int quantity = jsonObject.optInt("quantity", 0);

                String result = "Name: " + item + " \nQuantity: " + quantity + " " + servingUnit ;
                ingredients.add(result);
                //fullNutrients.add(jsonObject.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ingredients;
    }


}