package com.ssm.util;

public enum enums {
	
	STATE1("state1","打印state1"),
	STATE2("state2","打印state2"),
	STATE3("state3","打印state3");
	
	private final String state ;
	private final String msg ;
	enums(String state,String msg) {
		this.state = state ; 
		this.msg = msg ;
	}
	
	public String getState() {
		return state;
	}

	public String getMsg() {
		return msg;
	}

	public static void main(String[] args) {
		String state ="state2";
		boolean states = false;
		for (enums e : enums.values()) {
			if(e.getState().equals(state)){
				states = true;
				System.out.println(e.getMsg());
			}else{
				states = false;
			}
		}
	}
}
