import java.util.function.Consumer;

public interface IClient
{
    public void             setReceiveMessageCallBack(Consumer<ReceiveRequest> receivedMsg);
    public void             sendMessage(coincheProto.ServerMsg message);
    public int              getId();
    public boolean          getConnected();
}
