package com.sample.nss.ws;

import java.util.List;

import com.sample.nss.chat.ChatMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketChatCodec extends MessageToMessageCodec<TextWebSocketFrame, ChatMessage>{
	@Override
	protected void encode(ChannelHandlerContext ctx, ChatMessage msg, List<Object> out) throws Exception {
		System.out.println("WebSocketChatCodec encode");
		out.add(new TextWebSocketFrame(msg.toString()));
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame msg, List<Object> out) throws Exception {
		System.out.println("WebSocketChatCodec decode");
		out.add(ChatMessage.parse(msg.text()));
	}
}
