package tv.fengmang.xeniadialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class Ip2MacActivity extends AppCompatActivity {

    private Button btnChange;
    private EditText edtIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip2_mac);

        btnChange = findViewById(R.id.btn_change);
        edtIp = findViewById(R.id.edt_ip);
    }


    public static String getAddressFromByte(byte[] address) {
        if (address == null || address.length != 6) {
            return null;
        }
        return String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                address[0], address[1], address[2], address[3], address[4], address[5]);
    }
}
