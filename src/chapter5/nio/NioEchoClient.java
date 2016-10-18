package chapter5.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class NioEchoClient {

	private Selector selector;

	public void init(String ip, int port) throws IOException {
		// 创建SocketChannel实例; 并设为非阻塞模式
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);

		this.selector = SelectorProvider.provider().openSelector();
		// 建立连接
		channel.connect(new InetSocketAddress(ip, port));
		// 将Channel与Selector进行绑定, 注册连接事件
		channel.register(selector, SelectionKey.OP_CONNECT);
	}

	public void working() throws IOException {
		while (true) {
			if (!selector.isOpen())
				break;
			// 这是一个阻塞方法,如果当前没有任何数据准备好,它就会等待.它的返回值是已经准备就绪的SelectionKey的数量.
			selector.select();
			Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
			while (ite.hasNext()) {
				SelectionKey key = ite.next();
				// 注意,一定要将此元素移除,这个非常重要,否则就会重复处理相同的SelectionKey
				ite.remove();
				// 连接事件发生
				if (key.isConnectable()) {
					connect(key);
				} else if (key.isReadable()) {
					read(key);
				}
			}
		}
	}

	/**
	 * 当channel可读取时,执行read()方法
	 * @param key
	 * @throws IOException 
	 */
	private void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		// 创建读取的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(100);
		channel.read(buffer);
		byte[] data = buffer.array();
		String msg = new String(data).trim();
		System.out.println("客户端收到消息: " + msg);
		channel.close();
		key.selector().close();
	}

	private void connect(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		// 如果正在连接, 则完成连接
		if (channel.isConnectionPending()) {
			channel.finishConnect();
		}
		channel.configureBlocking(false);
		channel.write(ByteBuffer.wrap(new String("hello server!\r\n").getBytes())); // 写消息
		channel.register(selector, SelectionKey.OP_READ); // 注册读取事件
	}
	
	public static void main(String[] args) throws IOException {
		NioEchoClient client = new NioEchoClient();
		client.init("127.0.0.1", 8888);
		client.working();
	}
}
