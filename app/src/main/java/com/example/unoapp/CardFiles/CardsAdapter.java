package com.example.unoapp.CardFiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unoapp.R;

import java.util.ArrayList;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {
    public interface onPlacedCard {
        void placedCardResult(CardModel placedCard);
    }
    private ArrayList<CardModel> cards = new ArrayList<>();
    private onPlacedCard placedCard;
    private Context context;

    public CardsAdapter(Context context) {
        this.context = context;
    }

    public void setCards(ArrayList<CardModel> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardModel boundCard = cards.get(position);
        Glide.with(context)
                .asBitmap()
                .load(getImage(boundCard))
                .into(holder.cardImage);
        if (boundCard.isPlayable()) {
            holder.cardParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog builder = new AlertDialog.Builder(context)
                            .setTitle("Playing Card...")
                            .setMessage("Place Card?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                     try {
                                         placedCard = (onPlacedCard) context;
                                         placedCard.placedCardResult(boundCard);
                                     } catch (ClassCastException e) {
                                         e.printStackTrace();
                                     }
                                }
                            }).create();
                    builder.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    private int getImage(CardModel boundCard) {
        switch(boundCard.getType()) {
            case CardModel.TYPE_NUMBER:
                switch(boundCard.getNumber()) {
                    case CardModel.NUMBER_ZERO:
                        switch(boundCard.getColor()) {
                            case CardModel.COLOR_RED:
                                return R.drawable.zero_red;
                            case CardModel.COLOR_YELLOW:
                                return R.drawable.zero_yellow;
                            case CardModel.COLOR_BLUE:
                                return R.drawable.zero_blue;
                            case CardModel.COLOR_GREEN:
                                return R.drawable.zero_green;
                        }
                        break;
                    case CardModel.NUMBER_ONE:
                        switch(boundCard.getColor()) {
                            case CardModel.COLOR_RED:
                                return R.drawable.one_red;
                            case CardModel.COLOR_YELLOW:
                                return R.drawable.one_yellow;
                            case CardModel.COLOR_BLUE:
                                return R.drawable.one_blue;
                            case CardModel.COLOR_GREEN:
                                return R.drawable.one_green;
                        }
                        break;
                    case CardModel.NUMBER_TWO:
                        switch(boundCard.getColor()) {
                            case CardModel.COLOR_RED:
                                return R.drawable.two_red;
                            case CardModel.COLOR_YELLOW:
                                return R.drawable.two_yellow;
                            case CardModel.COLOR_BLUE:
                                return R.drawable.two_blue;
                            case CardModel.COLOR_GREEN:
                                return R.drawable.two_green;
                        }
                        break;
                    case CardModel.NUMBER_THREE:
                        switch(boundCard.getColor()) {
                            case CardModel.COLOR_RED:
                                return R.drawable.three_red;
                            case CardModel.COLOR_YELLOW:
                                return R.drawable.three_yellow;
                            case CardModel.COLOR_BLUE:
                                return R.drawable.three_blue;
                            case CardModel.COLOR_GREEN:
                                return R.drawable.three_green;
                        }
                        break;
                    case CardModel.NUMBER_FOUR:
                        switch(boundCard.getColor()) {
                            case CardModel.COLOR_RED:
                                return R.drawable.four_red;
                            case CardModel.COLOR_YELLOW:
                                return R.drawable.four_yellow;
                            case CardModel.COLOR_BLUE:
                                return R.drawable.four_blue;
                            case CardModel.COLOR_GREEN:
                                return R.drawable.four_green;
                        }
                        break;
                    case CardModel.NUMBER_FIVE:
                        switch(boundCard.getColor()) {
                            case CardModel.COLOR_RED:
                                return R.drawable.five_red;
                            case CardModel.COLOR_YELLOW:
                                return R.drawable.five_yellow;
                            case CardModel.COLOR_BLUE:
                                return R.drawable.five_blue;
                            case CardModel.COLOR_GREEN:
                                return R.drawable.five_green;
                        }
                        break;
                    case CardModel.NUMBER_SIX:
                        switch(boundCard.getColor()) {
                            case CardModel.COLOR_RED:
                                return R.drawable.six_red;
                            case CardModel.COLOR_YELLOW:
                                return R.drawable.six_yellow;
                            case CardModel.COLOR_BLUE:
                                return R.drawable.six_blue;
                            case CardModel.COLOR_GREEN:
                                return R.drawable.six_green;
                        }
                        break;
                    case CardModel.NUMBER_SEVEN:
                        switch(boundCard.getColor()) {
                            case CardModel.COLOR_RED:
                                return R.drawable.seven_red;
                            case CardModel.COLOR_YELLOW:
                                return R.drawable.seven_yellow;
                            case CardModel.COLOR_BLUE:
                                return R.drawable.seven_blue;
                            case CardModel.COLOR_GREEN:
                                return R.drawable.seven_green;
                        }
                        break;
                    case CardModel.NUMBER_EIGHT:
                        switch(boundCard.getColor()) {
                            case CardModel.COLOR_RED:
                                return R.drawable.eight_red;
                            case CardModel.COLOR_YELLOW:
                                return R.drawable.eight_yellow;
                            case CardModel.COLOR_BLUE:
                                return R.drawable.eight_blue;
                            case CardModel.COLOR_GREEN:
                                return R.drawable.eight_green;
                        }
                        break;
                    case CardModel.NUMBER_NINE:
                        switch(boundCard.getColor()) {
                            case CardModel.COLOR_RED:
                                return R.drawable.nine_red;
                            case CardModel.COLOR_YELLOW:
                                return R.drawable.nine_yellow;
                            case CardModel.COLOR_BLUE:
                                return R.drawable.nine_blue;
                            case CardModel.COLOR_GREEN:
                                return R.drawable.nine_green;
                        }
                        break;

                }
                break;
            case CardModel.TYPE_PLUS_TWO:
                switch(boundCard.getColor()) {
                    case CardModel.COLOR_RED:
                        return R.drawable.plustwo_red;
                    case CardModel.COLOR_YELLOW:
                        return R.drawable.plustwo_yellow;
                    case CardModel.COLOR_BLUE:
                        return R.drawable.plustwo_blue;
                    case CardModel.COLOR_GREEN:
                        return R.drawable.plustwo_green;
                }
                break;
            case CardModel.TYPE_SKIP:
                switch(boundCard.getColor()) {
                    case CardModel.COLOR_RED:
                        return R.drawable.skip_red;
                    case CardModel.COLOR_YELLOW:
                        return R.drawable.skip_yellow;
                    case CardModel.COLOR_BLUE:
                        return R.drawable.skip_blue;
                    case CardModel.COLOR_GREEN:
                        return R.drawable.skip_green;
                }
                break;
            case CardModel.TYPE_REVERSE:
                switch(boundCard.getColor()) {
                    case CardModel.COLOR_RED:
                        return R.drawable.reverse_red;
                    case CardModel.COLOR_YELLOW:
                        return R.drawable.reverse_yellow;
                    case CardModel.COLOR_BLUE:
                        return R.drawable.reverse_blue;
                    case CardModel.COLOR_GREEN:
                        return R.drawable.reverse_green;
                }
                break;
            case CardModel.TYPE_COLOR_SWITCH:
                return R.drawable.colorswitch;
            case CardModel.TYPE_PLUS_FOUR:
                return R.drawable.plusfour;
            default:
                return R.drawable.backcard;
        }
        return -1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout cardParent;
        private ImageView cardImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardParent = itemView.findViewById(R.id.cardParent);
            cardImage = itemView.findViewById(R.id.cardImage);
        }
    }
}
