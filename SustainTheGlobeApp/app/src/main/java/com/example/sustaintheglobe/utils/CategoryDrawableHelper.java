package com.example.sustaintheglobe.utils;

import android.content.Context;

import com.example.sustaintheglobe.R;

public class CategoryDrawableHelper {

    public static int getCategoryDrawable(String category) {
        int defaultDrawableResourceId = R.drawable.ic_cat_default; // Set your default drawable resource ID here

        // Convert category to lowercase for case-insensitive comparison
        String lowercaseCategory = category.toLowerCase();

        // Use switch case to match category names with drawable names
        switch (lowercaseCategory) {
            case "energy conservation":
                return R.drawable.ic_cat_energy;
            case "waste reduction":
                return R.drawable.ic_cat_waste;
            case "environmental preservation":
                return R.drawable.ic_cat_environment;
            case "sustainable transportation":
                return R.drawable.ic_cat_transportation;
            case "water conservation":
                return R.drawable.ic_cat_water;
            case "food sustainability":
                return R.drawable.ic_cat_food;
            case "community engagement":
                return R.drawable.ic_cat_community;
            case "sustainable shopping":
                return R.drawable.ic_cat_shopping;
            case "education and awareness":
                return R.drawable.ic_cat_education;
            case "social responsibility":
                return R.drawable.ic_cat_social;
            default:
                // Return the default drawable resource ID if category is not found
                return defaultDrawableResourceId;
        }
    }
}
