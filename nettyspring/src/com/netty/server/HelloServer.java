package com.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//注解方式注入bean，名字是helloServer
@Service("helloServer")
public class HelloServer {
	private static Logger log = Logger.getLogger(HelloServer.class);
	/**
	 * 服务端监听的端口地址
	 */
	private static final int portNumber = 7878;

	// 自动装备变量，spring会根据名字或者类型来装备这个变量，注解方式不需要set get方法了
	@Autowired
	private HelloServerInitializer helloServerInitializer;

	// 程序初始方法入口注解，提示spring这个程序先执行这里
	@PostConstruct
	public void serverStart() throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(helloServerInitializer);

			// 服务器绑定端口监听
			ChannelFuture f = b.bind(portNumber).sync();
			// 监听服务器关闭监听
			f.channel().closeFuture().sync();

			log.info("###########################################");
			// 可以简写为
			/* b.bind(portNumber).sync().channel().closeFuture().sync(); */
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}