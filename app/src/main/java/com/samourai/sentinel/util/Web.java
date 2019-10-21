package com.samourai.sentinel.util;

import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Web	{

    public static final String BLOCKCHAIN_DOMAIN = "https://chainz.cryptoid.info/grs/";
    public static final String BLOCKCHAIN_DOMAIN_API = BLOCKCHAIN_DOMAIN + "api.dws?key=d47da926b82e&q=";
    public static final String BLOCKCHAIN_DOMAIN_TEST = "https://chainz.cryptoid.info/grs-test/";
    public static final String BLOCKCHAIN_DOMAIN_API_TEST = BLOCKCHAIN_DOMAIN_TEST + "api.dws?key=d47da926b82e&q=";
	public static final String EXCHANGE_URL = "https://localbitcoins.com/bitcoinaverage/ticker-all-currencies/";
    public static final String SAMOURAI_API2 = BLOCKCHAIN_DOMAIN_API;
    public static final String SAMOURAI_API2_TESTNET = BLOCKCHAIN_DOMAIN_API_TEST;
    public static final String LBC_EXCHANGE_URL = "https://localbitcoins.com/bitcoinaverage/ticker-all-currencies/";
    public static final String BTCe_EXCHANGE_URL = "https://wex.nz/api/3/ticker/";
    public static final String BFX_EXCHANGE_URL = "https://api.bitfinex.com/v1/pubticker/btcusd";
    public static final String BITTREX_EXCHANGE_URL = "https://bittrex.com/api/v1.1/public/getmarkethistory?market=BTC-GRS&count=25";
    public static final String BINANCE_EXCHANGE_URL = "https://api.binance.com/api/v1/ticker/price?symbol=GRSBTC";
    public static final String UPBIT_EXCHANGE_URL = "https://crix-api.upbit.com/v1/crix/trades/ticks?code=CRIX.UPBIT.BTC-GRS";

    public static final String BITCOIND_FEE_URL = "https://api.samourai.io/v2/fees";

    private static final int DefaultRequestRetry = 2;
    private static final int DefaultRequestTimeout = 60000;

    static public String postURL(String request, String urlParameters) throws Exception {

        return postURL(null, request, urlParameters);

    }

    static public String postURL(String contentType, String request, String urlParameters) throws Exception {

        String error = null;

        for (int ii = 0; ii < DefaultRequestRetry; ++ii) {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", contentType == null ? "application/x-www-form-urlencoded" : contentType);
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");

                connection.setUseCaches (false);

                connection.setConnectTimeout(DefaultRequestTimeout);
                connection.setReadTimeout(DefaultRequestTimeout);

                connection.connect();

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                connection.setInstanceFollowRedirects(false);

                if (connection.getResponseCode() == 200) {
//					Log.d("postURL", "return code 200");
                    return IOUtils.toString(connection.getInputStream(), "UTF-8");
                }
                else {
                    error = IOUtils.toString(connection.getErrorStream(), "UTF-8");
//					Log.d("postURL", "return code " + error);
                }

                Thread.sleep(5000);
            } finally {
                connection.disconnect();
            }
        }

        throw new Exception("Invalid Response " + error);
    }


    public static String getURL(String URL) throws Exception {

        URL url = new URL(URL);

        String error = null;

        for (int ii = 0; ii < DefaultRequestRetry; ++ii) {

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {
                connection.setRequestMethod("GET");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");

                connection.setConnectTimeout(DefaultRequestTimeout);
                connection.setReadTimeout(DefaultRequestTimeout);

                connection.setInstanceFollowRedirects(false);

                connection.connect();

                if (connection.getResponseCode() == 200)
                    return IOUtils.toString(connection.getInputStream(), "UTF-8");
                else
                    error = IOUtils.toString(connection.getErrorStream(), "UTF-8");

                Thread.sleep(5000);
            } finally {
                connection.disconnect();
            }
        }

        return error;
    }

}
