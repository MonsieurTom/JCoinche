import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.function.Consumer;

public class NettyHandler extends SimpleChannelInboundHandler<coincheProto.ServerMsg>
{
    private static final ChannelGroup   allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private Runnable                    _run;

    NettyHandler(Runnable runnable)
    {
        _run = runnable;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext chc)
    {
        allChannels.add(chc.channel());
        System.out.println("---   Connection done   ---");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext chc)
    {
        allChannels.remove(chc.channel());
        System.out.println("---   Server deconnection   ---");
        System.out.println("---   Closing the program   ---");
        System.exit(1);
    }

    @Override
    public void channelRead0(ChannelHandlerContext chc, coincheProto.ServerMsg msg)
    {
        CoincheDecodeMessage.getInstance().decodeMessage(msg, _run);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext chc, Throwable throwable)
    {
        System.out.println("Exception caught from the server.");
        System.out.println("Closing connection to the server.");
        chc.close();
        System.out.println("Connection closed");
        throwable.printStackTrace();
        _run.run();
    }
}
