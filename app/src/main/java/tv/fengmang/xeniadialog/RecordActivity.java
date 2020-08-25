package tv.fengmang.xeniadialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import tv.fengmang.xeniadialog.record.AudioRecorder;
import tv.fengmang.xeniadialog.record.RecordStreamListener;

public class RecordActivity extends AppCompatActivity {
    TextView tvRecord;
    TextView tvStopRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        tvRecord = findViewById(R.id.tv_record);
        tvStopRecord = findViewById(R.id.tv_stop_record);


        String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        AudioRecorder.getInstance().createDefaultAudio(fileName);

        tvRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRecord.setEnabled(false);
                AudioRecorder.getInstance().startRecord(new RecordStreamListener() {
                    @Override
                    public void recordOfByte(byte[] data, int begin, int end) {

                    }
                });
            }
        });

        tvStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRecord.setEnabled(true);
                AudioRecorder.getInstance().stopRecord();
            }
        });

    }
}
