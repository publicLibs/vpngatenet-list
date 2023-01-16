/**
 *
 */
package com.github.publiclibs.vpngatenet.list.utils;

import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

/**
 * @author freedom1b2830
 * @date 2023-января-16 02:02:14
 */
public class KeyUtils {
	static {
		Security.addProvider(new BouncyCastleProvider());
		Security.setProperty("crypto.policy", "unlimited");
	}

	public static RSAPrivateKey getPrivateKeyFromString(final String key)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		final var targetReader = new StringReader(key);
		// read PKCS1_PEM
		final var pemParser = new PEMParser(targetReader);
		final var converter = new JcaPEMKeyConverter().setProvider("BC");
		final var object = pemParser.readObject();
		final var kp = converter.getKeyPair((PEMKeyPair) object);
		final var privateKey = kp.getPrivate();
		// convert PKCS1_RAW to PKCS8Encoded
		final java.security.spec.KeySpec spec = new java.security.spec.PKCS8EncodedKeySpec(privateKey.getEncoded());
		return (RSAPrivateKey) java.security.KeyFactory.getInstance("RSA").generatePrivate(spec);
	}
}
