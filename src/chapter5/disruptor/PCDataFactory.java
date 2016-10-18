package chapter5.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 产生PCData的工厂类, 它会在系统初始化的时候, 构造所有的缓冲区中的对象实例(Disruptor会预先分配空间)
 */
public class PCDataFactory implements EventFactory<PCData> {

	@Override
	public PCData newInstance() {
		return new PCData();
	}

}
