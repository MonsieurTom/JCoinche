import java.util.ArrayList;

public class Hand
{
    private boolean             _playedcard = false;
    private int                 _playedCardName = -1;
    private int                 _playedCardColor = -1;
    private static Hand         instance;
    private ArrayList<String>   _cardName;
    private ArrayList<String>   _cardColor;

    private Hand()
    {
        _cardColor = new ArrayList<>();
        _cardName = new ArrayList<>();
    }

    public static Hand      getInstance()
    {
        if (instance == null)
            instance = new Hand();
        return (instance);
    }

    public void             addCard(String cardName, String CardColor)
    {
        _cardName.add(cardName);
        _cardColor.add(CardColor);
    }

    public String           getCardName(int idx)
    {
        if (idx >= _cardName.size())
            return (null);
        else
            return (_cardName.get(idx));
    }

    public String           getCardColor(int idx)
    {
        if (idx >= _cardColor.size())
            return (null);
        else
            return (_cardColor.get(idx));
    }

    public boolean          playCard(String cardName, String cardColor)
    {
        for (int i = 0; i < _cardColor.size(); i++)
        {
            if (_cardColor.get(i).equalsIgnoreCase(cardColor) && _cardName.get(i).equalsIgnoreCase(cardName))
            {
                _playedCardColor = i;
                _playedCardName = i;
                _playedcard = true;
                return (true);
            }
        }
        return (false);
    }

    public boolean          deletePlayedCard()
    {
        if (_playedcard && _playedCardName != -1 && _playedCardColor != -1)
        {
            _cardName.remove(_playedCardName);
            _cardColor.remove(_playedCardColor);
            _playedCardName = -1;
            _playedCardColor = -1;
            _playedcard = false;
            return (true);
        }
        return (false);
    }
}
