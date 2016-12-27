package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.server.handler.TimeServerHandler;
import netty.server.handler.TimeServerInHandler1;
import netty.server.handler.TimeServerInHandler2;
import netty.server.handler.TimeServerOutHandler1;
import netty.server.handler.TimeServerOutHandler2;

public class TimeServer {

	public static void main(String[] args) throws InterruptedException {
		int port = 8080;
		new TimeServer().bind(port);
	}

	public void bind(int port) throws InterruptedException {
		// 配置nio线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();// 主线程，连接线成
		EventLoopGroup workerGroup = new NioEventLoopGroup();// 处理线程组
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup);// server端引导类,管理两个线程组
		// 设置TCP协议的参数
		try {
			bootstrap.channel(NioServerSocketChannel.class);// 指定通道类型为NioServerSocketChannel，一种异步模式，OIO阻塞模式为OioServerSocketChannel
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);// BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
			bootstrap.childHandler(new childChannelHandler());// 设置childHandler执行所有的连接请求
			ChannelFuture channelFuture = bootstrap.bind(port).sync();// 绑定端口，同步等待成功
			channelFuture.channel().closeFuture().sync();// 最后绑定服务器等待直到绑定完成，调用sync()方法会阻塞直到服务器完成绑定,然后服务器等待通道关闭，因为使用sync()，所以关闭操作也会被阻塞。
		} finally {
			// 退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private class childChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			// 注册一个handler，处理之前正序执行handlerAdded方法，处理之后逆序执行handlerRemoved方法，如果出现异常执行exceptionCaught方法
			ch.pipeline().addLast(new TimeServerHandler()).addLast(new TimeServerInHandler1())
					.addLast(new TimeServerOutHandler1()).addLast(new TimeServerOutHandler2())
					.addLast(new TimeServerInHandler2());
			// pipeline.addLast("decoder", new HttpRequestDecoder());
			// pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
			// pipeline.addLast("encoder", new HttpResponseEncoder());
		}

	}
	/**
	 * 在使用Handler的过程中，需要注意：
	 * 
	 * 1、ChannelInboundHandler之间的传递，通过调用ctx.fireChannelRead(msg)
	 * 实现；调用ctx.write(msg) 将传递到ChannelOutboundHandler。
	 * 
	 * 2、ctx.write()方法执行后，需要调用flush()方法才能令它立即执行。
	 * 
	 * 3、流水线pipeline中outhandler不能放在最后，否则不生效
	 * 
	 * 4、Handler的销毁处理放在最后一个处理。
	 */
}
