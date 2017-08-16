package cn.upfinder.editnumviewdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import cn.upfinder.editnumview.EditNumView;

public class MainActivity extends AppCompatActivity {


    private Context context;
    private EditNumView editNumView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        editNumView = (EditNumView) findViewById(R.id.editNumView);
        editNumView.setOnWarnListener(new EditNumView.OnWarnListener() {
            @Override
            public void onWarningForInventory(int inventory) {

                Toast.makeText(context, "剩余库存" + inventory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWarningForMax(int max) {

                Toast.makeText(context, "购买最大值" + max, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWarningForMin(int min) {

                Toast.makeText(context, "购买最小值" + min, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
