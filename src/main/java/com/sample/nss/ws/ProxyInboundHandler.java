package com.sample.nss.ws;

import com.sample.nss.chat.ChatMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyInboundHandler extends SimpleChannelInboundHandler<String>{
	private Channel wsChannel;
	private ChannelHandlerContext ctx;
	
	public ProxyInboundHandler(Channel wsChannel) {
		this.wsChannel = wsChannel;
	}
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		ctx.read();
		log.debug("ProxyInboundHandler active");
		ctx.writeAndFlush("SEND TEST");
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		log.debug("ProxyInboundHandler channelRead0 : "+ msg);
		ByteBuf buf = Unpooled.wrappedBuffer(msg.getBytes(CharsetUtil.UTF_8));
		BinaryWebSocketFrame frame = new BinaryWebSocketFrame(buf);
		// System.out.println("start : " +  buf.toString(CharsetUtil.UTF_8) + " : end");
		frame.retain();
		wsChannel.writeAndFlush(frame).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					ctx.channel().read();
				} else {
					future.channel().close();
				}
			}
		});
	}
	
	public void sendMsg(ChatMessage msg) {
		byte[] str = msg.toString().getBytes(CharsetUtil.UTF_8);
        ByteBuf byteBuf = Unpooled.copiedBuffer(str);
		ctx.writeAndFlush(byteBuf).addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				log.debug("sendMsg result : {}", future.isSuccess());
			}
		});
	}
}
