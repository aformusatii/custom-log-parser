package test;

import java.util.LinkedList;

public final class LogTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LinkedList<String> ll = new LinkedList<String>();
		ll.add("A1");
		ll.add("A2");
		ll.add("A3");
		
		ll.removeFirst();
		System.out.println(ll.size());
		for (String e : ll) {
			System.out.println(e);
		}
		
		System.out.println(ll.get(1));

	}

}
