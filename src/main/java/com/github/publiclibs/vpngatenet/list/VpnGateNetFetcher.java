/**
 *
 */
package com.github.publiclibs.vpngatenet.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.github.publiclibs.vpngatenet.list.data.VpnConf;
import com.github.publiclibs.vpngatenet.list.utils.VpnConfStreams;
import com.github.publiclibs.vpngatenet.list.utils.VpnConfUtils;

/**
 * @author freedom1b2830
 * @date 2023-января-14 00:41:51
 */
public class VpnGateNetFetcher {
	public static final String urlString = "http://www.vpngate.net/api/iphone/";

	public static Stream<VpnConf> fetch() throws IOException {
		final Builder<VpnConf> streamBuilder = Stream.builder();
		final var website = new URL(urlString);
		final var connection = website.openConnection();
		try (var in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String line;
			while ((line = in.readLine()) != null) {
				if (VpnConfUtils.sanitize(line)) {
					streamBuilder.add(VpnConfUtils.parseCSV(line));
				}
			}
		}
		return streamBuilder.build();
	}

	public static Stream<VpnConf> fetchCountry(final String country) throws IOException {
		if (country == null || country.length() != 2) {
			throw new IllegalArgumentException("country error: " + country);
		}
		return VpnConfStreams.sortByCountry(VpnGateNetFetcher.fetch(), country);
	}

	public static Stream<VpnConf> fetchCountryFast(final String country) throws IOException {
		return VpnConfStreams.sortBySpeed(fetchCountry(country));
	}

	public static Stream<VpnConf> fetchFast() throws IOException {
		return VpnConfStreams.sortBySpeed(fetch());
	}

	public static Stream<VpnConf> fetchFast(final long count) throws IOException {
		return VpnConfStreams.sortNFasters(fetchFast(), count);
	}

	public static Stream<VpnConf> fetchFastCountry(final int count, final String country) throws IOException {
		return VpnConfStreams.sortNFasters(fetchCountry(country), count);
	}
}
