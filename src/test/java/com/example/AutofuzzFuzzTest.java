package com.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.github.publiclibs.vpngatenet.list.VpnGateNetFetcher;
import com.github.publiclibs.vpngatenet.list.data.VpnConf;
import com.github.publiclibs.vpngatenet.list.exceptions.InputException;
import com.github.publiclibs.vpngatenet.list.utils.VpnConfUtils;

class AutofuzzFuzzTest {
	final static CopyOnWriteArrayList<VpnConf> confs = new CopyOnWriteArrayList<>();

	static @BeforeAll void init() throws IOException {
		final var stream = VpnGateNetFetcher.fetchFast();
		stream.forEachOrdered(conf -> confs.addIfAbsent(conf));
	}

	public @FuzzTest(maxDuration = "3m") void myFuzzTest(final FuzzedDataProvider data) {
		final var string = data.consumeRemainingAsString();
		try {
			VpnConfUtils.parseCSV(string);
		} catch (final InputException e) {
			// ignore vaidation
		}
	}

	@Test
	void testKeys() {
		assertTrue(!confs.isEmpty());
	}
}