package com.sample.nss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.sample.nss.echo.EchoServerHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;

@SpringBootTest
class EchoServerHandlerTest {

	@Test
	public void echo() {
		String msg = "hello \n";
		EmbeddedChannel ch = new EmbeddedChannel(new EchoServerHandler());
		ByteBuf in = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
		ch.writeInbound(in);
		ByteBuf read = ch.readOutbound();
		
		System.out.println(read);
		System.out.println(read.refCnt());
		System.out.println(read.toString(CharsetUtil.UTF_8));
		assertNotEquals(read, null);
		assertEquals(1, read.refCnt());
		assertEquals(msg, read.toString(CharsetUtil.UTF_8));
	}

}
