package com.samourai.sentinel.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

//import android.util.Log;

public class ExchangeRateFactory	{

    private static Context context = null;

    private static String strDataLBC = null;
    private static String strDataBTCe = null;
    private static String strDataBFX = null;
    private static String strBittrex = null;
    private static String strBinance = null;
    private static String strUpbit = null;

    private static HashMap<String,Double> fxRatesLBC = null;
    private static HashMap<String,Double> fxRatesBTCe = null;
    private static HashMap<String,Double> fxRatesBFX = null;
    private static HashMap<String,Double> fxBittrex = null;
    private static HashMap<String,Double> fxBinance = null;
    private static HashMap<String,Double> fxUpbit = null;
//    private static HashMap<String,String> fxSymbols = null;

    private static ExchangeRateFactory instance = null;

    private static String[] currencies = {
            "CNY", "EUR", "GBP", "RUB", "USD", "KRW"
    };

    private static String[] currencyLabels = {
            "United States Dollar - USD",
            "Euro - EUR",
            "British Pound Sterling - GBP",
            "Chinese Yuan - CNY",
            "Russian Rouble - RUB",
            "Korean Won - KRW"
    };

    private static String[] currencyLabelsBTCe = {
            "United States Dollar - USD",
            "Euro - EUR",
            "Russian Rouble - RUR"
    };

    private static String[] exchangeLabels = {
            "Bittrex",
            "Binance",
            "Upbit"
    };

    private ExchangeRateFactory()	 { ; }

    public static ExchangeRateFactory getInstance(Context ctx)	 {

        context = ctx;

        if(instance == null)	 {
            fxRatesLBC = new HashMap<String,Double>();
            fxRatesBTCe = new HashMap<String,Double>();
            fxRatesBFX = new HashMap<String,Double>();
            fxBittrex = new HashMap<String,Double>();
            fxBinance = new HashMap<>();
            fxUpbit = new HashMap<>();

            instance = new ExchangeRateFactory();
        }

        return instance;
    }

    public double getAvgPrice(String currency)	 {
       // int fxSel = PrefsUtil.getInstance(context).getValue(PrefsUtil.CURRENT_EXCHANGE_SEL, 0);
        HashMap<String,Double> fxRates = null;
        /*if(!fxRatesBTCe.isEmpty() && fxRatesBTCe.containsKey(currency) && fxRatesBTCe.get(currency) > 0.0)	 {
            fxRates = fxRatesBTCe;
        }
        else if(!fxRatesBFX.isEmpty() && fxRatesBFX.containsKey(currency) && fxRatesBFX.get(currency) > 0.0)	 {
            fxRates = fxRatesBFX;
        }
        else */if(!fxRatesLBC.isEmpty() && fxRatesLBC.containsKey(currency) && fxRatesLBC.get(currency) > 0.0)	 {
            fxRates = fxRatesLBC;
        }

        double GRS_price = getAvgGRSPrice("BTC");

        if(GRS_price > 0.0 && fxRates.get(currency) != null && fxRates.get(currency) > 0.0)	 {
            PrefsUtil.getInstance(context).setValue("CANNED_" + currency, Double.toString(fxRates.get(currency)*GRS_price));
            return fxRates.get(currency)*GRS_price;
        }
        else	 {
            return Double.parseDouble(PrefsUtil.getInstance(context).getValue("CANNED_" + currency, "0.0"));
        }
    }

    public double getAvgGRSPrice(String currency)	 {
        int fxSel = PrefsUtil.getInstance(context).getValue(PrefsUtil.CURRENT_EXCHANGE_SEL, 0);
        HashMap<String,Double> fxRates = null;
        if(fxSel == 0)	 {
            fxRates = fxBittrex;
        } else if(fxSel == 1) {
            fxRates = fxBinance;
        } else {
            fxRates = fxUpbit;
        }

        if(fxRates.get(currency) != null && fxRates.get(currency) > 0.0)	 {
            PrefsUtil.getInstance(context).setValue("CANNED_" + currency, Double.toString(fxRates.get(currency)));
            return fxRates.get(currency);
        }
        else	 {
            return Double.parseDouble(PrefsUtil.getInstance(context).getValue("CANNED_" + currency, "0.0"));
        }
    }

    public String[] getCurrencies()	 {
        return currencies;
    }

    public String[] getCurrencyLabels()	 {
        return currencyLabels;
    }

    public String[] getCurrencyLabelsBTCe()	 {
        return currencyLabelsBTCe;
    }

    public String[] getExchangeLabels()	 {
        return exchangeLabels;
    }

    public void setDataLBC(String data)	 {
        strDataLBC = data;
    }

    public void setDataBTCe(String data)	 {
        strDataBTCe = data;
    }

    public void setDataBFX(String data)	 {
        strDataBFX = data;
    }

    public void parseLBC()	 {
        for(int i = 0; i < currencies.length; i++)	 {
            getLBC(currencies[i]);
        }
    }

    public void parseBTCe()	 {
        for(int i = 0; i < currencies.length; i++)	 {
            if(currencies[i].equals("GBP") || currencies[i].equals("CNY"))	 {
                continue;
            }
            if(currencies[i].equals("RUB"))	 {
                getBTCe("RUR");
            }
            else	 {
                getBTCe(currencies[i]);
            }
        }
    }

    public void parseBFX()	 {
        for(int i = 0; i < currencies.length; i++)	 {
            if(currencies[i].equals("USD"))	 {
                getBFX("USD");
            }
            else	 {
                continue;
            }
        }
    }

    public double getBitfinexPrice(String currency)	 {

        HashMap<String,Double> fxRates = fxRatesBFX;

        if(fxRates.get(currency) != null && fxRates.get(currency) > 0.0)	 {
            PrefsUtil.getInstance(context).setValue("CANNED_" + currency, Double.toString(fxRates.get(currency)));
            return fxRates.get(currency);
        }
        else	 {
            return Double.parseDouble(PrefsUtil.getInstance(context).getValue("CANNED_" + currency, "0.0"));
        }
    }

    private void getLBC(String currency)	 {
        try {
            JSONObject jsonObject = new JSONObject(strDataLBC);
            if(jsonObject != null)	{
                JSONObject jsonCurr = jsonObject.getJSONObject(currency);
                if(jsonCurr != null)	{
                    double avg_price = 0.0;
                    if(jsonCurr.has("avg_12h"))	{
                        avg_price = jsonCurr.getDouble("avg_12h");
                    }
                    else if(jsonCurr.has("avg_24h"))	{
                        avg_price = jsonCurr.getDouble("avg_24h");
                    }
                    fxRatesLBC.put(currency, Double.valueOf(avg_price));
//                    Log.i("ExchangeRateFactory", "LBC:" + currency + " " + Double.valueOf(avg_price));
                }
            }
        } catch (JSONException je) {
            fxRatesLBC.put(currency, Double.valueOf(-1.0));
//            fxSymbols.put(currency, null);
        }
    }

    private void getBTCe(String currency)	 {
        try {
            JSONObject jsonObject = new JSONObject(strDataBTCe);
            if(jsonObject != null)	{
                JSONObject jsonCurr = jsonObject.getJSONObject("btc_" + currency.toLowerCase());
                if(jsonCurr != null)	{
                    double avg_price = 0.0;
                    if(jsonCurr.has("avg"))	{
                        avg_price = jsonCurr.getDouble("avg");
                    }
                    if(currency.equals("RUR"))	{
                        fxRatesBTCe.put("RUB", Double.valueOf(avg_price));
                    }
                    fxRatesBTCe.put(currency, Double.valueOf(avg_price));
//                    Log.i("ExchangeRateFactory", "BTCe:" + currency + " " + Double.valueOf(avg_price));
                }
            }
        } catch (JSONException je) {
            fxRatesBTCe.put(currency, Double.valueOf(-1.0));
//            fxSymbols.put(currency, null);
        }
    }

    private void getBFX(String currency)	 {
        try {
            JSONObject jsonObject = new JSONObject(strDataBFX);
            if(jsonObject != null && jsonObject.has("last_price"))	{
                String strLastPrice = jsonObject.getString("last_price");
                double avg_price = Double.parseDouble(strLastPrice);
                fxRatesBFX.put(currency, Double.valueOf(avg_price));
//                    Log.i("ExchangeRateFactory", "BFX:" + currency + " " + Double.valueOf(avg_price));
            }
        }
        catch (JSONException je) {
            fxRatesBFX.put(currency, Double.valueOf(-1.0));
//            fxSymbols.put(currency, null);
        }
        catch (NumberFormatException nfe) {
            fxRatesBFX.put(currency, Double.valueOf(-1.0));
//            fxSymbols.put(currency, null);
        }
    }

    public void setDataBittrex(String str)
    {  strBittrex = str; }

    public void parseBittrex()	 {
        getBittrex();
    }

    public void setDataBinance(String str) {
        strBinance = str;
    }

    public void parseBinance() {
        getBinance();
    }

    public void setDataUpbit(String str) {
        strUpbit = str;
    }

    public void parseUpbit() {
        getUpbit();
    }

    private void getBittrex()	 {
        try {
            JSONObject jsonObject = new JSONObject(strBittrex);
            JSONArray recenttrades = jsonObject.getJSONArray("result");

            double btcTraded = 0.0;
            double coinTraded = 0.0;

            for(int i = 0; i < recenttrades.length(); ++i)
            {
                JSONObject trade = (JSONObject)recenttrades.get(i);

                btcTraded += trade.getDouble("Total");
                coinTraded += trade.getDouble("Quantity");

            }

            Double averageTrade = btcTraded / coinTraded;

            fxBittrex.put("BTC", Double.valueOf(averageTrade));
//                Log.i("ExchangeRateFactory", "BFX:" + currency + " " + Double.valueOf(avg_price));


        } catch (JSONException je) {
            fxRatesBFX.put("BTC", Double.valueOf(-1.0));
//            fxSymbols.put(currency, null);
        }
    }



    private void getBinance()	 {
        try {
            JSONObject jsonObject = new JSONObject(strBinance);
            if(jsonObject.has("symbol") && jsonObject.getString("symbol").equals("GRSBTC")) {
                fxBinance.put("BTC", Double.valueOf(jsonObject.getString("price")));
            }

        } catch (JSONException je) {
            fxRatesBFX.put("BTC", Double.valueOf(-1.0));
        }
    }

    private void getUpbit()	 {
        try {
            JSONArray array = new JSONArray(strUpbit);
            JSONObject jsonObject = array.getJSONObject(0);
            if(jsonObject.has("code") && jsonObject.getString("code").equals("CRIX.UPBIT.BTC-GRS")) {
                fxUpbit.put("BTC", Double.valueOf(jsonObject.getString("tradePrice")));
            }

        } catch (JSONException je) {
            fxUpbit.put("BTC", Double.valueOf(-1.0));
        }
    }
}
