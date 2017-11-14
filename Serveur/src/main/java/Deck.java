import java.util.ArrayList;
import java.util.Random;

public class Deck
{
    private final coincheProto.CARD_COLOR[] _card_colors = {coincheProto.CARD_COLOR.SPADE, coincheProto.CARD_COLOR.HEARTH,
            coincheProto.CARD_COLOR.DIAMOND, coincheProto.CARD_COLOR.CLUB};
    private final coincheProto.CARD_NAME[]  _card_names = {coincheProto.CARD_NAME.ACE, coincheProto.CARD_NAME.SEVEN,
            coincheProto.CARD_NAME.HEIGHT, coincheProto.CARD_NAME.NINE, coincheProto.CARD_NAME.TEN,
            coincheProto.CARD_NAME.JACK, coincheProto.CARD_NAME.QUEEN, coincheProto.CARD_NAME.KING};
    private ArrayList<Card>     _cards = new ArrayList<>( );

    Deck()
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                this._cards.add(new Card(_card_colors[i], _card_names[j]));
            }
        }
    }

    public Card                 getRandCard()
    {
        int                     cardNum;
        Random                  rand = new Random();

        if (this._cards.size() > 1)
            cardNum = rand.nextInt(this._cards.size() - 1);
        else
            cardNum = 0;
        Card                    theCard;

        theCard = this._cards.get(cardNum);
        this._cards.remove(cardNum);
        return (theCard);
    }

    public int                  getNbCards()
    { return(this._cards.size()); }
}
