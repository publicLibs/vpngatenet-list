package com.example;

public class AutofuzzFuzzTest {
	/*
	 * final static CopyOnWriteArrayList<VpnConf> confs = new
	 * CopyOnWriteArrayList<>();
	 *
	 *
	 * static @BeforeAll void init() throws IOException { final var stream =
	 * VpnGateNetFetcher.fetchFast(); stream.forEachOrdered(conf ->
	 * confs.addIfAbsent(conf)); }
	 * 
	 * @FuzzTest(maxDuration = "3m") void myFuzzTest// (final FuzzedDataProvider
	 * data) { (final String string) { // csv->conf->ovpn1->conf->ovpn2 check
	 * ovpn1.equals(ovpn2) // final var string = data.consumeRemainingAsString();
	 * try { final var conf1 = VpnConfUtils.parseCSV(string); final var ovpn1 =
	 * VpnConfUtils.toOvpnConf(conf1);
	 * 
	 * final var conf2 = OVPNReader.read(ovpn1); final var ovpn2 =
	 * VpnConfUtils.toOvpnConf(conf2);
	 * 
	 * if (!ovpn1.equals(ovpn2)) { throw new RuntimeException("no valid parse"); } }
	 * catch (final InputException e) { // ignore validation } }
	 */

}