package com.example.sustaintheglobe.service;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.sustaintheglobe.R;
import com.example.sustaintheglobe.utils.CategoryDrawableHelper;

public class HelperPopUpWindow {

    //PopupWindow display method

    public void showPopupWindow(final View view, String taskID,String title, String description,String category){


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwindow_help, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

        ImageView categoryIcon = popupView.findViewById(R.id.categoryLogo);
        TextView categoryTV = popupView.findViewById(R.id.category);
        TextView titleTV = popupView.findViewById(R.id.titleText);
        TextView messageTV = popupView.findViewById(R.id.description);
        if(title == null)
            titleTV.setVisibility(View.GONE);
        else
            titleTV.setText(title);
        messageTV.setText(description);
        categoryTV.setText(category);
        categoryIcon.setImageResource(CategoryDrawableHelper.getCategoryDrawable(category));

        ImageView buttonEdit = popupView.findViewById(R.id.closeButton);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();

            }
        });



        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

}