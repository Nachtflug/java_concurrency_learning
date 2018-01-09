package nio.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf, List<Object> list) throws Exception {
        int readableBytes = byteBuf.readableBytes();
        byte[] arr = new  byte[readableBytes];
        byteBuf.getBytes(byteBuf.readerIndex(), arr, 0, readableBytes);
        MessagePack msgpack = new MessagePack();
        list.add(msgpack.read(arr));


    }
}
