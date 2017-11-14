import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;

import io.netty.bootstrap.Bootstrap;
import  io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.InputMismatchException;
import java.util.Scanner;

public class NettyNetworkController implements INetworkController
{
    private boolean                     _running = true;
    private int                         _port = 0;
    private String                      _address = "";
    private coincheProto.PlayerMsg      _shellCmd = null;
    private Channel                     _chan = null;

    NettyNetworkController() throws InterruptedIOException
    {
        System.out.println("---   Welcome to Jcoinche   ---");
        System.out.println("Coinche game is a variant of the french card game Belotte.");
        //afficher les règles au démarrage.

        try
        {
            String          tmp;
            Scanner scanner = new Scanner(System.in);

            System.out.println("Please enter the host's ip address:");
            tmp = scanner.nextLine();
            if (tmp.equalsIgnoreCase(""))
                System.exit(1);
            _address = tmp;
            System.out.println("Please enter the host's port:");
            tmp = scanner.nextLine();
            _port = new Integer(tmp);
            this.start();
        }
        catch (Exception e)
        {
            System.out.println("Bad argument.");
        }
    }

    private void                    start()
    {
        EventLoopGroup              group = new NioEventLoopGroup();

        try
        {
            Bootstrap                   b = new Bootstrap();
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                { _running = false; }
            };

            b.group(group).channel(NioSocketChannel.class).handler(new NettyInitializer(runnable));
            _chan = b.connect(_address, _port).sync().channel();
            Scanner scan = new Scanner(System.in);
            while (_running)
            {
                try
                {
                    if (scan.hasNext())
                        _shellCmd = CoincheEncodeMessage.getInstance(runnable).encode(scan.nextLine());
                    else
                        _running = false;
                    if (_shellCmd != null)
                    {
                        _chan.writeAndFlush(_shellCmd);
                        _shellCmd = null;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    _running = false;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Something went wrong, you probably mistaken when typing the address and port.");
            System.out.println("Client Closing.........");
        }
        finally
        { group.shutdownGracefully(); }
    }
}
