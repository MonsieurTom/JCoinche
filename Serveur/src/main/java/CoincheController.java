import java.util.ArrayList;
import java.util.function.Consumer;

public class CoincheController implements IGameController
{
    private ArrayList<Player>           _players = new ArrayList<>();
    private ArrayList<CoincheGame>      _games = new ArrayList<>();
    private Consumer<MessageRequest>    _sendMsg;

    public void addPlayer(IClient client)
    {
        Player  newplayer = new Player(client);

        this.sendPlayerState(newplayer, coincheProto.PlayerState.State.WAITINGGAME);
        _players.add(newplayer);
        if (_players.size() == 4)
            launchGame();
    }

    public void setSendMessageCallBack(Consumer<MessageRequest> sendMessage)
    {
        _sendMsg = sendMessage;
    }

    private void launchGame()
    {
        Player player1 = _players.get(0);
        Player player2 = _players.get(1);
        Player player3 = _players.get(2);
        Player player4 = _players.get(3);

        _players.remove(player1);
        _players.remove(player2);
        _players.remove(player3);
        _players.remove(player4);

        CoincheGame newGame = new CoincheGame(player1, player2, player3, player4, _sendMsg);

        _games.add(newGame);
        newGame.start();
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

    public void     receiveMsg(ReceiveRequest msg)
    {
        for (CoincheGame game : _games)
        {
            if (game.isInGame(msg._clientId))
            {
                game.msgFifoLock.lock();
                game.msgFifo.add(msg);
                game.msgFifoLock.unlock();
            }
        }
    }
}
