/**
 *
 */
package com.github.publiclibs.vpngatenet.list.demo;

import java.io.IOException;

import com.github.publiclibs.vpngatenet.list.utils.VpnConfUtils;
import com.github.publiclibs.vpngatenet.list.utils.VpnGateNetFetcher;

/**
 * @author freedom1b2830
 * @date 2023-января-13 17:51:05
 */
public class VpngatenetDemo {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final var fastStream = VpnGateNetFetcher.fetchFastCountry(10, "JP");
		fastStream.forEachOrdered((final var conf) -> System.err.println(VpnConfUtils.toOvpnConf(conf)));
	}

}
