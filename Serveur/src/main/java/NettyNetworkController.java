import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Scanner;
import java.util.function.Consumer;

public class NettyNetworkController implements INetworkController
{
    private static int                          _port = 4242;
    private Consumer<IClient>                   _runAdd;
    private Consumer<IClient>                   _runDel;
    private Consumer<ReceiveRequest>    _runRcv;

    NettyNetworkController(Consumer<IClient> runAdd, Consumer<IClient> runDel, Consumer<ReceiveRequest> runRcv)
    {
        this.setDelClientCallBack(runDel);
        this.setNewClientCallBack(runAdd);
        this._runRcv = runRcv;
        Scanner scan = new Scanner(System.in);

        try
        {
            System.out.println("---   Server initialized   ---\nPlease specify a port : ");
            _port = scan.nextInt();
        }
        catch (Exception e)
        {
            System.out.println("Enter a proper port please.");
            System.exit(1);
        }
        this.start();
    }

    private void                start()
    {
        EventLoopGroup          bossGroup = new NioEventLoopGroup();
        EventLoopGroup          workerGroup = new NioEventLoopGroup();

        try
        {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new NettyNetworkInitializer(this._runAdd, this._runDel, this._runRcv));
            ChannelFuture futureChan = b.bind(_port).sync();
            futureChan.channel().closeFuture().sync();
        }
        catch (Exception e)
        {
            System.out.println("Something went wrong, try another port");
            System.out.println("Server closing......");
        }
        finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void                 setNewClientCallBack(Consumer<IClient> runAdd)
    {
        _runAdd = runAdd;
    }

    public void                 setDelClientCallBack(Consumer<IClient> runDel) { _runDel = runDel; }
}
