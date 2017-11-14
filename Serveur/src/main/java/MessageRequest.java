public class MessageRequest {
    public int                      _clientId;
    public coincheProto.ServerMsg   _msg;

    MessageRequest(int id, coincheProto.ServerMsg msg)
    {
        this._clientId = id;
        this._msg = msg;
    }
}