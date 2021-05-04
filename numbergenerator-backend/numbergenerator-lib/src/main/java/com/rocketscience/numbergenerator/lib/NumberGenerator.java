package com.rocketscience.numbergenerator.lib;

import lombok.Getter;
import lombok.Setter;

public class NumberGenerator {
	@Getter @Setter
	private long defaultMaxValue = 100l;
	
	public long random(Long maxValue) {
		return (long)(Math.random() * (maxValue != null ? maxValue : defaultMaxValue) + 1);
	}
	
	public long random() {
		return this.random(null);
	}
}
