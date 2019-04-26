package cn.leo.zxingview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import cn.leo.produce.ZxingView;
import cn.leo.produce.decode.ResultCallBack;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ZxingViewResult:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ZxingView zxingView = findViewById(R.id.zxingView);
        zxingView.bind(this)
            .subscribe(new ResultCallBack() {
                @Override
                public void onResult(String result) {
                    Log.i(TAG, "onResult:" + result);
                }
            });


    }
}
