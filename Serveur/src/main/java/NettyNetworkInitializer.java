import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.function.Consumer;

public class NettyNetworkInitializer extends ChannelInitializer<SocketChannel>
{
    private Consumer<IClient>               _runAdd;
    private Consumer<IClient>               _runDel;
    private Consumer<ReceiveRequest>        _runRcv;

    NettyNetworkInitializer(Consumer<IClient> runAdd, Consumer<IClient> runDel, Consumer<ReceiveRequest> runRcv)
    {
        this._runAdd = runAdd;
        this._runDel = runDel;
        this._runRcv = runRcv;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception
    {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("FramerDecoder", new ProtobufVarint32FrameDecoder());
        pipeline.addLast("Decoder", new ProtobufDecoder(coincheProto.PlayerMsg.getDefaultInstance()));
        pipeline.addLast("FramerEncoder", new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast("Encoder", new ProtobufEncoder());
        pipeline.addLast("handler", new NettyHandler(this._runAdd, this._runDel, this._runRcv));
    }
}
