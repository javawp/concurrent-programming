package chapter5.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AIOEchoServer {

	public static final int PORT = 8888;

	private AsynchronousServerSocketChannel server;

	public AIOEchoServer() throws IOException {
		server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));
	}

	public void start() {
		System.out.println("Server listen on " + PORT);
		// 注册事件和事件完成后的处理器
		server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

			final ByteBuffer buffer = ByteBuffer.allocate(1024);

			@Override
			public void completed(AsynchronousSocketChannel result, Object attachment) {
				System.out.println(Thread.currentThread().getName());
				Future<Integer> writeResult = null;
				try {
					buffer.clear();
					/**
					 * 这里的AsynchronousSocketChannel.read()方法也是异步的,
					 * 它不会等到读取完了才返回,而是立即返回,返回的结果是一个Future,
					 * 因此这里就是Future模式的典型应用,当然,为了编程方便,
					 * 在这里直接调用Future.get()方法,进行等待, 将这个异步方法变成了同步方法.
					 */
					result.read(buffer).get(100, TimeUnit.SECONDS);
					buffer.flip();
					/**
					 * 这里的AsynchronousSocketChannel.write()方法,不会等待数据全部写完,
					 * 也是立即返回的,返回的结果也是Future对象
					 */
					writeResult = result.write(buffer);
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					e.printStackTrace();
				} finally {
					try {
						// 服务器进行下一个客户端连接的准备,同时关闭当前正在处理的客户端连接.
						server.accept(null, this);
						// 但在关闭之前,得先确保之前的write()操作已经完成,因此,使用Future.get()方法进行等待.
						writeResult.get();
						result.close();
					} catch (InterruptedException | ExecutionException | IOException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void failed(Throwable exc, Object attachment) {
				System.out.println("failed: " + exc);
			}
		});
	}

	public static void main(String args[]) throws Exception {
		new AIOEchoServer().start();
		// 主线程可以继续自己的行为
		while (true) {
			Thread.sleep(1000);
		}
	}

}
