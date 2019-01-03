package test;

public class Test {

	public static void main(String[] args) {

		final Test b = new Test();
		new Thread(new Runnable() {
			public void run() {
				b.f();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				b.g();
			}
		}).start();
		
	}


	boolean available = false;
	public synchronized void f() {
		while(!available) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(1);
	}
	
	public synchronized void g() {
		while (available) {
            try {
                wait();
            } catch (InterruptedException e) {
            	e.printStackTrace();
            }
        }
		System.out.println(2);
		available = true;
		notifyAll();
	}
}
