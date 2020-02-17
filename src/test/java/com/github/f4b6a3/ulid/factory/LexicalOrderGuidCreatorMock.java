package com.github.f4b6a3.ulid.factory;

class LexicalOrderGuidCreatorMock extends LexicalOrderGuidCreator {
	public LexicalOrderGuidCreatorMock(long low, long high, long previousTimestamp) {
		super();
		this.low = low;
		this.high = high;
		this.previousTimestamp = previousTimestamp;
	}
}