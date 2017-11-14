import java.io.InterruptedIOException;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            INetworkController controller = new NettyNetworkController();
        }
        catch (InterruptedIOException e)
        {
            e.printStackTrace();
        }
    }
}
