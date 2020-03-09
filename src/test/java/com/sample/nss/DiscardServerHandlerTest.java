package com.sample.nss;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.sample.nss.discard.DiscardServerHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

@SpringBootTest
public class DiscardServerHandlerTest {

	@Test
	public void discard() {
		String msg = "hello \n";
		EmbeddedChannel ch = new EmbeddedChannel(new DiscardServerHandler());
		ByteBuf in = Unpooled.wrappedBuffer(msg.getBytes());
		ch.writeInbound(in);
		ByteBuf read = ch.readOutbound();
		System.out.println(read);
		assertEquals(read, null);
	}
}
