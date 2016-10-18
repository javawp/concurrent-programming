package chapter5;

/**
 * 不变模式 
 * 去除setter方法以及所有修改自身属性的方法 
 * 将所有属性设为私有,并且final标记,确保其不可修改 
 * 确保没有子类可以重载修改它的行为
 * 只有一个可以创建完整对象的构造函数
 * 
 * 在不变模式中,final关键字起到了重要的作用.对属性的final定义确保所有数据只能在对象被构造时赋值1次,之后,就永远不再发生改变.
 * 而对class的final确保了类不会有子类,根据里氏代换原则, 子类可以完全的替代父类.如果父类是不变的,那么子类也必须是不变的.
 * 但实际上我们无法约束这点,为了防止子类做出一些意外的行为,这里就干脆把子类都禁用了.
 * 
 * 在JDK中,不变模式的应用非常广泛. 其中,最为典型的就是java.lang.String类. 此外, 所有的元数据类包装类, 都是使用不变模式实现的.
 * 
 * 注意: 不变模式通过回避问题而不是解决问题的态度来处理多线程并发访问控制.不变对象是不需要进行同步操作的. 
 * 由于并发同步会对性能产生不良的影响,因此,在需求允许的情况下,不变模式可以提高系统的并发性能和并发量.
 */
public final class InvariantPattern { // 确保无子类

	private final String no; // 私有属性,不会被其他对象获取
	private final String name; // final保证属性不会被2次赋值
	private final double price;

	/**
	 * 创建对象时, 必须制定数据, 因为创建之后, 无法进行修改
	 * @param no
	 * @param name
	 * @param price
	 */
	public InvariantPattern(String no, String name, double price) {
		super();
		this.no = no;
		this.name = name;
		this.price = price;
	}

	public String getNo() {
		return no;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

}
