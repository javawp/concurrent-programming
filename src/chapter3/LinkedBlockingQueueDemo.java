package chapter3;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueDemo {

	private static Node last;

	static class Node {
		String item;
		Node next;

		Node(String x) {
			item = x;
		}

		@Override
		public String toString() {
			return "Node [item=" + item + ", next=" + next + "]";
		}

	}

	public static void main(String[] args) throws InterruptedException {

		Node node = new Node("1");
		last = new Node(null);
		// 记录原来的指针,当last指针改变时,原来的地址记录下来
		Node node3 = last;
		last.next = node;
		last = last.next;
		System.out.println(node3);

		LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		queue.add("1");
		queue.put("2");
	}
}
