package com.yhh.demo;




public class StandardPipeline implements Pipeline {

	protected Valve valves[] = new Valve[0];
	protected Valve basic = null;
	
	public void invoke() {
		// TODO Auto-generated method stub

	}

	public void setBasic(org.apache.catalina.Valve valve) {
		// TODO Auto-generated method stub
		
	}


	public void addValve(org.apache.catalina.Valve valve) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	protected class StandardPipelineValveContext 
					implements ValveContext {
		
		protected int stage = 0;
		
		public void invokeNext() {
			int subscript = stage;
            stage = stage + 1;

            if (subscript < valves.length) {
                valves[subscript].invoke(this);
            } else if ((subscript == valves.length) && (basic != null)) {
                basic.invoke(this);
            } else {
                //throw new ServletException(sm.getString("standardPipeline.noValve"));
            }
			
		}

		
	}



}
