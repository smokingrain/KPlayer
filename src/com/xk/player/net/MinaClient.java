package com.xk.player.net;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.xk.player.tools.JSONUtil;


public class MinaClient {
	
	public static final Long SERVER = -1L;
	public static final String APP = "kp";
	public static final String MSG_DISCONNECT = "disconnect";
	public static final String MSG_ASK_SEND = "asksend";
	public static final String MSG_SEND_DATA = "senddata";
	public static final String MSG_SEND_END = "sendend";
	public static final String RESULT_FILE_EXISTS = "fileexists";
	public static final String RESULT_OK = "sendok";
	public static final String RESULT_OVER = "sendover";
	public static final String RESULT_NO_CLIENT = "noclient";
	public static final String RESULT_WORKING = "working";
	
	
	
	private IoSession session;
	private MessageListener listener;
	private IoConnector connector;
	private Long cid;
	private MinaClient(){}
	
	public boolean init(String host,int port){
		if(null==connector){
			connector = new NioSocketConnector();
			//设置链接超时时间
			connector.getSessionConfig().setReadBufferSize(2048);
			connector.setConnectTimeoutMillis(5000);
			connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
			connector.setHandler(new MessageHandler());
			try{
				ConnectFuture future = connector.connect(new InetSocketAddress(host,port));
				future.awaitUninterruptibly();// 等待连接创建完成
				session = future.getSession();//获得session
				System.out.println("created connection!");
				return true;
			}catch (Exception e){
				close(true);
			}
		}
		return false;
	}
	
	public Long getCid() {
		return cid;
	}

	public void setListener(MessageListener listener) {
		this.listener = listener;
	}
	
	public boolean writeMessage(final String msg){
		if(null==session){
			return false;
		}
		System.out.println(msg);
		WriteFuture future=session.write(msg);
		future.setWritten();
		return future.isWritten();
	}
	
	
	public void close(boolean notify){
		if(null!=listener&&notify){
			PackageInfo info=new PackageInfo(getCid(), "disconnect", SERVER, MSG_DISCONNECT, APP);
			listener.getMessage(info);
			System.out.println("send close msg!");
		}
		setListener(null);
		if(null!=connector){
			connector.dispose();
		}
		session=null;
		connector=null;
	}
	
	public static MinaClient getInstance(){
		return MinaFactory.INSTANCE;
	}
	
	private class MessageHandler extends IoHandlerAdapter  {

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			session.closeNow();
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			PackageInfo info=JSONUtil.toBean(message.toString(),PackageInfo.class);
			if("LOGIN".equals(info.getType())){
				cid = Long.parseLong(info.getMsg());
				System.out.println("连接成功，id = " + getCid());
				return;
			}
			listener.getMessage(info);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			close(true);
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			System.out.println("conection conecting!");
		}
		
	}
	
	private static class MinaFactory{
		public static final MinaClient INSTANCE=new MinaClient();
	}
	
	
}
