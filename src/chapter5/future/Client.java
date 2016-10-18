package chapter5.future;

public class Client {

	public Data request(final String queryStr) {
		final FutureData future = new FutureData();
		new Thread() {
			// RealData的构造很慢,所以在单独的线程中进行
			public void run() {
				long start = System.currentTimeMillis();
				RealData realdata = new RealData(queryStr);
				long end = System.currentTimeMillis();
				if (realdata != null) {
					System.out.println("RealData的构造很慢,所以在单独的线程中进行, 耗时: " + (end - start));
				}
				future.setRealData(realdata);
			}
		}.start();
		return future; // FutureData会被立即返回
	}

	public static void main(String[] args) {
		Client client = new Client();
		// 这里会立即返回, 因为得到的是FutureData而不是RealData
		Data data = client.request("name");
		System.out.println("请求完毕");
		try {
			// 这里可以用一个sleep代替了对其他业务逻辑的处理,
			// 在处理这些业务逻辑的过程中, RealData被创建,从而充分利用了等待时间
			long start = System.currentTimeMillis();
			Thread.sleep(2000);
			long end = System.currentTimeMillis();
			System.out.println("这里可以用一个sleep代替了对其他业务逻辑的处理,在处理这些业务逻辑的过程中, RealData被创建,从而充分利用了等待时间, 耗时: " + (end - start));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 使用真实的数据
		System.out.println("数据 = " + data.getResult());
	}
}
