package com.sample.nss.ws;

import com.sample.nss.chat.ChatMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

//도전과제: 시간이 더 있다면, 세번째 시간에 개발한 채팅 서버에 TCP 연결로 프록시 처리를 하는 웹소켓 채팅서비스를 만들어봅시다.
@Slf4j
public class ChatProxyHandler extends SimpleChannelInboundHandler<ChatMessage>{
	private Bootstrap b;
	private ChannelFuture f;
	private Channel outChannel;
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    	log.debug("ChatProxyHandler handlerAdded");
    	final Channel inChannel = ctx.channel();
		b = new Bootstrap();
    	b.group(inChannel.eventLoop())
    		.channel(NioSocketChannel.class)
    		.option(ChannelOption.TCP_NODELAY, true)
    		//.option(ChannelOption.AUTO_READ, false)
    		.handler(new LoggingHandler(LogLevel.DEBUG))
    		.handler(new ChannelInitializer<SocketChannel>() {
    			@Override
    			protected void initChannel(SocketChannel ch) throws Exception {
    				ChannelPipeline p = ch.pipeline();
    				p.addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8));
    				p.addLast(new ProxyInboundHandler(inChannel));
    			}
			});
    	f = b.connect("127.0.0.1", 8040);
    	outChannel = f.channel();
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) throws Exception {
    	// TODO Auto-generated method stub
    	log.debug("ChatProxyHandler channelRead0 msg: {}, outChannel : {}", msg, outChannel);
    	String tempMsg = msg.toString() + "\r\n";
		outChannel.writeAndFlush(tempMsg).addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					log.debug("Chat message write Success");
				}
			}
		});
		
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	// TODO Auto-generated method stub
    	// super.channelReadComplete(ctx);
    	log.debug("ChatProxyHandler channelReadComplete");
    }
    
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	log.debug("ChatProxyHandler channelInactive");
    	if (f != null) {
    		outChannel.close();
    		f = null;
    	}
    	
    	if (b != null) {
    		b = null;
    	}
    }
}
