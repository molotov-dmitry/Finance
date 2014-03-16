package com.finance.Finance;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.finance.data.Helper;

import java.util.Date;

public class AddFlow extends Activity {

    private int pos;
    private boolean flowType;
    private Date curDate;
    private int curAccountPos;

    private final View.OnClickListener mDoneButton = new View.OnClickListener() {
        public void onClick(View v) {
            Intent answerIntent = new Intent();

            TextView edName = (TextView) findViewById(R.id.flow_ed_name);
            TextView edAmount = (TextView) findViewById(R.id.flow_ed_amount);

            String strName = edName.getText().toString();
            String strAmount = edAmount.getText().toString();

            long valAmount = Helper.balance(strAmount);

            answerIntent.putExtra("Finance.flow_date", curDate.getTime());
            answerIntent.putExtra("Finance.flow_name", strName);
            answerIntent.putExtra("Finance.flow_amount", valAmount);
            answerIntent.putExtra("Finance.flow_account", Helper.getAccountId(curAccountPos));
            answerIntent.putExtra("Finance.flow_type", flowType);
            answerIntent.putExtra("Finance.flow_pos", pos);

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

    private final View.OnClickListener mDatePickButton = new View.OnClickListener() {
        public void onClick(View v) {
            showDialog(R.id.CREATE_DATE_DIALOG);
        }
    };

    protected Dialog onCreateDialog(int id) {
        if (id == R.id.CREATE_DATE_DIALOG) {
            return new DatePickerDialog(this, myCallBack, curDate.getYear() + 1900, curDate.getMonth(), curDate.getDay());
        }
        return super.onCreateDialog(id);
    }

    private final DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            curDate.setYear(year - 1900);
            curDate.setMonth(monthOfYear);
            curDate.setDate(dayOfMonth);
            EditText edDate = (EditText) findViewById(R.id.flow_ed_date_label);
            edDate.setText(Helper.dateToSimpleString(curDate));
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flow_edit);

        Intent intent = getIntent();

        curDate = new Date(intent.getLongExtra("Finance.flow_date", 0));
        long valAmount = intent.getLongExtra("Finance.flow_amount", 0);
        long valAccount = intent.getLongExtra("Finance.flow_account", 0);
        String strName = intent.getStringExtra("Finance.flow_name");


        pos = intent.getIntExtra("Finance.flow_pos", 0);
        flowType = intent.getBooleanExtra("Finance.flow_type", false);

        if (flowType)
            this.setTitle("Доход");
        else
            this.setTitle("Расход");

        EditText edDate = (EditText) findViewById(R.id.flow_ed_date_label);
        edDate.setText(Helper.dateToSimpleString(curDate));

        TextView edAmount = (TextView) findViewById(R.id.flow_ed_amount);
        edAmount.setText(Long.toString(Helper.balance_r(valAmount)) + "." + Long.toString(Helper.balance_k(valAmount)));

        String[] accounts = Helper.accountNameArray();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accounts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.flow_ed_account);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                curAccountPos = pos;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        curAccountPos = Helper.getAccountIndex(valAccount);

        if (curAccountPos < 0)
            curAccountPos = 0;

        spinner.setSelection(curAccountPos);

        TextView edName = (EditText) findViewById(R.id.flow_ed_name);
        edName.setText(strName);

        Button btnDone = (Button) findViewById(R.id.flow_btn_done);
        btnDone.setOnClickListener(mDoneButton);

        Button btnCancel = (Button) findViewById(R.id.flow_btn_cancel);
        btnCancel.setOnClickListener(mCancelButton);

        ImageButton btnSetDate = (ImageButton) findViewById(R.id.flow_ed_date_btn);
        btnSetDate.setOnClickListener(mDatePickButton);
    }
}
