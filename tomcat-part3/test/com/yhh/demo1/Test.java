package com.yhh.demo1;


interface ValveContext {
	public void invokeNext();
}

class SimpleValve {
	
	private String name = "默认值";
	
	public SimpleValve(String name) {
		this.name = name;
	}
	
	//
	public void invoke(ValveContext aa) {
		System.out.println(this.name); 
		aa.invokeNext();
	}
}

public class Test {

	public SimpleValve[] vs = new SimpleValve[5];
	
	public Test() {
		for (int i = 0; i < vs.length; i++) {
			vs[i] = new SimpleValve("aaa" + i);
		}
	}

	//开始遍历
	public void invoke() {
		SimplePipelineValveContext s = new SimplePipelineValveContext();
		System.out.println("s = " + s);
		s.invokeNext();
	}
	
	
	//遍历器
	protected class SimplePipelineValveContext implements ValveContext{
		
		protected int stage = 0;

		public void invokeNext(){
			int subscript = stage;
			stage = stage + 1;
			
			if (subscript < vs.length) {
				System.out.println("this = " + this); 
				vs[subscript].invoke(this);
			} 
		}
	}
	

	public static void main(String[] args) {
		new Test().invoke();
	}

}
