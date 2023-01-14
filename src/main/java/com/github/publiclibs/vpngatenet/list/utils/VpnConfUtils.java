/**
 *
 */
package com.github.publiclibs.vpngatenet.list.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.github.publiclibs.vpngatenet.list.data.BuilderType;
import com.github.publiclibs.vpngatenet.list.data.Proto;
import com.github.publiclibs.vpngatenet.list.data.VpnConf;

/**
 * @author freedom1b2830
 * @date 2023-января-14 00:28:16
 */
public class VpnConfUtils {
	public static VpnConf parse(final Path path) throws IOException {
		final var lines = Files.readAllLines(path, UTF_8);
		return parseOVPNLines(lines);
	}

	public static Stream<VpnConf> parseCSV(final Path path) throws IOException {
		final var cSVs = Files.readAllLines(path, UTF_8);
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

	public static VpnConf parseCSV(final String fullCsv) throws IOException {
		final var params = fullCsv.split(",");
		if (params.length != 15) {
			throw new IllegalArgumentException(
					String.format("[%s] %s", fullCsv, Arrays.toString(fullCsv.getBytes(UTF_8))));
		}
		String openVPN_ConfigData_Base64;
		String host;
		int speed;
		String countryLong;
		String countryShort;
		int ping;
		int score;
		int numVpnSessions;
		String uptime;
		int totalUsers;
		long totalTraffic;
		String logType;
		String operator;
		String message;
		String id;
		try {
			id = params[0];
			host = params[1];
			score = Integer.parseInt(params[2]);

			if (params[3].equals("-")) {
				ping = -1;
			} else {
				ping = Integer.parseInt(params[3]);
			}

			speed = Integer.parseInt(params[4]);
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
		} catch (final Exception e) {
			System.err.println(String.format("[%s] %s", fullCsv, Arrays.toString(fullCsv.getBytes(UTF_8))));
			throw e;
		}

		final var decodedBytes = Base64.getDecoder().decode(openVPN_ConfigData_Base64);
		final var decodedString = new String(decodedBytes);
		final var ovpnConfLine = decodedString.split("\n");
		final List<String> listTmp = Arrays.asList(ovpnConfLine);

		final var list = new ArrayList<String>();
		for (final String string : listTmp) {
			if (string.length() == 1) {
				continue;
			}
			list.add(string);
		}

		final var vpnConf = parseOVPNLines(list);
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

	public static VpnConf parseOVPNLines(final Iterable<String> lines) {
		final var data = new VpnConf();
		BuilderType builderType = null;
		final var caBuilder = new StringBuilder();
		final var certBuilder = new StringBuilder();
		final var keyBuilder = new StringBuilder();

		for (final String line : lines) {
			if (line.startsWith("#") || line.startsWith(";") || line.isEmpty()) {
				continue;
			}

			final var dataAr = line.split(" ");
			final var param = dataAr[0].trim().strip().trim();

			switch (param) {
			case "dev" -> data.dev = dataAr[1];
			case "cipher" -> data.cipher = dataAr[1];
			case "auth" -> data.auth = dataAr[1];
			case "resolv-retry" -> data.resolvretry = dataAr[1];
			case "nobind" -> data.nobind = true;
			case "persist-key" -> data.persistkey = true;
			case "persist-tun" -> data.persisttun = true;
			case "client" -> data.client = true;
			case "verb" -> data.verb = Integer.parseInt(dataAr[1].trim().strip().trim());

			case "proto" -> {
				final var value = dataAr[1].trim().strip().trim();
				try {
					data.proto = Proto.valueOf(value);
				} catch (final Exception e) {
					System.err.println(Arrays.toString(value.getBytes()));
					throw e;
				}

			}
			case "remote" -> {
				data.host = dataAr[1];
				data.port = Integer.parseInt(dataAr[2].trim().strip().trim());
			}
			case "<ca>" -> {
				builderType = BuilderType.CA;
				caBuilder.append(line).append('\n');
			}
			case "</ca>" -> {
				builderType = null;
				caBuilder.append(line).append('\n');

			}

			case "<cert>" -> {
				builderType = BuilderType.CERT;
				certBuilder.append(line).append('\n');

			}
			case "</cert>" -> {
				builderType = null;
				certBuilder.append(line).append('\n');

			}

			case "<key>" -> {
				builderType = BuilderType.KEY;
				keyBuilder.append(line).append('\n');

			}
			case "</key>" -> {
				builderType = null;
				keyBuilder.append(line).append('\n');

			}

			default -> {
				if ("-----BEGIN CERTIFICATE-----".equals(line.trim().strip().trim())) {
					if (builderType == BuilderType.CA) {
						caBuilder.append(line).append('\n');
						continue;
					}
					if (builderType == BuilderType.CERT) {
						certBuilder.append(line).append('\n');
						continue;
					}
				}
				if (line.trim().strip().trim().length() == 64 || line.trim().strip().trim().length() == 56) {
					if (builderType == BuilderType.CA) {
						caBuilder.append(line.trim().strip().trim()).append('\n');
						continue;
					}
					if (builderType == BuilderType.CERT) {
						certBuilder.append(line.trim().strip().trim()).append('\n');
						continue;
					}
					if (builderType == BuilderType.KEY) {
						keyBuilder.append(line.trim().strip().trim()).append('\n');
						continue;
					}
				}
				if ("-----END CERTIFICATE-----".equals(line.trim().strip().trim())) {
					if (builderType == BuilderType.CA) {
						caBuilder.append(line.trim().strip().trim()).append('\n');
						continue;
					}
					if (builderType == BuilderType.CERT) {
						certBuilder.append(line).append('\n');
						continue;
					}
				}

				if ("-----BEGIN RSA PRIVATE KEY-----".equals(line.trim().strip().trim())
						&& builderType == BuilderType.KEY) {
					keyBuilder.append(line.trim().strip().trim()).append('\n');
					continue;
				}
				if ("-----END RSA PRIVATE KEY-----".equals(line.trim().strip().trim())
						&& builderType == BuilderType.KEY) {
					keyBuilder.append(line.trim().strip().trim()).append('\n');
					continue;
				}
				throw new IllegalArgumentException("Unexpected value: " + param + " in line [" + line + "]");
			}
			}
		}
		data.ca = caBuilder.toString();
		data.privKey = keyBuilder.toString();
		data.cert = certBuilder.toString();
		return data;
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

		ovpnBuilder.append(conf.ca).append('\n');
		ovpnBuilder.append(conf.cert).append('\n');
		ovpnBuilder.append(conf.privKey).append('\n');

		return ovpnBuilder.toString();
	}
}
