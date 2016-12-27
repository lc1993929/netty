package netty.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TimeClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
	// 客户端连接服务器后被调用
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端连接服务器，开始发送数据……");
		byte[] req = "client向server发送的数据".getBytes();// 消息
		ByteBuf byteBuf = Unpooled.buffer(req.length);// 根据请求数据的大小创建容器
		byteBuf.writeBytes(req);// 将数据写入到容器中
		ctx.writeAndFlush(byteBuf);// 输出到server
	}

	// • 从服务器接收到数据后调用
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		System.out.println("client 读取server数据..");
		// 服务端返回消息后
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];// 根据请求数据的大小创建一个字节数组
		buf.readBytes(req);// 将请求数据写入到字节数组中
		String body = new String(req, "UTF-8");
		System.out.println("服务端数据为 :" + body);
		ctx.close();
	}

	// • 发生异常时被调用
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("client exceptionCaught..");
		// 释放资源
		ctx.close();
	}
}