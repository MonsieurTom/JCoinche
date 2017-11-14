import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequestDecoder;
import sun.awt.Mutex;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CoincheGame extends Thread
{
    private final String[]                  _cardName = {"ace", "seven", "height", "nine", "ten", "jack", "queen", "king"};
    private final String[]                  _trumps = {"none", "spade", "hearth", "diamond", "club", "all"};
    private final String[]                  _cardColor = {"spade", "hearth", "diamond", "club"};

    private boolean                             _leaved = false;
    private Deck                                _deck = new Deck();
    private ArrayList<Player>                   _players = new ArrayList<>();
    private int                                 _playerTurn;
    private coincheProto.TRUMP                  _trump;
    private Consumer<MessageRequest>            _sendMsg;
    public ArrayList<ReceiveRequest>            msgFifo = new ArrayList<>();
    public Mutex                                msgFifoLock = new Mutex();
    private ArrayList<Card>                     _stack = new ArrayList<>();
    private int                                 _nbTurn;
    private int                                 _firstPlayer;
    private int                                 _dealValue;
    private Player                              _dealer;

    CoincheGame(Player player1, Player player2, Player player3, Player player4, Consumer<MessageRequest> sendMsg)
    {
        this.setSendMsg(sendMsg);
        player1.set_team(1);
        player2.set_team(2);
        player3.set_team(1);
        player4.set_team(2);

        player1.set_playerId(0);
        player2.set_playerId(1);
        player3.set_playerId(2);
        player4.set_playerId(3);

        this.sendPlayerState(player1, coincheProto.PlayerState.State.PLAYING);
        this.sendPlayerState(player2, coincheProto.PlayerState.State.PLAYING);
        this.sendPlayerState(player3, coincheProto.PlayerState.State.PLAYING);
        this.sendPlayerState(player4, coincheProto.PlayerState.State.PLAYING);

        _players.add(player1);
        _players.add(player2);
        _players.add(player3);
        _players.add(player4);

        _playerTurn = 0;
        _nbTurn = 0;
        _firstPlayer = 0;
        _dealValue = 0;
        System.out.println("Starting a game on thread " + Thread.currentThread().getName());
    }

    @Override
    public void run() {
        try {
            distribute();

            int noDealTurn = 0;

            while (true) {
                Player currentPlayer = _players.get(_playerTurn);
                while (true) {
                    sendPlayerState(currentPlayer, coincheProto.PlayerState.State.DEAL);
                    coincheProto.PlayerMsg playerMsg;
                    if ((playerMsg = this.waitFor(currentPlayer, coincheProto.PlayerMsg.TypeMessage.DEAL)) == null)
                        break ;
                    coincheProto.Deal deal = playerMsg.getDeal();
                    if (deal.getDeal() == -1) {
                        this.sendActionValidation(currentPlayer, true);
                        this.sendPlayerDealed(currentPlayer, deal.getDeal(), deal.getTrump());
                        noDealTurn++;
                        break;
                    }
                    else if (deal.getDeal() > _dealValue)
                    {
                        noDealTurn = 0;
                        _dealValue = deal.getDeal();
                        _dealer = currentPlayer;
                        setTrump(deal.getTrump());
                        this.sendActionValidation(currentPlayer, true);
                        this.sendPlayerDealed(currentPlayer, deal.getDeal(), deal.getTrump());
                        break;
                    }
                    else
                        this.sendActionValidation(currentPlayer, false);
                }

                if (_leaved)
                    break;
                if (noDealTurn == 3)
                    break;
                _playerTurn++;
                if (_playerTurn == 4) {
                    _playerTurn = 0;
                }
            }

            if (!_leaved)
                this.sendDealWinner();

            _playerTurn = 0;
            _firstPlayer = 0;
            if (!_leaved) {
                while (true) {
                    Player currentPlayer = _players.get(_playerTurn);
                    System.out.println("Thread " + Thread.currentThread().getName() + ", Player" + _playerTurn + " turn");
                    if (checkConnected())
                        break;
                    while (true) {
                        if (checkConnected())
                            break;
                        sendPlayerState(currentPlayer, coincheProto.PlayerState.State.YOURTURN);
                        coincheProto.PlayerMsg playerMsg;
                        if ((playerMsg = this.waitFor(currentPlayer, coincheProto.PlayerMsg.TypeMessage.PLAYCARD)) == null)
                            break;
                        coincheProto.PlayCard playedCardBuff = playerMsg.getPlayCard();
                        Card playedCard = decodePlayedCard(playedCardBuff);

                        if (this.validCard(playedCard, currentPlayer)) {
                            _stack.add(playedCard);
                            this.sendActionValidation(currentPlayer, true);
                            this.sendPlayedCard(playedCard, currentPlayer);
                            System.out.println("Thread " + Thread.currentThread().getName() + ", Player" + _playerTurn + " played : " + playedCard.name + " " + playedCard.color);
                            break;
                        } else
                            this.sendActionValidation(currentPlayer, false);
                    }
                    if (checkConnected())
                        break;
                    _playerTurn++;
                    if (_playerTurn == 4) {
                        _playerTurn = 0;
                    }
                    if (_stack.size() == 4) {
                        _playerTurn = endStack();
                        _firstPlayer = _playerTurn;
                        _nbTurn++;
                    }
                    if (_nbTurn == 8)
                        break;
                }
            }
            endGame();
        }
        catch (Exception e)
        {
            System.out.println("Game crashed : Error n°" + e.hashCode() + " " + e.getMessage());
            for (Player player : _players)
            {
                this.sendPlayerState(player, coincheProto.PlayerState.State.LEAVE);
            }
        }
    }

    private boolean     checkConnected()
    {
        for (int i = 0; i < _players.size(); i++)
        {
            if (!_players.get(i).getConnected())
            {
                _leaved = true;
                //for (int j = 0; i < _players.size(); j++)
                //{
                //    if (j != i)
                //        sendPlayerState(_players.get(j), coincheProto.PlayerState.State.LEAVE);
                //}
                return (true);
            }
        }
        return (false);
    }

    public void         setTrump(coincheProto.TRUMP newTrump)
    {
        _trump = newTrump;
    }

    private void        endGame()
    {
        if (!_leaved) {
            System.out.println("Thread " + Thread.currentThread().getName() + " End of the game");

            int team1 = _players.get(0).getScore() + _players.get(2).getScore();
            int team2 = _players.get(1).getScore() + _players.get(3).getScore();

            coincheProto.ServerMsg.Builder msg = coincheProto.ServerMsg.newBuilder();
            coincheProto.GameWinner.Builder data = coincheProto.GameWinner.newBuilder();

            data.setTeam1Score(team1);
            data.setTeam2Score(team2);
            data.setDealerTeam(_dealer.getTeam());
            data.setWinDeal(_dealValue <= getTeamScore(_dealer.getTeam()));

            msg.setWinner(data.build());
            msg.setTypeMessage(coincheProto.ServerMsg.TypeMessage.WINNER);
            for (Player player : _players) {
                _sendMsg.accept(new MessageRequest(player.getClientId(), msg.build()));
            }
        }
        else
        {
            for (Player player : _players)
                this.sendPlayerState(player, coincheProto.PlayerState.State.LEAVE);
        }

    }

    private int    endStack()
    {
        int         winnerPos = 0;
        boolean     winnerTrump = false;
        int         winnerValue = 0;
        int         total = 0;
        int         tmpValue = 0;
        int         pos = 0;
        coincheProto.CARD_COLOR     stackColor = _stack.get(0).color;

        for (Card card : _stack)
        {
            tmpValue = getCardValue(card);
            total += tmpValue;

            if (_trump != coincheProto.TRUMP.TALL && card.color != stackColor && !sameColor(_trump, card.color))
            {
                ++pos;
                continue;
            }

            if ((_trump == coincheProto.TRUMP.TALL && tmpValue > winnerValue) ||
                    (sameColor(_trump, card.color) && !winnerTrump) ||
                    (winnerTrump && sameColor(_trump, card.color) && tmpValue > winnerValue) ||
                    tmpValue > winnerValue)
            {
                winnerTrump = sameColor(_trump, card.color);
                winnerPos = pos;
                winnerValue = tmpValue;
            }
            ++pos;
        }

        Player player = _players.get((_firstPlayer + winnerPos) % 4);
        player.addScore(total);
        this.sendStackWinner(player, total);
        System.out.println("Thread " + Thread.currentThread().getName() + " End of stack, player" + player.getPlayerId() + " won " + total + "pts");
        _stack.clear();
        return (_firstPlayer + winnerPos) % 4;
    }

    public int                     getCardValue(Card card) {
        switch (_trump)
        {
            case TALL:
                switch (card.name)
                {
                    case ACE:
                        return 7;
                    case SEVEN:
                        return 0;
                    case HEIGHT:
                        return 0;
                    case NINE:
                        return 9;
                    case TEN:
                        return 5;
                    case JACK:
                        return 14;
                    case QUEEN:
                        return 2;
                    case KING:
                        return 3;
                }

            case TNONE:
                switch (card.name)
                {
                    case ACE:
                        return 19;
                    case SEVEN:
                        return 0;
                    case HEIGHT:
                        return 0;
                    case NINE:
                        return 0;
                    case TEN:
                        return 10;
                    case JACK:
                        return 2;
                    case QUEEN:
                        return 3;
                    case KING:
                        return 4;
                }

            default:
                switch (card.name)
                {
                    case ACE:
                        return (sameColor(_trump, card.color) ? 11 : 11);
                    case SEVEN:
                        return (sameColor(_trump, card.color) ? 0 : 0);
                    case HEIGHT:
                        return (sameColor(_trump, card.color) ? 0 : 0);
                    case NINE:
                        return (sameColor(_trump, card.color) ? 14 : 0);
                    case TEN:
                        return (sameColor(_trump, card.color) ? 10 : 10);
                    case JACK:
                        return (sameColor(_trump, card.color) ? 20 : 2);
                    case QUEEN:
                        return (sameColor(_trump, card.color) ? 3 : 3);
                    case KING:
                        return (sameColor(_trump, card.color) ? 4 : 4);
                }
        }
        return (0);
    }

    public boolean                 sameColor(coincheProto.TRUMP trump, coincheProto.CARD_COLOR color)
    {
        if (trump == coincheProto.TRUMP.TDIAMOND && color == coincheProto.CARD_COLOR.DIAMOND)
            return (true);
        if (trump == coincheProto.TRUMP.THEARTH && color == coincheProto.CARD_COLOR.HEARTH)
            return (true);
        if (trump == coincheProto.TRUMP.TSPADE && color == coincheProto.CARD_COLOR.SPADE)
            return (true);
        if (trump == coincheProto.TRUMP.TCLUB && color == coincheProto.CARD_COLOR.CLUB)
            return (true);
        return false;
    }

    private boolean                 validCard(Card card, Player player)
    {
        if (!player.haveCard(card))
            return false;
        return true;
    }

    private coincheProto.PlayerMsg  waitFor(Player currentPlayer, coincheProto.PlayerMsg.TypeMessage type)
    {
        while (true)
        {
            if (checkConnected())
                return (null);
            msgFifoLock.lock();
            if (msgFifo.size() > 0)
            {
                ReceiveRequest  msg = msgFifo.get(0);
                msgFifo.remove(msg);
                if (msg._msg.getTypeMessage() == type && msg._clientId == currentPlayer.getClientId())
                {
                    msgFifoLock.unlock();
                    return msg._msg;
                }
                else {
                    this.sendActionValidation(msg._clientId, false);
                }
            }
            msgFifoLock.unlock();
            try {
                Thread.sleep(10);
            }
            catch (Exception e)
            {
                System.out.println("Game crashed after : " + e.getMessage() + " Error n°" + e.hashCode());
                for (Player player : _players) {
                    sendPlayerState(player, coincheProto.PlayerState.State.LEAVE);
                    currentThread().interrupt();
                }
            }
        }
    }

    private void    distribute()
    {
        for(Player player : _players)
        {
            for (int card = 0; card < 8; card++)
            {
                Card    newCard = _deck.getRandCard();

                this.sendDrawCard(player, newCard);
                player.drawCard(newCard);
            }
        }
    }

    private void    sendDrawCard(Player player, Card card)
    {
        coincheProto.ServerMsg.Builder      msg = coincheProto.ServerMsg.newBuilder();
        coincheProto.DrawCard.Builder       data = coincheProto.DrawCard.newBuilder();

        data.setColor(card.color);
        data.setName(card.name);

        msg.setTypeMessage(coincheProto.ServerMsg.TypeMessage.DRAWCARD);
        msg.setDrawCard(data.build());

        _sendMsg.accept(new MessageRequest(player.getClientId(), msg.build()));
    }

    private void    sendPlayerState(Player player, coincheProto.PlayerState.State type)
    {
        coincheProto.ServerMsg.Builder      msg = coincheProto.ServerMsg.newBuilder();
        coincheProto.PlayerState.Builder    data = coincheProto.PlayerState.newBuilder();

        data.setState(type);
        data.setPlayer(player.getPlayerId());
        data.setTeam(player.getTeam());

        msg.setTypeMessage(coincheProto.ServerMsg.TypeMessage.PLAYERSTATE);
        msg.setPlayerState(data.build());

        _sendMsg.accept(new MessageRequest(player.getClientId(), msg.build()));
    }

    private void    sendActionValidation(Player player, boolean validation)
    {
        coincheProto.ServerMsg.Builder          msg = coincheProto.ServerMsg.newBuilder();
        coincheProto.ActionValidation.Builder   data = coincheProto.ActionValidation.newBuilder();

        data.setAccepted(validation);

        msg.setTypeMessage(coincheProto.ServerMsg.TypeMessage.ACTIONVALIDATION);
        msg.setActionValidation(data.build());

        _sendMsg.accept(new MessageRequest(player.getClientId(), msg.build()));
    }

    private void    sendActionValidation(int id, boolean validation)
    {
        coincheProto.ServerMsg.Builder          msg = coincheProto.ServerMsg.newBuilder();
        coincheProto.ActionValidation.Builder   data = coincheProto.ActionValidation.newBuilder();

        data.setAccepted(validation);

        msg.setTypeMessage(coincheProto.ServerMsg.TypeMessage.ACTIONVALIDATION);
        msg.setActionValidation(data.build());

        _sendMsg.accept(new MessageRequest(id, msg.build()));
    }

    private void    sendPlayedCard(Card card, Player player)
    {
        coincheProto.PlayedCard.Builder     playedCard = coincheProto.PlayedCard.newBuilder();
        coincheProto.ServerMsg.Builder      msg = coincheProto.ServerMsg.newBuilder();

        playedCard.setColor(card.color);
        playedCard.setName(card.name);
        playedCard.setPlayer(player.getPlayerId());
        playedCard.setTeam(player.getTeam());


        msg.setTypeMessage(coincheProto.ServerMsg.TypeMessage.PLAYEDCARD);
        msg.setPlayedCard(playedCard.build());

        for (Player target : _players)
        {
            _sendMsg.accept(new MessageRequest(target.getClientId(), msg.build()));
        }
    }

    private void    sendPlayerDealed(Player player, int deal, coincheProto.TRUMP trump)
    {
        coincheProto.PlayerDealed.Builder   data = coincheProto.PlayerDealed.newBuilder();
        coincheProto.ServerMsg.Builder      msg = coincheProto.ServerMsg.newBuilder();

        data.setPlayer(player.getPlayerId());
        data.setTeam(player.getTeam());
        data.setDeal(deal);
        data.setTrump(trump);

        msg.setTypeMessage(coincheProto.ServerMsg.TypeMessage.PLAYERDEALED);
        msg.setPlayerDealed(data.build());

        for (Player target : _players)
        {
            _sendMsg.accept(new MessageRequest(target.getClientId(), msg.build()));
        }
    }

    private void    sendDealWinner()
    {
        coincheProto.ServerMsg.Builder  msg = coincheProto.ServerMsg.newBuilder();
        coincheProto.DealWinner.Builder data = coincheProto.DealWinner.newBuilder();

        data.setDeal(_dealValue);
        data.setPlayer(_dealer.getPlayerId());
        data.setTeam(_dealer.getTeam());
        data.setTrump(_trump);

        msg.setTypeMessage(coincheProto.ServerMsg.TypeMessage.DEALWINNER);
        msg.setDealWinner(data.build());

        for (Player player : _players)
        {
            _sendMsg.accept(new MessageRequest(player.getClientId(), msg.build()));
        }
    }

    private void     sendStackWinner(Player winner, int score)
    {
        coincheProto.ServerMsg.Builder      msg = coincheProto.ServerMsg.newBuilder();
        coincheProto.StackWinner.Builder    data = coincheProto.StackWinner.newBuilder();

        data.setPlayer(winner.getPlayerId());
        data.setTeam(winner.getTeam());
        data.setScore(score);

        msg.setTypeMessage(coincheProto.ServerMsg.TypeMessage.STACKWINNER);
        msg.setStackWinner(data.build());

        for (Player player : _players) {
            _sendMsg.accept(new MessageRequest(player.getClientId(), msg.build()));
        }
    }

    public Card                decodePlayedCard(coincheProto.PlayCard card)
    {
        return (new Card(card.getColor(), card.getName()));
    }

    public Deck                 getDeck()
    { return (this._deck); }

    public ArrayList<Player>    getPlayers()
    { return (_players); }

    public Player               getPlayer(int index)
    { return (_players.get(index)); }

    private int                 getTeamScore(int team)
    {
        int                 score = 0;

        for (Player player : _players)
        {
            if (player.getTeam() == team)
            {
                score += player.getScore();
            }
        }
        return score;
    }

    public void                 setSendMsg(Consumer<MessageRequest> sendMsg)
    {
        _sendMsg = sendMsg;
    }

    public boolean              isInGame(int clientId)
    {
        for (Player player : _players)
        {
            if (player.getClientId() == clientId)
                return (true);
        }
        return (false);
    }
}
