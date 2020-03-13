package com.sample.nss.ws;

import com.sample.nss.chat.ChatServerHandler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebChatHandler extends SimpleChannelInboundHandler<WebSocketFrame>{

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		// super.handlerAdded(ctx);
		System.out.println("handlerAdded");
		String handlerName = handlerName(ctx);
		// System.out.println("handlerName : " + handlerName);
		ChannelPipeline p = ctx.pipeline();
		p.addAfter(handlerName, "chatServer", new ChatServerHandler());
		p.addAfter(handlerName, "wsEncoder", new WebSocketChatCodec());
		
		handlerName(ctx);
	}
	
	// 채널 파이프라인에서 현재핸들러가 등록된 이름을 구합니다.
    // 이 이름을 기준으로 앞뒤에 다른 핸들러를 추가할 수 있습니다.
    private String handlerName(ChannelHandlerContext ctx) {
        final String[] result = new String[1];
        ctx.pipeline().toMap().forEach((name, handler) -> {
        	// System.out.println(name);
            if (handler.getClass().equals(this.getClass())) {
                result[0] = name;
            }
        });
        return result[0];
    }
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame wsFrame) throws Exception {
		System.out.println("WebChatHandler channelRead0");
		
		// close 인경우
		if (wsFrame instanceof CloseWebSocketFrame) {
			ctx.channel().writeAndFlush(wsFrame.retain())
				.addListener(ChannelFutureListener.CLOSE);
			return;
		}
		
		if (wsFrame instanceof PingWebSocketFrame) {
			ctx.channel().writeAndFlush(new PongWebSocketFrame(wsFrame.content().retain()));
			return;
		}
		
		if (!(wsFrame instanceof TextWebSocketFrame || wsFrame instanceof BinaryWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", wsFrame.getClass()));
		}
		
		// 이 WebChatHandler는 TextWebSocketFrame만 다음 핸들러에게 위임합니다.
        // SimpleInboundChannelHandler<I>는 기본으로 release() 처리가 들어있기 때문에
        // 아래의 코드는 retain()호출이 필요합니다.
        ctx.fireChannelRead(wsFrame.retain());
	}
}
