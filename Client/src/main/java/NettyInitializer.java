import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class NettyInitializer extends ChannelInitializer<SocketChannel>
{
    private Runnable            _run;

    NettyInitializer(Runnable runnable)
    {
        _run = runnable;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception
    {
        ChannelPipeline pipe = channel.pipeline();

        pipe.addLast("FramerDecoder", new ProtobufVarint32FrameDecoder());
        pipe.addLast("Decoder", new ProtobufDecoder(coincheProto.ServerMsg.getDefaultInstance()));
        pipe.addLast("FramerEncoder", new ProtobufVarint32LengthFieldPrepender());
        pipe.addLast("Encoder", new ProtobufEncoder());
        pipe.addLast("Handler", new NettyHandler(_run));
    }
}
