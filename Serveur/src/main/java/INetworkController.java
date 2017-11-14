import java.util.function.Consumer;

public interface INetworkController
{
    public void setNewClientCallBack(Consumer<IClient> runAdd);
    public void setDelClientCallBack(Consumer<IClient> runDel);
}
