package com.guo.startAFR;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class AnotherActivity extends Activity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.another);
        Button btnClose=(Button)findViewById(R.id.close);
        btnClose.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //������ʹ��Intent����
                Intent intent = new Intent();
                //�ѷ������ݴ���Intent
                intent.putExtra("result", "Hello,I'm back!");
                //���÷�������
                AnotherActivity.this.setResult(RESULT_OK, intent);
                //�ر�Activity
                AnotherActivity.this.finish();
            }
        });        
    }
}
