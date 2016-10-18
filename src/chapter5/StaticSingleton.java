package chapter5;

/**
 * 首先getInstance()方法没有锁,这使得在高并发环境下性能优越.
 * 其次,只有在getInstance()第一次调用时,StaticSingleton的实例才会被创建. 因此这种方法巧妙地使用了内部类和类的初始化方式.
 * 内部类SingletonHolder被申明为private,这使得我们不可能在外部访问并初始化它.
 * 而我们只能在getInstance()内部对SingletonHolder类进行初始化,利用虚拟机的类初始化机制创建单例.
 */
public class StaticSingleton {

	private StaticSingleton() {
		System.out.println("StaticSingleton is create");
	}

	private static class SingletonHolder {
		private static StaticSingleton instance = new StaticSingleton();
	}

	public static StaticSingleton getInstance() {
		return SingletonHolder.instance;
	}

}
