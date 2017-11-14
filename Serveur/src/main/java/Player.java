import sun.util.resources.cldr.ga.CalendarData_ga_IE;

import java.util.ArrayList;

public class Player
{
    private IClient             _client;
    private int                 _clientId;
    private int                 _team;
    private ArrayList<Card>     _hand = new ArrayList<>();
    private int                 _playerId;
    private int                 _deal;
    private int                 _score;

    Player(int id)
    {
        _client = null;
        _team = -1;
        _playerId = -1;
        _clientId = id;
        _score = 0;
        _deal = -1;
        _playerId = id;
    }

    Player(IClient client)
    {
        _client = client;
        _team = -1;
        _playerId = -1;
        _clientId = client.getId();
        _score = 0;
        _deal = -1;
        _playerId = client.getId();
    }

    public void                 set_playerId(int id)
    {
        _playerId = id;
    }

    public boolean              getConnected()
    { return (_client.getConnected()); }

    public int                  getClientId()
    { return (_clientId); }

    public int                  getPlayerId()
    {
        return (_playerId);
    }

    public int                  getTeam()
    { return (_team); }

    public void set_team(int _team) {
        this._team = _team;
    }

    public ArrayList<Card>      getHand()
    { return (_hand); }

    public boolean              removeCard(Card card)
    {
        for (Card c : _hand)
        {
            if (c.color == card.color && c.name == card.name)
            {
                _hand.remove(c);
                return (true);
            }
        }
        return (true);
    }

    public void                 drawCard(Card card)
    {
        _hand.add(card);
    }

    public boolean              haveCard(Card card)
    {
        for (Card c : _hand)
        {
            if (c.color == card.color && c.name == card.name)
                return (true);
        }
        return (false);
    }

    public void                 addScore(int score)
    {
        _score += score;
    }

    public int                  getScore()
    {
        return (_score);
    }
}
