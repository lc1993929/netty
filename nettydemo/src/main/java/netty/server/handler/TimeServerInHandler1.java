package netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerInHandler1 extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("in1");
		ctx.fireChannelRead(msg);// 通知执行下一个InBoundHandler
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
