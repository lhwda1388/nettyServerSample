package com.sample.nss.ws;

import com.sample.nss.chat.ChatMessageCodec;
import com.sample.nss.chat.ChatServerHandler;
import com.sample.nss.chat.ChatServerHandler2;
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

public class ProxyWebChatServer {
	
	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        EventLoopGroup bossGroup2 = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup2 = new NioEventLoopGroup(2);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new ChannelInitializer<Channel>() {
            	@Override
            	protected void initChannel(Channel ch) throws Exception {
            		ChannelPipeline p = ch.pipeline();
            		p.addLast(new LineBasedFrameDecoder(1024, true, true), new LoggingHandler(LogLevel.DEBUG))
        			.addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8))
        			.addLast(new ChatMessageCodec(), new LoggingHandler(LogLevel.DEBUG))
        			.addLast(new ChatServerHandler(), new LoggingHandler(LogLevel.DEBUG));
        			// .addLast(new ChatServerHandler2(), new LoggingHandler(LogLevel.DEBUG));
            	}
			});
            
            final String index = System.getProperty("user.dir") + "/html/ws/index.html";
            ServerBootstrap b2 = new ServerBootstrap();
            b2.group(bossGroup2, workerGroup2)
            .channel(NioServerSocketChannel.class);
            b2.handler(new LoggingHandler(LogLevel.INFO));
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
            // .childOption(ChannelOption.AUTO_READ, true);
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
}
