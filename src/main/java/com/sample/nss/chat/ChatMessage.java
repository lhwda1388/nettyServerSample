package com.sample.nss.chat;

import java.util.Objects;

import org.springframework.util.StringUtils;

public class ChatMessage {
	public final String command;
	public final String nickname;
	public String text;
	
	public ChatMessage(String command) {
		this(command, null, null);
	}
	
	public ChatMessage(String command, String nickname) {
		this(command, nickname, null);
	}
	
	public ChatMessage(String command, String nickname, String text) {
		this.command = command;
		this.nickname = nickname;
		this.text = text;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.command);
		
		if (!StringUtils.isEmpty(this.nickname)) b.append(":").append(this.nickname);
		if (!StringUtils.isEmpty(this.text)) b.append(" ").append(this.text);
		return b.toString().trim();
	}
	
	public static ChatMessage parse(String line) {
		System.out.println("line.contains(\"\\n\") " + line.contains("\n"));
		if (line.contains("\n")) throw new IllegalArgumentException();
		String[] tokens = line.split("\\s", 2);
		String command = tokens[0];
		ChatMessage m;
		if (command.contains(":")) {
			String[] t = command.split(":", 2);
			m = new ChatMessage(t[0], t[1]);
		} else {
			m = new ChatMessage(command);
		}
		if (tokens.length > 1) m.text = tokens[1];
		
		return m;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ChatMessage)) return false;
        ChatMessage oc = (ChatMessage)o;
        return this == o ||
                Objects.equals(this.command, oc.command)
                && Objects.equals(this.nickname, oc.nickname)
                && Objects.equals(this.text, oc.text);
	}
	
	@Override
	public int hashCode() {
		return (this.command + this.nickname + this.text).hashCode();
	}
}
