import java.util.ArrayList;
import java.util.function.Consumer;

public class ControllerDaemon
{
    private ArrayList<IClient>          _clients = new ArrayList<>();
    private INetworkController          _networkController;
    private IGameController             _gameController;

    ControllerDaemon()
    {
        Consumer<IClient> fctAdd = new Consumer<IClient>()
        {
            @Override
            public void accept(IClient client)
            {
                addClient(client);
            }
        };

        Consumer<IClient> fctDel = new Consumer<IClient>()
        {
            @Override
            public void accept(IClient client)
            {
                delClient(client);
            }
        };

        Consumer<ReceiveRequest> fctRcv = new Consumer<ReceiveRequest>()
        {
            @Override
            public void accept(ReceiveRequest s)
            {
                receiveMessage(s);
            }
        };

        _gameController = new CoincheController();
        Consumer<MessageRequest> sendMsgCB = new Consumer<MessageRequest>() {
            @Override
            public void accept(MessageRequest messageRequest)
            {
                for (IClient client : _clients)
                {
                    if (client.getId() == messageRequest._clientId)
                        client.sendMessage(messageRequest._msg);
                }
            }
        };
        _gameController.setSendMessageCallBack(sendMsgCB);

        _networkController = new NettyNetworkController(fctAdd, fctDel, fctRcv);
    }

    public void                 addClient(IClient newClient)
    {
        System.out.println("---   new connection   ---");

        Consumer<ReceiveRequest>    reveiveMsg = new Consumer<ReceiveRequest>() {
            @Override
            public void accept(ReceiveRequest msg) {
                _gameController.receiveMsg(msg);
            }
        };

        this._clients.add(newClient);
        newClient.setReceiveMessageCallBack(reveiveMsg);
        _gameController.addPlayer(newClient);
    }

    public void                 delClient(IClient client)
    {
        System.out.println("---   deconnection   ---");
        this._clients.remove(client);
    }

    public void                 receiveMessage(ReceiveRequest message)
    {
    }
}
