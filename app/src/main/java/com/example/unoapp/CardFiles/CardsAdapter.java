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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unoapp.ColourDialog;
import com.example.unoapp.R;

import java.util.ArrayList;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> implements ColourDialog.ColorChange {
    public interface onPlacedCard {
        void placedCardResult(CardModel placedCard);
    }

    @Override
    public void colorChangeResult(CardModel boundCard, String color) {
        boundCard.setColor(color);
        removeCard(boundCard);
        placedCard.placedCardResult(boundCard);
    }

    private ArrayList<CardModel> cards = new ArrayList<>();
    private final onPlacedCard placedCard;
    private final FragmentManager fragmentManager;
    private final Context context;

    public CardsAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        placedCard = (onPlacedCard) context;
    }

    public void setCards(ArrayList<CardModel> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    private void removeCard(CardModel boundCard) {
        cards.remove(boundCard);
        notifyDataSetChanged();
    }

    public ArrayList<CardModel> getCards() {
        return cards;
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
                                    if (boundCard.getType().equals(CardModel.TYPE_PLUS_FOUR) || boundCard.getType().equals(CardModel.TYPE_COLOR_SWITCH)) {
                                        ColourDialog colourDialog = new ColourDialog(CardsAdapter.this, boundCard);
                                        colourDialog.show(fragmentManager, "");
                                    } else {
                                        removeCard(boundCard);
                                        placedCard.placedCardResult(boundCard);
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

    public static int getImage(CardModel card) {
        switch (card.getType()) {
            case CardModel.TYPE_NUMBER:
                switch (card.getNumber()) {
                    case CardModel.NUMBER_ZERO:
                        switch (card.getColor()) {
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
                        switch (card.getColor()) {
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
                        switch (card.getColor()) {
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
                        switch (card.getColor()) {
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
                        switch (card.getColor()) {
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
                        switch (card.getColor()) {
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
                        switch (card.getColor()) {
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
                        switch (card.getColor()) {
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
                        switch (card.getColor()) {
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
                        switch (card.getColor()) {
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
                switch (card.getColor()) {
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
                switch (card.getColor()) {
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
                switch (card.getColor()) {
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
                if (card.getColor() != null) {
                    switch (card.getColor()) {
                        case CardModel.COLOR_RED:
                            return R.drawable.colorswitch_red;
                        case CardModel.COLOR_YELLOW:
                            return R.drawable.colorswitch_yellow;
                        case CardModel.COLOR_BLUE:
                            return R.drawable.colorswitch_blue;
                        case CardModel.COLOR_GREEN:
                            return R.drawable.colorswitch_green;
                    }
                } else {
                    return R.drawable.colorswitch;
                }
            case CardModel.TYPE_PLUS_FOUR:
                if (card.getColor() != null) {
                    switch (card.getColor()) {
                        case CardModel.COLOR_RED:
                            return R.drawable.plusfour_red;
                        case CardModel.COLOR_YELLOW:
                            return R.drawable.plusfour_yellow;
                        case CardModel.COLOR_BLUE:
                            return R.drawable.plusfour_blue;
                        case CardModel.COLOR_GREEN:
                            return R.drawable.plusfour_green;
                    }
                } else {
                    return R.drawable.plusfour;
                }
            default:
                return R.drawable.backcard;
        }
        return -1;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout cardParent;
        private ImageView cardImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardParent = itemView.findViewById(R.id.cardParent);
            cardImage = itemView.findViewById(R.id.cardImage);
        }
    }
}
