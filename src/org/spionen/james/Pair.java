package org.spionen.james;

public class Pair<A, B> {

	public A a;
	public B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A first() {
		return a;
	}
	
	public B second() {
		return b;
	}
}
