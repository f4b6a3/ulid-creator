package com.github.f4b6a3.ulid.guid;

import com.github.f4b6a3.ulid.guid.GuidCreator;

class GuidCreatorMock extends GuidCreator {
	public GuidCreatorMock(long low, long high, long previousTimestamp) {
		super();
		this.low = low;
		this.high = high;
		this.previousTimestamp = previousTimestamp;
	}
}