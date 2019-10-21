package com.samourai.sentinel.util;

public class BlockExplorerUtil {

    private static CharSequence[] blockExplorers = { "Chainz", "Groestlsight", "Blockbook" };
    private static CharSequence[] blockExplorerUrls = { "https://chainz.cryptoid.info/grs/tx.dws?", "http://groestlsight.groestlcoin.org/tx/", "https://blockbook.groestlcoin.org/tx/"};
    private static CharSequence[] blockExplorerUrlsTest = { "https://chainz.cryptoid.info/grs-test/tx.dws?", "http://groestlsight-test.groestlcoin.org/tx/", "https://blockbook.groestlcoin.org/tx/"};

    private static BlockExplorerUtil instance = null;

    private BlockExplorerUtil() { ; }

    public static BlockExplorerUtil getInstance() {

        if(instance == null) {
            instance = new BlockExplorerUtil();
        }

        return instance;
    }

    public CharSequence[] getBlockExplorers() {
        return blockExplorers;
    }

    public CharSequence[] getBlockExplorerUrls() {
        return blockExplorerUrls;
    }
    public CharSequence[] getBlockExplorerTestUrls() {
        return blockExplorerUrlsTest;
    }
}
