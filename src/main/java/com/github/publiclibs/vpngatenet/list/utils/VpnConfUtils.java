/**
 *
 */
package com.github.publiclibs.vpngatenet.list.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.github.publiclibs.vpngatenet.list.data.VpnConf;
import com.github.publiclibs.vpngatenet.list.exceptions.InputException;
import com.github.publiclibs.vpngatenet.list.reader.OVPNReader;

/**
 * @author freedom1b2830
 * @date 2023-января-14 00:28:16
 */
public class VpnConfUtils {

	/**
	 * @param cSVs
	 * @return
	 */
	private static Stream<VpnConf> parseCSV(final List<String> cSVs) {
		final Builder<VpnConf> streamBuilder = Stream.builder();
		cSVs.parallelStream().forEachOrdered(line -> {
			if (sanitize(line)) {
				streamBuilder.add(parseCSV(line));
			}
		});
		return streamBuilder.build();

	}

	public static Stream<VpnConf> parseCSV(final Path path) throws IOException {
		if (path == null) {
			throw new InputException("path==null");
		}
		final var cSVs = Files.readAllLines(path, UTF_8);
		return parseCSV(cSVs);
	}

	public static VpnConf parseCSV(final String fullCsv) {
		if (fullCsv == null) {
			throw new InputException("fullCsv==null");
		}

		final var params = fullCsv.split(",");
		if (params.length != 15) {
			throw new InputException(String.format("[%s] %s", fullCsv, Arrays.toString(fullCsv.getBytes(UTF_8))));
		}

		final var openVPN_ConfigData_Base64 = params[14];
		if (openVPN_ConfigData_Base64 == null || openVPN_ConfigData_Base64.isEmpty()) {
			throw new InputException("openVPN_ConfigData_Base64==null");
		}

		byte[] decodedBytes;
		try {
			decodedBytes = Base64.getDecoder().decode(openVPN_ConfigData_Base64);
		} catch (final java.lang.IllegalArgumentException e) {
			throw new InputException("base64", e);
		}
		final var decodedString = new String(decodedBytes);
		final var vpnConf = OVPNReader.read(decodedString);
		vpnConf.id = params[0];
		final var host = params[1];
		if (host == null || host.isEmpty()) {
			throw new InputException("host==null");
		}
		vpnConf.host = host;
		vpnConf.speed = Long.parseLong(params[4]);

		final var countryLongTmp = params[5];
		if (countryLongTmp == null || countryLongTmp.isEmpty()) {
			throw new InputException("countryLongTmp==null");
		}
		final var countryShortTmp = params[6];
		if (countryShortTmp == null || countryShortTmp.isEmpty()) {
			throw new InputException("countryShortTmp==null");
		}
		vpnConf.countryLong = countryLongTmp;
		vpnConf.countryShort = countryShortTmp;
		if (!"-".equals(params[3])) {
			vpnConf.ping = Integer.parseInt(params[3]);
		}
		vpnConf.score = Integer.parseInt(params[2]);
		vpnConf.numVpnSessions = Integer.parseInt(params[7]);
		vpnConf.uptime = params[8];
		vpnConf.totalUsers = Integer.parseInt(params[9]);
		vpnConf.totalTraffic = Long.parseLong(params[10]);
		vpnConf.logType = params[11];// 2weeks
		vpnConf.operator = params[12];
		vpnConf.message = params[13];
		return vpnConf;
	}

	/**
	 * @param line
	 * @return
	 */
	public static boolean sanitize(final String line) {
		return !line.startsWith("#") && !line.startsWith("*");
	}

	public static String toOvpnConf(final VpnConf conf) {
		final var ovpnBuilder = new StringBuilder();
		ovpnBuilder.append("dev").append(' ').append(conf.dev).append('\n');
		ovpnBuilder.append("proto").append(' ').append(conf.proto).append('\n');
		ovpnBuilder.append("remote").append(' ').append(conf.host).append(' ').append(conf.port).append('\n');

		ovpnBuilder.append("cipher").append(' ').append(conf.cipher).append('\n');
		ovpnBuilder.append("auth").append(' ').append(conf.auth).append('\n');
		ovpnBuilder.append("resolv-retry").append(' ').append(conf.resolvretry).append('\n');
		if (conf.nobind) {
			ovpnBuilder.append("nobind").append('\n');
		}
		if (conf.persistkey) {
			ovpnBuilder.append("persist-key").append('\n');
		}
		if (conf.persisttun) {
			ovpnBuilder.append("persist-tun").append('\n');
		}
		if (conf.client) {
			ovpnBuilder.append("client").append('\n');
		}
		if (conf.verb > 0) {
			ovpnBuilder.append("verb").append(' ').append(conf.verb).append('\n');
		}

		ovpnBuilder.append("<ca>").append(conf.ca).append('\n').append("</ca>").append('\n');
		ovpnBuilder.append("<cert>").append(conf.cert).append('\n').append("</cert>").append('\n');
		ovpnBuilder.append("<key>").append(conf.privKey).append('\n').append("</key>").append('\n');
		return ovpnBuilder.toString();
	}
}
