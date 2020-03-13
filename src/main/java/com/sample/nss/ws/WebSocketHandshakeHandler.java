package com.sample.nss.ws;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class WebSocketHandshakeHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
	private final String wsPath;
	private ChannelHandler wsHandler;

	public WebSocketHandshakeHandler(String wsPath, ChannelHandler wsHandler) {
		super(false);
		this.wsPath = wsPath;
		this.wsHandler = wsHandler;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		// 웹소켓 path
		if (wsPath.equals(req.getUri())) {
			handshakeWebSocket(ctx, req);
		} else { // 그외
			ctx.fireChannelRead(req);
		}
	}
	
	private void handshakeWebSocket(ChannelHandlerContext ctx, FullHttpRequest req) {
		try {
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(wsPath, null, true);
			WebSocketServerHandshaker h = wsFactory.newHandshaker(req);
			if (h == null) {
				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			} {
				h.handshake(ctx.channel(), req).addListener((ChannelFuture f) -> {
					// 웹소켓 핸드쉐이킹 처리 되면 웹소켓 핸들러로 변경
					ChannelPipeline p = f.channel().pipeline();
					p.replace(WebSocketHandshakeHandler.class, "wsHandler", wsHandler);
					p.addLast(new LoggingHandler(LogLevel.INFO));
				});
			}
			
		} finally {
			req.release();
		}
	}
}
