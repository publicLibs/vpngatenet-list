/**
 *
 */
package com.github.publiclibs.vpngatenet.list.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.publiclibs.vpngatenet.list.VpnGateNetFetcher;
import com.github.publiclibs.vpngatenet.list.utils.VpnConfUtils;

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

		final var fastStream = VpnGateNetFetcher.fetchFast();
		final var a = new AtomicInteger();
		fastStream.forEachOrdered((final var conf) -> {

			final var fName = a.getAndIncrement() + "-" + conf.speed + "-" + conf.countryShort + "-" + conf.proto + "-"
					+ conf.host + "-" + conf.port + ".conf";

			System.err.println("append_vpn_serviceList " + conf.host);

			final var path = Paths.get("data", fName);
			try {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
				final var data = VpnConfUtils.toOvpnConf(conf);
				Files.writeString(path, data);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
	}

}
