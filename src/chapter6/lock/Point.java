package chapter6.lock;

import java.util.concurrent.locks.StampedLock;

public class Point {

	private double x, y;
	private final StampedLock s1 = new StampedLock();

	void move(double deltaX, double deltaY) { // 这是一个排它锁
		long stamp = s1.writeLock();
		try {
			x += deltaX;
			y += deltaY;
		} finally {
			s1.unlockWrite(stamp);
		}
	}

	double distanceFromOrigin() { // 只读方法
		long stamp = s1.tryOptimisticRead(); // 这个方法表示试图尝试一次乐观读, 返回邮戳stamp,
												// 作为这一次锁的凭证
		double currentX = x, currentY = y;
		if (!s1.validate(stamp)) { // 判断这个stamp是否在读过程发生期间被修改过, 如果stamp没有被修改过,
									// 认为这次读取是有效的,跳出; 否则,
			stamp = s1.readLock(); // 升级乐观锁的级别, 变为悲观锁, 如果当前对象正在被修改,
									// 读锁的申请可能导致线程挂起
			try {
				currentX = x;
				currentY = y;
			} finally {
				s1.unlockRead(stamp);
			}
		}
		return Math.sqrt(currentX * currentX + currentY * currentY);
	}
}
