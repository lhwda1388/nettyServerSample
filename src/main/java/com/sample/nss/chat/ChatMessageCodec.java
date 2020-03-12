package com.sample.nss.chat;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

public class ChatMessageCodec extends MessageToMessageCodec<String, ChatMessage>{
	
	@Override
	protected void encode(ChannelHandlerContext ctx, ChatMessage msg, List<Object> out) throws Exception {
		out.add(msg + "\n");
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		out.add(ChatMessage.parse(msg));
	}
}
