package chapter5.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadNIOEchoServer {

	/** selector用于处理所有的网络连接 */
	private Selector selector;
	/** 线程池tp用于对每一个客户端进行相应的处理,每一个请求都会委托给线程池中的线程进行实际的处理 */
	private ExecutorService tp = Executors.newCachedThreadPool();
	/** 统计在某一个Socket上花费的时间 */
	public static Map<Socket, Long> time_stat = new HashMap<Socket, Long>(10240);

	/**
	 * 封装了一个队列, 保存需要回复给这个客户端的所有信息,在需要回复时,从队列中弹出元素即可
	 */
	public class EchoClient {

		private LinkedList<ByteBuffer> outq;

		public EchoClient() {
			this.outq = new LinkedList<ByteBuffer>();
		}

		public LinkedList<ByteBuffer> getOutputQueue() {
			return outq;
		}

		public void enqueue(ByteBuffer bb) {
			outq.addFirst(bb);
		}

	}

	class HandleMsg implements Runnable {

		SelectionKey sk;
		ByteBuffer bb;

		public HandleMsg(SelectionKey sk, ByteBuffer bb) {
			this.sk = sk;
			this.bb = bb;
		}

		@Override
		public void run() {
			EchoClient echoClient = (EchoClient) sk.attachment();
			echoClient.enqueue(bb);
			sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			// 强迫selector立即返回
			selector.wakeup();
		}

	}

	private void startServer() throws Exception {
		// 通过工厂方法获得一个Selector对象的实例
		selector = SelectorProvider.provider().openSelector();
		// 获得表示服务端的SocketChannel实例
		ServerSocketChannel ssc = ServerSocketChannel.open();
		// 将这个SocketChannel设置为非阻塞模式
		ssc.configureBlocking(false);

		// 将Channel进行端口绑定
		InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(), 8888);
		ssc.socket().bind(isa);

		/**
		 * 将ServerSocketChannel绑定到Selector上, 并注册它感兴趣的时间为Accept,
		 * 这样,Selector就能为这个Channel服务了. 当Selector发现ServerSocketChannel有新的客户端连接时,
		 * 就会通知ServerSocketChannel进行处理. 方法register()的返回值是一个SelectionKey,
		 * SelectionKey表示一对Selector和Channel的关系. 当Channel注册到Selector上时,
		 * 就相当于确立了两者的服务关系,那么SelectionKey就是这个契约.
		 * 当Selector或者Channel被关闭时,它们对应的SelectionKey就会失效.
		 */
		@SuppressWarnings("unused")
		SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

		// 等待分发网络消息
		for (;;) {
			// 这是一个阻塞方法,如果当前没有任何数据准备好,它就会等待.一旦有数据可读,它就会返回.它的返回值是已经准备就绪的SelectionKey的数量.
			selector.select();
			// 获取那些准备好的SelectionKey,因为Selector同时为多个Channel服务,因此已经准备就绪的Channel就有可能是多个
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> i = readyKeys.iterator();
			long e = 0;
			while (i.hasNext()) {
				SelectionKey sk = i.next();
				// 注意,一定要将此元素移除,这个非常重要,否则就会重复处理相同的SelectionKey
				i.remove();
				// 判断当前SelectionKey所代表的Channel是否在Acceptable状态,如果是,就进行客户端的接收,doAccept(sk);
				if (sk.isAcceptable()) {
					doAccept(sk);
				}
				// 判断Channel是否已经可读了,doRead(sk);
				else if (sk.isValid() && sk.isReadable()) {
					// 记录读取数据之前的一个时间戳
					if (!time_stat.containsKey(((SocketChannel) sk.channel()).socket()))
						time_stat.put(((SocketChannel) sk.channel()).socket(), System.currentTimeMillis());
					doRead(sk);
				}
				// 判断通道是否准备好进行写,doWrite(sk);
				else if (sk.isValid() && sk.isWritable()) {
					doWrite(sk);
					e = System.currentTimeMillis();
					// 获取读取前的时间戳
					long b = time_stat.remove(((SocketChannel) sk.channel()).socket());
					// 输出处理这个Socket连接的耗时
					System.out.println("spend: " + (e - b) + "ms");
				}
			}
		}
	}

	private void doWrite(SelectionKey sk) {
		SocketChannel channel = (SocketChannel) sk.channel();
		EchoClient echoClient = (EchoClient) sk.attachment();
		LinkedList<ByteBuffer> outq = echoClient.getOutputQueue();

		ByteBuffer bb = outq.getLast();
		try {
			int len = channel.write(bb);
			if (len == -1) {
				disconnect(sk);
				return;
			}
			if (bb.remaining() == 0) {
				// The buffer was completely written, remove it.
				outq.removeLast();
			}
		} catch (Exception e) {
			System.out.println("Failed to write to client.");
			e.printStackTrace();
			disconnect(sk);
		}

		// If there is no more data to be written, remove interest in OP_WRITE.
		if (outq.size() == 0) {
			sk.interestOps(SelectionKey.OP_READ);
		}
	}

	/**
	 * Channel读取
	 * 
	 * @param sk
	 */
	private void doRead(SelectionKey sk) {
		// 通过这个SelectionKey可以得到当前的客户端Channel
		SocketChannel channel = (SocketChannel) sk.channel();
		// 我们准备8K的缓冲区读取数据,所有读取的数据存放在变量bb中.
		ByteBuffer bb = ByteBuffer.allocate(8192);
		int len;

		try {
			len = channel.read(bb);
			if (len < 0) {
				disconnect(sk);
				return;
			}
		} catch (IOException e) {
			System.out.println("Failed to read from client.");
			e.printStackTrace();
			disconnect(sk);
			return;
		}

		// 读取完成后, 重置缓冲区,为数据处理做准备
		bb.flip();
		tp.execute(new HandleMsg(sk, bb));
	}

	private void disconnect(SelectionKey sk) {
		SocketChannel channel = (SocketChannel) sk.channel();

		InetAddress clientAddress = channel.socket().getInetAddress();
		System.out.println(clientAddress.getHostAddress() + " disconnected.");

		try {
			channel.close();
		} catch (IOException e) {
			System.out.println("Failed to close client socket channel.");
			e.printStackTrace();
		}
	}

	/**
	 * 与客户端建立连接
	 * 
	 * @param sk
	 */
	private void doAccept(SelectionKey sk) {
		ServerSocketChannel server = (ServerSocketChannel) sk.channel();
		SocketChannel clientChannel;
		try {
			// 和客户端通信的通道
			clientChannel = server.accept();
			// 将SocketChannel配置为非阻塞模式,也就是说要求系统在准备好IO后,再通知我们的线程来读取或者写入
			clientChannel.configureBlocking(false);
			// 将新生成的Channel注册到selector选择器上, 当发现Channel准备好读取时, 通知线程来读取.
			SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
			// 新建一个客户端对象, 附加到SelectionKey, 这样在整个连接的处理过程中,我们都可以共享这个EchoClient实例
			EchoClient echoClient = new EchoClient();
			clientKey.attach(echoClient);

			InetAddress clientAddress = clientChannel.socket().getInetAddress();
			System.out.println("Accepted connection from " + clientAddress.getHostAddress() + ".");
		} catch (Exception e) {
			System.out.println("Failed to accept new client.");
			e.printStackTrace();
		}
	}
	
	// Main entry point.
    public static void main(String[] args) {
        MultiThreadNIOEchoServer echoServer = new MultiThreadNIOEchoServer();
        try {
            echoServer.startServer();
        } catch (Exception e) {
            System.out.println("Exception caught, program exiting...");
            e.printStackTrace();
        }
    }
}
