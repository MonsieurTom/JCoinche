import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.function.Consumer;

public class NettyHandler extends SimpleChannelInboundHandler<coincheProto.PlayerMsg>
{
    private static int                      _cptClients = 0;
    private ArrayList<NettyClient>          _clients = new ArrayList<>();
    private Consumer<IClient>               _runAdd;
    private Consumer<IClient>               _runDel;
    private Consumer<ReceiveRequest>                _runRcv;
    private static final ChannelGroup       allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    NettyHandler(Consumer<IClient> runAdd, Consumer<IClient> runDel, Consumer<ReceiveRequest> runRcv)
    {
        this._runAdd = runAdd;
        this._runDel = runDel;
        this._runRcv = runRcv;
    }

    @Override
    public void          handlerAdded(ChannelHandlerContext chc) throws Exception
    {
        allChannels.add(chc.channel());
        IClient client = new NettyClient(chc, this._runRcv, _cptClients);
        this._clients.add((NettyClient) client);
        this._runAdd.accept(client);
        _cptClients++;
    }

    @Override
    public void         handlerRemoved(ChannelHandlerContext chc) throws Exception
    {
        allChannels.remove(chc.channel());
        for (NettyClient client: _clients)
        {
            if (client.getContext().equals(chc))
            {
                client.setConnected(false);
                this._runDel.accept(client);
                break;
            }
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext chc,  coincheProto.PlayerMsg message) throws Exception
    {
        for (NettyClient client : _clients)
        {
            if (client.getContext() == chc)
            {
                ReceiveRequest req = new ReceiveRequest(client.getId(), message);
                client.getCallBack().accept(req);
            }
        }
    }

    @Override
    public void         exceptionCaught(ChannelHandlerContext chc, Throwable thrown) throws Exception
    {
        System.out.println("Exception caught on : " + chc.channel().remoteAddress());
        System.out.println(chc.channel().remoteAddress() + " being close");
        chc.close();
        System.out.println("Client closed\n");
        thrown.printStackTrace();
    }

    @Override
    public void         channelInactive(ChannelHandlerContext chc) throws  Exception
    {
        System.out.println("Connexion error on " + chc.channel().remoteAddress());
        System.out.println(chc.channel().remoteAddress() + " being kick");
        chc.write("Network error, you'r being kick...\n").addListener(ChannelFutureListener.CLOSE);
    }
}
