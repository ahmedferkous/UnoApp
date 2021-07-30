package com.example.unoapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.unoapp.CardFiles.CardModel;
import com.example.unoapp.CardFiles.CardsAdapter;

public class ColourDialog extends DialogFragment {
    public interface ColorChange {
        void colorChangeResult(CardModel boundCard, String color);
    }

    private ImageView redSquare, yellowSquare, blueSquare, greenSquare;
    private final ColorChange colorChange;
    private final CardModel boundCard;

    public ColourDialog(CardsAdapter callingAdapter, CardModel boundCard) {
        colorChange = callingAdapter;
        this.boundCard = boundCard;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.color_dialog_fragment, null);
        initViews(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);

        redSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorChange.colorChangeResult(boundCard, CardModel.COLOR_RED);
                dismiss();
            }
        });

        yellowSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorChange.colorChangeResult(boundCard, CardModel.COLOR_YELLOW);
                dismiss();
            }
        });

        blueSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorChange.colorChangeResult(boundCard, CardModel.COLOR_BLUE);
                dismiss();
            }
        });

        greenSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorChange.colorChangeResult(boundCard, CardModel.COLOR_GREEN);
                dismiss();
            }
        });

        return builder.create();
    }

    private void initViews(View view) {
        redSquare = view.findViewById(R.id.imgViewRedSquare);
        yellowSquare = view.findViewById(R.id.imgViewYellowSquare);
        blueSquare = view.findViewById(R.id.imgViewBlueSquare);
        greenSquare = view.findViewById(R.id.imgViewGreenSquare);
    }
}
