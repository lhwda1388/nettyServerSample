package com.sample.nss.ws;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProxyInboundHandler extends SimpleChannelInboundHandler<String>{
	private Channel wsChannel;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println("ProxyInboundHandler: "+ msg);
		wsChannel.writeAndFlush(msg);
	}
}
