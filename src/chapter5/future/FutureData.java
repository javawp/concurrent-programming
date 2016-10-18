package chapter5.future;

public class FutureData implements Data {

	protected RealData realdata = null; // FutureData 是RealData的包装
	protected boolean isReady = false;

	public synchronized void setRealData(RealData realdata) {
		if (isReady) {
			return;
		}
		this.realdata = realdata;
		isReady = true;
		notifyAll(); // RealData已经被注入, 通知getResult()
	}

	@Override
	public synchronized String getResult() { // 会等待RealData构造完成
		while (!isReady) {
			try {
				wait(); // 一直等待, 直到RealData被注入
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return realdata.getResult(); // 由RealData实现
	}

}
