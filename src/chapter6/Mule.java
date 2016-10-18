package chapter6;

public class Mule implements IHorse, IDonkey, IAnimal {

	@Override
	public void eat() {
		System.out.println("Mule eat");
	}

	@Override
	public void run() {
		IHorse.super.run();
	}

	public static void main(String[] args) {
		Mule mule = new Mule();
		mule.run();
		mule.breath();
		mule.eat();
	}

}
