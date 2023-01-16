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
				try {
					streamBuilder.add(parseCSV(line));
				} catch (final IOException e) {
					e.printStackTrace();
				}
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

	public static VpnConf parseCSV(final String fullCsv) throws IOException {
		if (fullCsv == null) {
			throw new InputException("fullCsv==null");
		}

		final var params = fullCsv.split(",");
		if (params.length != 15) {
			throw new InputException(String.format("[%s] %s", fullCsv, Arrays.toString(fullCsv.getBytes(UTF_8))));
		}

		int ping;
		String countryLong;
		String countryShort;
		int numVpnSessions;
		String uptime;
		int totalUsers;
		long totalTraffic;
		String logType;
		String operator;
		String message;
		String openVPN_ConfigData_Base64;

		String id;
		String host;
		int score;
		String decodedString;
		long speed;
		try {
			id = params[0];
			host = params[1];
			score = Integer.parseInt(params[2]);

			if (params[3].equals("-")) {
				ping = -1;
			} else {
				ping = Integer.parseInt(params[3]);
			}

			speed = Long.parseLong(params[4]);
			countryLong = params[5];
			countryShort = params[6];
			numVpnSessions = Integer.parseInt(params[7]);
			uptime = params[8];
			totalUsers = Integer.parseInt(params[9]);
			totalTraffic = Long.parseLong(params[10]);
			logType = params[11];// 2weeks
			operator = params[12];
			message = params[13];
			openVPN_ConfigData_Base64 = params[14];
			final var decodedBytes = Base64.getDecoder().decode(openVPN_ConfigData_Base64);
			decodedString = new String(decodedBytes);
		} catch (final Exception e) {
			throw new InputException(e);
		}
		final var vpnConf = OVPNReader.read(decodedString);
		vpnConf.id = id;
		vpnConf.host = host;
		vpnConf.speed = speed;
		vpnConf.countryLong = countryLong;
		vpnConf.countryShort = countryShort;
		vpnConf.ping = ping;
		vpnConf.score = score;
		vpnConf.numVpnSessions = numVpnSessions;
		vpnConf.uptime = uptime;
		vpnConf.totalUsers = totalUsers;
		vpnConf.totalTraffic = totalTraffic;
		vpnConf.logType = logType;
		vpnConf.operator = operator;
		vpnConf.message = message;
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

		ovpnBuilder.append("<ca>").append(conf.ca).append("</ca>").append('\n');
		ovpnBuilder.append("<cert>").append(conf.cert).append("</cert>").append('\n');
		ovpnBuilder.append("<key>").append(conf.privKey).append("</key>").append('\n');
		return ovpnBuilder.toString();
	}
}
