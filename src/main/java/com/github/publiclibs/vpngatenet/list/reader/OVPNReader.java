/**
 *
 */
package com.github.publiclibs.vpngatenet.list.reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.github.publiclibs.vpngatenet.list.data.Proto;
import com.github.publiclibs.vpngatenet.list.data.VpnConf;
import com.github.publiclibs.vpngatenet.list.exceptions.InputException;

/**
 * @author freedom1b2830
 * @date 2023-января-16 01:06:56
 */
public class OVPNReader {
	public static void main(final String[] args) throws IOException {
		read(Paths.get("data/0-1494003758-JP-tcp-219.100.37.66-443.conf"));
	}

	/**
	 * @param path
	 * @throws IOException
	 */
	private static void read(final Path path) throws IOException {
		read(Files.readString(path, StandardCharsets.UTF_8));
	}

	public static VpnConf read(final String fullString) {
		final var dataBuilder = new StringBuilder();
		final var dataAr = fullString.split("\n");
		for (String string : dataAr) {
			if (string.contains("\r")) {
				string = string.substring(0, string.length() - 1);
			}
			if (string.isEmpty()) {
				continue;
			}
			if (string.startsWith("#")) {
				continue;
			}
			if (string.startsWith(";")) {
				continue;
			}
			dataBuilder.append(string).append('\n');
		}
		var ca = StringUtils.substringBetween(dataBuilder.toString(), "<ca>", "</ca>");
		if (ca == null) {
			throw new InputException("ca == null");
		}

		var cert = StringUtils.substringBetween(dataBuilder.toString(), "<cert>", "</cert>");
		if (cert == null) {
			throw new InputException("cert == null");
		}
		var key = StringUtils.substringBetween(dataBuilder.toString(), "<key>", "</key>");
		if (key == null) {
			throw new InputException("key == null");
		}
		final var data = dataBuilder.toString().replace(ca, "").replace(cert, "").replace(key, "")
				.replaceAll("<ca></ca>", "").replaceAll("<cert></cert>", "").replaceAll("<key></key>", "");

		final var dataAr2 = data.split("\n");
		final var dataBuilder2 = new StringBuilder();
		for (final String string : dataAr2) {
			if (string.isEmpty()) {
				continue;
			}
			dataBuilder2.append(string).append('\n');
		}
		final var paramsAR = dataBuilder2.toString().split("\n");

		final var ovpnConf = new VpnConf();
		for (var i = 0; i < paramsAR.length; i++) {
			final var paramLine = paramsAR[i];
			final var paramData = paramLine.split(" ");
			final var paramKey = paramData[0];
			if (paramData.length == 1) {
				switch (paramKey) {
				case "nobind" -> ovpnConf.nobind = true;
				case "client" -> ovpnConf.client = true;
				case "persist-key" -> ovpnConf.persistkey = true;
				case "persist-tun" -> ovpnConf.persisttun = true;
				default -> throw new IllegalArgumentException("Unexpected value: " + paramKey);
				}
			} else if (paramData.length == 2) {
				final var paramVal = paramData[1];
				switch (paramKey) {
				case "dev" -> ovpnConf.dev = paramVal;
				case "cipher" -> ovpnConf.cipher = paramVal;
				case "data-ciphers" -> ovpnConf.dataCiphers = paramVal;
				case "auth" -> ovpnConf.auth = paramVal;
				case "verb" -> ovpnConf.verb = Integer.parseInt(paramVal);
				case "resolv-retry" -> ovpnConf.resolvretry = paramVal;
				case "proto" -> ovpnConf.proto = Proto.valueOf(paramVal);
				default ->
					throw new IllegalArgumentException("Unexpected value: [" + paramKey + "] val:[" + paramVal + "]");
				}
			} else {
				if ("remote".equals(paramKey)) {
					final var tmpHost = paramData[1];
					if (tmpHost == null || tmpHost.isEmpty()) {
						throw new InputException("remote host");
					}
					final var tmpPort = Integer.parseInt(paramData[2]);
					if (tmpPort < 0 || tmpPort > 65535) {
						throw new InputException("remote host");
					}
					ovpnConf.host = tmpHost;
					ovpnConf.port = tmpPort;
					continue;
				}
				throw new UnsupportedOperationException(Arrays.toString(paramData));
			}
		}
		if (ca.endsWith("\n")) {
			ca = ca.substring(0, ca.length() - 1);
		}
		if (cert.endsWith("\n")) {
			cert = cert.substring(0, cert.length() - 1);
		}
		if (key.endsWith("\n")) {
			key = key.substring(0, key.length() - 1);
		}

		ovpnConf.ca = ca;
		ovpnConf.cert = cert;
		ovpnConf.privKey = key;
		return ovpnConf;
	}

}
