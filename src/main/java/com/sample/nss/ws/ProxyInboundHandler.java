package com.sample.nss.ws;

import org.springframework.beans.factory.annotation.Value;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyInboundHandler extends SimpleChannelInboundHandler<String>{
	
	@Value("${ws.bin-mode}")
	private boolean wsBinMode;
	
	private Channel wsChannel;
	
	public ProxyInboundHandler(Channel wsChannel) {
		this.wsChannel = wsChannel;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug("ProxyInboundHandler active channel : {}", ctx.channel());
		ctx.read();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		log.debug("ProxyInboundHandler channelRead0 msg : {}, channel:  {}", msg, ctx.channel());
		ByteBuf buf = Unpooled.wrappedBuffer(msg.getBytes(CharsetUtil.UTF_8));
		WebSocketFrame frame = null;
		if (wsBinMode) {
			frame = new BinaryWebSocketFrame(buf);
		} else {
			frame = new TextWebSocketFrame(buf);
		}
		
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
}
