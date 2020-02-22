package com.github.f4b6a3.demo;

import com.github.f4b6a3.ulid.UlidCreator;

public class DemoTest {

	private static final String HORIZONTAL_LINE = "----------------------------------------";

	public static void printList() {
		int max = 1_000;

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### ULID");
		System.out.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			System.out.println(UlidCreator.getUlid());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### GUID");
		System.out.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			System.out.println(UlidCreator.getGuid());
		}
	}

	public static void main(String[] args) {
		printList();
	}
}
