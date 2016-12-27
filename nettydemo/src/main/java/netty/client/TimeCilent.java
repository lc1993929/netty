package netty.client;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.client.handler.TimeClientHandler;

/**
 * • 连接服务器 • 写数据到服务器 • 等待接受服务器返回相同的数据 • 关闭连接
 */
public class TimeCilent {
	public static void main(String[] args) throws Exception {
		new TimeCilent("localhost", 8080).start();
	}

	private final String host;
	private final int port;

	public TimeCilent(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void start() throws Exception {
		// EventLoopGroup可以理解为是一个线程池，这个线程池用来处理连接、接受数据、发送数据
		EventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap(); // 客户端引导类
		bootstrap.group(nioEventLoopGroup);// 多线程处理
		try {
			bootstrap.channel(NioSocketChannel.class);// 指定通道类型为NioServerSocketChannel，一种异步模式，OIO阻塞模式为OioServerSocketChannel
			bootstrap.remoteAddress(new InetSocketAddress(host, port));// 指定请求地址
			bootstrap.handler(new childChannelHandler());// 业务处理类
			ChannelFuture channelFuture = bootstrap.connect().sync();// 链接服务器.调用sync()方法会同步阻塞
			channelFuture.channel().closeFuture().sync();// 最后绑定客户端等待直到绑定完成，调用sync()方法会阻塞直到客服端完成绑定,然后客户端等待通道关闭，因为使用sync()，所以关闭操作也会被阻塞。
		} finally {
			// 退出，释放线程池资源
			nioEventLoopGroup.shutdownGracefully().sync();
		}
	}

	private class childChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			// 注册一个handler，处理之前正序执行handlerAdded方法，处理之后逆序执行handlerRemoved方法，如果出现异常执行exceptionCaught方法
			ch.pipeline().addLast(new TimeClientHandler());
		}
	}
}
