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
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

//도전과제: 시간이 더 있다면, 세번째 시간에 개발한 채팅 서버에 TCP 연결로 프록시 처리를 하는 웹소켓 채팅서비스를 만들어봅시다.
@Slf4j
public class ChatProxyHandler extends SimpleChannelInboundHandler<ChatMessage>{
	private final Channel wsChannel;
	private Bootstrap b;
	private ChannelFuture f;
	private Channel outChannel;
	private ProxyInboundHandler proxyInboundHandler;

    public ChatProxyHandler(Channel wsChannel) {
        this.wsChannel = wsChannel;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	final Channel inChannel = this.wsChannel;
		b = new Bootstrap();
    	b.group(inChannel.eventLoop())
    		.channel(NioSocketChannel.class)
    		.option(ChannelOption.TCP_NODELAY, true)
    		// .option(ChannelOption.AUTO_READ, false)
    		.handler(new LoggingHandler(LogLevel.INFO))
    		.handler(new ChannelInitializer<SocketChannel>() {
    			@Override
    			protected void initChannel(SocketChannel ch) throws Exception {
    				proxyInboundHandler = new ProxyInboundHandler(inChannel);
    				ChannelPipeline chp = ch.pipeline();
    				log.debug("initChannel");
    				chp.addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8));
    				chp.addLast(proxyInboundHandler);
    			}
			});
    	f = b.connect("localhost", 8040);
    	outChannel = f.channel();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) throws Exception {
    	// TODO Auto-generated method stub
    	log.debug("ChatProxyHandler channelRead0 {}", msg);
    	proxyInboundHandler.sendMsg(msg);
    	/*
    	f.addListener(new ChannelFutureListener() {
    		private ByteBuf byteBuf;
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				// TODO Auto-generated method stub
				if (future.isSuccess()) {
					Channel channel = future.channel();
					byte[] str = msg.toString().getBytes(CharsetUtil.UTF_8);
                    byteBuf = Unpooled.buffer(str.length);
                    byteBuf.writeBytes(str);
					channel.writeAndFlush(byteBuf).addListener(new ChannelFutureListener() {
						
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							log.debug("writeAndFlush success {}", future.isSuccess());
							log.debug("writeAndFlush done {}", future.isDone());
							
						}
					});
				}
			}
		});
		*/
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
