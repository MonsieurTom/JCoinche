import java.util.function.Consumer;

public interface IGameController
{
    public void         addPlayer(IClient client);
    public void         receiveMsg(ReceiveRequest msg);
    public void         setSendMessageCallBack(Consumer<MessageRequest> sendMessage);
}
