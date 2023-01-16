/**
 *
 */
package com.github.publiclibs.vpngatenet.list.debugpackage;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.code_intelligence.jazzer.Jazzer;
import com.github.publiclibs.vpngatenet.list.utils.KeyUtils;
import com.github.publiclibs.vpngatenet.list.utils.VpnConfUtils;

/**
 * @author freedom1b2830
 * @date 2023-января-14 02:52:03
 */
public class JazzerDebug {
	public static void fullTest1(final String fullCsv) throws IOException, GeneralSecurityException {
		final var conf = VpnConfUtils.parseCSV(fullCsv);
		final var key = KeyUtils.getPrivateKeyFromString(conf.privKey);
		final var keyFormat = key.getFormat();
		keyFormat.getClass();
	}

	public static void main(final String[] args) throws IOException, InterruptedException {
		Jazzer.main(new String[] {

				"--keep_going=2000",

				"-detect_leaks=1",

				"--autofuzz=com.github.publiclibs.vpngatenet.list.debugpackage.JazzerDebug::fullTest1",

				"--autofuzz_ignore=com.github.publiclibs.vpngatenet.list.exceptions.InputException"

		});

	}
}
