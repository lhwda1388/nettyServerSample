package com.sample.nss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.sample.nss.chat.ChatServerHandler;
import com.sample.nss.ws.WebChatHandler;
import com.sample.nss.ws.WebSocketChatCodec;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;

@SpringBootTest
class WebChatHandlerChatTest {

	@Test
	void test() {
		EmbeddedChannel ch = new EmbeddedChannel(new WebChatHandler());
        ChannelPipeline p = ch.pipeline();
        System.out.println(p.first().getClass());
        assertEquals(p.first().getClass(), WebChatHandler.class); // WEbChatHandler check
        // assertNotEquals(p.get(WebSocketChatCodec.class), null); //  WebSocketChat Codec check
        assertNotEquals(p.get(ChatServerHandler.class), null); // ChatServerHandler check
	}

}
