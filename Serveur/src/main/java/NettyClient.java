import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

public class NettyClient extends AClient
{
    private ChannelHandlerContext                       _context;
    private Consumer<ReceiveRequest>                    _runRcv;
    private boolean                                     _connected;

    NettyClient(ChannelHandlerContext chc, Consumer<ReceiveRequest> runRcv, int id)
    {
        setReceiveMessageCallBack(runRcv);
        _connected = true;
        _id = id;
        _context = chc;
    }

    public void setReceiveMessageCallBack(Consumer<ReceiveRequest> receivedMessage) {
        this._runRcv = receivedMessage;
    }

    public void setConnected(boolean state)
    {
        _connected = state;
    }

    public boolean getConnected()
    {
        return (_connected);
    }

    public Consumer<ReceiveRequest> getCallBack()
    { return (_runRcv); }

    public void sendMessage(coincheProto.ServerMsg message)
    {
        this._context.writeAndFlush(message);
    }

    public ChannelHandlerContext    getContext()
    { return (_context); }
}
