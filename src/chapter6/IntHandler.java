package chapter6;

/**
 * 函数式接口定义<br>
 * 函数式接口只能有一个抽象方法, 而不是只能有一个方法<br>
 * 1: java8中, 接口运行存在实例方法("接口默认方法")<br>
 * 2: 任何被java.lang.Object实现的方法,都不能视为抽象方法
 */
@FunctionalInterface
public interface IntHandler {

	void handle(int i);

	/**
	 * java8中, 接口运行存在实例方法("接口默认方法")
	 * 
	 * @param j
	 */
	default void defaultMethod(int j) {

	}

	/**
	 * 任何被java.lang.Object实现的方法,都不能视为抽象方法
	 * 
	 * @param obj
	 * @return
	 */
	boolean equals(Object obj);
}
