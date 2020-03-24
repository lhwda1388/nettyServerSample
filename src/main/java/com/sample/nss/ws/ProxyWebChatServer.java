package com.sample.nss.ws;

import com.sample.nss.chat.ChatMessageCodec;
import com.sample.nss.chat.ChatServerHandler;
import com.sample.nss.http.HttpNotFoundHandler;
import com.sample.nss.http.HttpStaticFileHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SystemPropertyUtil;

public class ProxyWebChatServer {
	private final static int BOSS_THREADS = 3;
	private final static int MAX_WORKER_THREADS = 12;
	private final static int SO_BACKLOG_NUM = 300;

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_THREADS);
        EventLoopGroup workerGroup = new NioEventLoopGroup(calculateThreadCount());
        EventLoopGroup bossGroup2 = new NioEventLoopGroup(BOSS_THREADS);
        EventLoopGroup workerGroup2 = new NioEventLoopGroup(calculateThreadCount());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, SO_BACKLOG_NUM);
            b.handler(new LoggingHandler(LogLevel.DEBUG));
            b.childHandler(new ChannelInitializer<Channel>() {
            	@Override
            	protected void initChannel(Channel ch) throws Exception {
            		ChannelPipeline p = ch.pipeline();
            		p.addLast(new LineBasedFrameDecoder(1024, true, true));
        			p.addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8));
        			p.addLast(new ChatMessageCodec(), new LoggingHandler(LogLevel.DEBUG));
        			p.addLast(new ChatServerHandler(), new LoggingHandler(LogLevel.DEBUG));
        			// .addLast(new ChatServerHandler2(), new LoggingHandler(LogLevel.DEBUG));
            	}
			});
            // b.childOption(ChannelOption.AUTO_READ, false);
            
            final String index = System.getProperty("user.dir") + "/html/ws/index.html";
            ServerBootstrap b2 = new ServerBootstrap();
            b2.group(bossGroup2, workerGroup2)
            .channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, SO_BACKLOG_NUM);
            b2.handler(new LoggingHandler(LogLevel.DEBUG));
            b2.childHandler(new ChannelInitializer<Channel>() {
            	@Override
            	protected void initChannel(Channel ch) throws Exception {
            		ChannelPipeline p = ch.pipeline();
        			p.addLast(new HttpServerCodec());
        			p.addLast(new HttpObjectAggregator(65536));
        			// 핸드쉐이크 처리
        			p.addLast(new WebSocketHandshakeHandler("/chat", new WebChatHandler()));
        			// html 파일처리
        			p.addLast(new HttpStaticFileHandler("/", index));
        			// 404처리
        			p.addLast(new HttpNotFoundHandler());
            		
            	}
			});
            // b2.childOption(ChannelOption.AUTO_READ, false);
            Channel ch = b.bind(8040).sync().channel();
            Channel ch2 = b2.bind(8050).sync().channel();
            
            ch.closeFuture().sync();
            ch2.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            bossGroup2.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
	}
	
	private int calculateThreadCount() {
	    int threadCount;
	    System.out.println("??? : " + SystemPropertyUtil.getInt("io.netty.eventLoopThreads", 0));
	    if ((threadCount = SystemPropertyUtil.getInt("io.netty.eventLoopThreads", 0)) > 0) {
	    	System.out.println("calculateThreadCount1: " + threadCount);
	        return threadCount;
	    } else {
	        threadCount = Runtime.getRuntime().availableProcessors() * 2;
	        System.out.println("Runtime : " + threadCount);
	        System.out.println("calculateThreadCount2: " + (threadCount > MAX_WORKER_THREADS ? MAX_WORKER_THREADS : threadCount));
	        return threadCount > MAX_WORKER_THREADS ? MAX_WORKER_THREADS : threadCount;
	    }
	}
}
