package com.sample.nss.ws;

import com.sample.nss.chat.ChatMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;

//도전과제: 시간이 더 있다면, 세번째 시간에 개발한 채팅 서버에 TCP 연결로 프록시 처리를 하는 웹소켓 채팅서비스를 만들어봅시다.
public class ChatProxyHandler extends SimpleChannelInboundHandler<ChatMessage>{
	private final Channel wsChannel;
	Bootstrap b = null;
	ChannelFuture f = null;
	Channel outChannel = null;

    public ChatProxyHandler(Channel wsChannel) {
        this.wsChannel = wsChannel;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	final Channel inChannel = this.wsChannel;
    	b = new Bootstrap();
    	b.group(inChannel.eventLoop())
    		.channel(NioSocketChannel.class)
    		.handler(new ChannelInitializer<Channel>() {
    			@Override
    			protected void initChannel(Channel ch) throws Exception {
    				ChannelPipeline chp = ch.pipeline();
    				chp.addLast(new ProxyInboundHandler(inChannel), new LoggingHandler(LogLevel.INFO));
    			}
			});
    	f = b.connect("127.0.0.1", 8040);
    	outChannel = f.channel();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) throws Exception {
    	// TODO Auto-generated method stub
    	System.out.println("ChatProxyHandler channelRead0");
    	outChannel.writeAndFlush(msg);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	if (f != null) {
    		outChannel.close();
    		f = null;
    	}
    	
    	if (b != null) {
    		b = null;
    	}
    }
}
