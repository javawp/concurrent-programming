package chapter6;

public interface IAnimal {

	default void breath() {
		System.out.println("breath");
	}
}
