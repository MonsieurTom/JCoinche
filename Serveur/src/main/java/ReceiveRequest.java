public class ReceiveRequest {
    public int                      _clientId;
    public coincheProto.PlayerMsg   _msg;

    ReceiveRequest(int id, coincheProto.PlayerMsg msg)
    {
        this._clientId = id;
        this._msg = msg;
    }
}