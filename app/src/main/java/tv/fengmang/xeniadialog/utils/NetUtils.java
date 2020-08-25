package tv.fengmang.xeniadialog.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;

public class NetUtils {

    private final static String TAG = "NetUtils";

    private final static String NOUSE_MAC = "02:00:00:00:00:00";

    public static String getEthMacByCmd() {
        String cmd = "cat /sys/class/net/eth0/address";
        ShellUtil.CommandResult res = ShellUtil.executeCmd(cmd, false);
        //String text = "cmd=" + cmd + ":: result =" + res.result + ":: suc =" + res.successMsg + ":: err =" + res.errorMsg;
        //ELog.d(text);
        if (res.result == 0)
            return res.successMsg;
        else
            return NOUSE_MAC;
    }

    public static String getWlanMacByCmd() {
        String cmd = "cat /sys/class/net/wlan0/address";
        ShellUtil.CommandResult res = ShellUtil.executeCmd(cmd, false);
        //String text = "cmd=" + cmd + ":: result =" + res.result + ":: suc =" + res.successMsg + ":: err =" + res.errorMsg;
        //ELog.d(text);
        if (res.result == 0)
            return res.successMsg;
        else
            return NOUSE_MAC;
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getMacAddress(Context context) {
        String ret = null;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            ret = getMacEth();
        }
        if (TextUtils.isEmpty(ret)) {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            ret = wm.getConnectionInfo().getMacAddress();

            if (NOUSE_MAC.equalsIgnoreCase(ret)) {
                ret = getUUID();
            }
        }
        if (TextUtils.isEmpty(ret)) {
            ret = getUUID();
        }
        return ret;
    }

    public static String getMacEth() {
        String macEth = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> net = intf.getInetAddresses(); net
                        .hasMoreElements(); ) {
                    InetAddress iaddr = net.nextElement();
                    if (iaddr instanceof Inet4Address) {
                        if (!iaddr.isLoopbackAddress()) {
                            byte[] data = intf.getHardwareAddress();


//                            String text = getAddressFromByte(data);
//                            if (!TextUtils.isEmpty(text)) {
//                                ELog.e("getAddressFromByte|| " + text);
//                            }

                            StringBuilder sb = new StringBuilder();
                            if (data != null && data.length > 1) {
                                sb.append(parseByte(data[0])).append(":")
                                        .append(parseByte(data[1])).append(":")
                                        .append(parseByte(data[2])).append(":")
                                        .append(parseByte(data[3])).append(":")
                                        .append(parseByte(data[4])).append(":")
                                        .append(parseByte(data[5]));
                            }
                            macEth = sb.toString();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macEth;
    }

    public static String getAddressFromByte(byte[] address) {
        if (address == null || address.length != 6) {
            return null;
        }
        return String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                address[0], address[1], address[2], address[3], address[4], address[5]);
    }


    /**
     * 得到全局唯一UUID
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
        return uuid;
    }

    private static String parseByte(byte b) {
        int intValue = 0;
        if (b >= 0) {
            intValue = b;
        } else {
            intValue = 256 + b;
        }
        String str = Integer.toHexString(intValue);
        if (str.length() == 1) {
            return "0".concat(str);  //一位，前面补零
        } else {
            return str;
        }
    }

}
