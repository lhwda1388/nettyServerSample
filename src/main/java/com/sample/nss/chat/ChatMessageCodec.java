package com.sample.nss.chat;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

public class ChatMessageCodec extends MessageToMessageCodec<String, ChatMessage>{
	
	@Override
	protected void encode(ChannelHandlerContext ctx, ChatMessage msg, List<Object> out) throws Exception {
		System.out.println("ChatMessageCodec encode =============================== " + msg.command);
		String tempMsg = msg.toString() + "\r\n";
		System.out.println("|" + tempMsg + "|");
		out.add(tempMsg);
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		System.out.println("ChatMessageCodec decode =============================== " + msg);
		out.add(ChatMessage.parse(msg));
	}
}
