package com.sample.nss;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

public class StartUpUtil {
	public static void runServer(int port, ChannelHandler childHandler, Consumer<ServerBootstrap> block) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(childHandler);
            // b.childOption(ChannelOption.SO_KEEPALIVE, true);
            block.accept(b);
            Channel ch = b.bind(port).sync().channel();
            System.err.println("Ready for 0.0.0.0:" + port);
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void runServer(int port, ChannelHandler childHandler) throws Exception {
        runServer(port, childHandler, b -> {});
    }

    public static void runServer(int port, Consumer<ChannelPipeline> initializer) throws Exception {
        runServer(port, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                initializer.accept(ch.pipeline());
            }
        });
    }

}
