package com.finance.Finance;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.finance.data.Helper;

public class AddAccount extends Activity {

    private int pos;

    private final View.OnClickListener mDoneButton = new View.OnClickListener() {
        public void onClick(View v) {
            Intent answerIntent = new Intent();

            TextView edName = (TextView) findViewById(R.id.acc_ed_name);
            TextView edBalance = (TextView) findViewById(R.id.acc_ed_balance);

            String strName = edName.getText().toString();
            String strBalance = edBalance.getText().toString();

            long valBalance = Helper.balance(strBalance);

            answerIntent.putExtra("Finance.acc_name", strName);
            answerIntent.putExtra("Finance.acc_balance", valBalance);
            answerIntent.putExtra("Finance.acc_id", pos);
            setResult(RESULT_OK, answerIntent);
            finish();
        }
    };

    private final View.OnClickListener mCancelButton = new View.OnClickListener() {
        public void onClick(View v) {
            Intent answerIntent = new Intent();
            setResult(RESULT_CANCELED, answerIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_edit);

        Intent intent = getIntent();

        String strName = intent.getStringExtra("Finance.acc_name");
        long valBalance = intent.getLongExtra("Finance.acc_balance", 0);

        pos = intent.getIntExtra("Finance.acc_id", 0);

        TextView edName = (TextView) findViewById(R.id.acc_ed_name);
        edName.setText(strName);

        TextView edBalance = (TextView) findViewById(R.id.acc_ed_balance);
        edBalance.setText(Long.toString(Helper.balance_r(valBalance)) + "." + Long.toString(Helper.balance_k(valBalance)));

        Button btnDone = (Button) findViewById(R.id.acc_btn_done);
        btnDone.setOnClickListener(mDoneButton);

        Button btnCancel = (Button) findViewById(R.id.acc_btn_cancel);
        btnCancel.setOnClickListener(mCancelButton);
    }
}
