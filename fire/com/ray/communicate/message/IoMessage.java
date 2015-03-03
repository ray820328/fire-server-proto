package com.ray.communicate.message;

import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;

public class IoMessage {
	private IoHeader header;
	private IoContent content;
	private boolean serialized = false;//已经序列化成bytes
	private boolean sent = false;//发送状态
	private Set<Integer> forwardIds;//转发ids(空)
	public IoMessage(){
		
	}
	public IoMessage(int sid, int uid, int cmd, int code){
		header = new IoHeader();
		header.setSid(sid);
		header.setUid(uid);
		header.setCmd(cmd);
		header.setCode(code);
		content = new IoContent();
	}
	public IoHeader getHeader() {
		return header;
	}
	public void setHeader(IoHeader header) {
		this.header = header;
	}
	public IoContent getContent() {
		return content;
	}
	public void setContent(IoContent content) {
		this.content = content;
	}
	public void setBody(byte[] body){
		if(content == null){
			content = new IoContent();
		}
		content.setBody(body);
	}
	public byte[] getBody(){
		return content.getBody();
	}
	public long getSid(){
		return header.getSid();
	}
	public int getUid(){
		return header.getUid();
	}
	public int getLen(){
		return header.getBodyLength();
	}
	public void prepare(){
		header.setBodyLength(IoHeader.header_length + content.getLen());
	}
	public IoMessage read(IoBuffer buffer){
		readHeader(buffer);
		readContent(buffer);
		return this;
	}
	public IoMessage readHeader(IoBuffer buffer){
		if(header == null){
			header = new IoHeader();
		}
		header.read(buffer);
		return this;
	}
	public IoMessage readContent(IoBuffer buffer){
		if(content == null){
			content = new IoContent();
		}
		content.read(buffer, header.getBodyLength() - IoHeader.header_length);
		return this;
	}
	public IoBuffer write(IoBuffer buffer){
		header.write(buffer);
		content.write(buffer);
		return buffer;
	}
	
	public void addForwardId(int uid){
		if(forwardIds == null){
			forwardIds = new HashSet<Integer>();
		}
		forwardIds.add(uid);
	}
	
	public boolean hasForwardId(){
		return header.getSid() > 0 || (forwardIds != null && forwardIds.size() > 0);
	}
	
	public boolean isSerialized() {
		return serialized;
	}
	public void setSerialized(boolean serialized) {
		this.serialized = serialized;
	}
	public boolean isSent() {
		return sent;
	}
	public void setSent(boolean sent) {
		this.sent = sent;
	}
	public Set<Integer> getForwardIds() {
		return forwardIds;
	}
	public void setForwardIds(Set<Integer> forwardIds) {
		this.forwardIds = forwardIds;
	}
}