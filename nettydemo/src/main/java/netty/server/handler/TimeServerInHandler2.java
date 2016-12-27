package netty.server.handler;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerInHandler2 extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("in2");
		ByteBuf buf = (ByteBuf) msg;// 因为网络请求数据都为二进制，所以可以强转为字节数组。netty提供的bytebuf类可防止二次拷贝
		byte[] req = new byte[buf.readableBytes()];// 根据请求数据的大小创建一个字节数组
		buf.readBytes(req);// 将请求数据写入到字节数组中
		String body = new String(req, "UTF-8");
		System.out.println("接收客户端数据：" + body);
		// 向客户端写数据
		System.out.println("server开始向client发送数据");
		String currentTime = new Date(System.currentTimeMillis()).toString();
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		// (重要) 一旦调用write方法，后续的所有handler都将不再执行
		ctx.write(resp);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// 刷新后才将数据发出到socketChannel
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
