package com.sample.nss.ws;

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
@AllArgsConstructor
public class ProxyInboundHandler extends SimpleChannelInboundHandler<String>{
	private Channel wsChannel;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug("ProxyInboundHandler active channel : {}", ctx.channel());
		ctx.read();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		log.debug("ProxyInboundHandler channelRead0 msg : {}, channel:  {}", msg, ctx.channel());
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
}
