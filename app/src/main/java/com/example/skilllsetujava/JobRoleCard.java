package com.example.skilllsetujava;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public class JobRoleCard {
    public CardView cardView;
    public ImageView icon;
    public TextView text;
    public String roleName;

    public JobRoleCard(CardView cardView, ImageView icon, TextView text, String roleName) {
        this.cardView = cardView;
        this.icon = icon;
        this.text = text;
        this.roleName = roleName;
    }
}