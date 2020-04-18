package com.github.f4b6a3.ulid.demo;

import com.github.f4b6a3.ulid.UlidCreator;

public class DemoTest {

	private static final String HORIZONTAL_LINE = "----------------------------------------";

	public static void printList() {
		int max = 100;

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### ULID string");
		System.out.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			System.out.println(UlidCreator.getUlidString());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### ULID-based GUID");
		System.out.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			System.out.println(UlidCreator.getUlid());
		}
	}

	public static void main(String[] args) {
		printList();
	}
}
