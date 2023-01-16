/**
 *
 */
package com.github.publiclibs.vpngatenet.list.utils;

import java.util.Comparator;
import java.util.stream.Stream;

import com.github.publiclibs.vpngatenet.list.data.VpnConf;

/**
 * @author freedom1b2830
 * @date 2023-января-14 01:56:19
 */
public class VpnConfStreams {
	public static Stream<VpnConf> sortByCountry(final Stream<VpnConf> vpnConfStream, final String countryCode2char) {
		return vpnConfStream.filter(conf -> conf.countryShort.equalsIgnoreCase(countryCode2char));
	}

	public static Stream<VpnConf> sortByPing(final Stream<VpnConf> vpnConfStream) {
		return vpnConfStream.sorted(Comparator.comparingInt(VpnConf::getPing).reversed());
	}

	public static Stream<VpnConf> sortBySpeed(final Stream<VpnConf> vpnConfStream) {
		return vpnConfStream.sorted(Comparator.comparingLong(VpnConf::getSpeed).reversed());
	}

	public static Stream<VpnConf> sortNFasters(final Stream<VpnConf> vpnConfStream, final long count) {
		return sortBySpeed(vpnConfStream).limit(count);
	}

	public static Stream<VpnConf> sortNFastersPing(final int count, final Stream<VpnConf> vpnConfStream) {
		return vpnConfStream.limit(count).sorted(Comparator.comparingInt(VpnConf::getPing).reversed());
	}
}
