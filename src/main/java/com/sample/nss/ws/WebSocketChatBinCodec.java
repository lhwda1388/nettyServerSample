package com.sample.nss.ws;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.sample.nss.chat.ChatMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketChatBinCodec extends MessageToMessageCodec<BinaryWebSocketFrame, ChatMessage>{
	
	@Override
	protected void encode(ChannelHandlerContext ctx, ChatMessage msg, List<Object> out) throws Exception {
		log.debug("WebSocketChatCodec encode : " , msg.toString());
		Unpooled.wrappedBuffer(msg.toString().getBytes());
		out.add(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg.toString().getBytes())));
		// out.add(new TextWebSocketFrame(msg.toString()));
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {
		log.debug("WebSocketChatCodec decode");
		String s = msg.content().toString(CharsetUtil.UTF_8);
		out.add(ChatMessage.parse(s));
		// out.add(ChatMessage.parse(msg.text()))
	}
}
