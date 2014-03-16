package com.finance.Finance;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import com.finance.adapters.AccountAdapter;
import com.finance.adapters.FlowAdapter;
import com.finance.data.Account;
import com.finance.data.Flow;
import com.finance.data.Helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyActivity extends Activity {

    public void onBtnClick(View v) {
        Intent intent;
        int btn_id = v.getId();
        boolean flowType = true;
        switch (btn_id) {
            case R.id.main_btn_add_out:
                flowType = false;
            case R.id.main_btn_add_in:
                if (Helper.canCreateFlow(flowType)) {
                    intent = new Intent(MyActivity.this, AddFlow.class);

                    Date dateNow = new Date();

                    intent.putExtra("Finance.flow_date", dateNow.getTime());
                    intent.putExtra("Finance.flow_name", "");
                    intent.putExtra("Finance.flow_amount", 0);
                    intent.putExtra("Finance.flow_account", 0);
                    intent.putExtra("Finance.flow_type", flowType);
                    intent.putExtra("Finance.flow_pos", 0);

                    startActivityForResult(intent, R.id.REQUEST_NEW_FLOW);
                }
                break;
            case R.id.main_btn_add_acc:
                intent = new Intent(MyActivity.this, AddAccount.class);

                intent.putExtra("Finance.acc_name", "");
                intent.putExtra("Finance.acc_balance", 0);
                intent.putExtra("Finance.acc_id", 0);

                startActivityForResult(intent, R.id.REQUEST_NEW_ACCOUNT);

        }
    }

    void createTabs() {
        TabHost tabs = (TabHost) findViewById(R.id.tabHost);

        tabs.setup();

        TabHost.TabSpec spec;

        spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab_info);
        spec.setIndicator(getString(R.string.tab_info_str));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab_out);
        spec.setIndicator(getString(R.string.tab_out_str));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag3");
        spec.setContent(R.id.tab_in);
        spec.setIndicator(getString(R.string.tab_in_str));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag4");
        spec.setContent(R.id.tab_acc);
        spec.setIndicator(getString(R.string.tab_acc_str));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
    }

    void createAccView() {
        ListView list = (ListView) findViewById(R.id.accListView);
        list.setClickable(true);

        List<Account> listOfAccount = new ArrayList<Account>();

        AccountAdapter accountAdapter = new AccountAdapter(this, listOfAccount);

        Helper.setAccountAdapter(accountAdapter);
        Helper.setAccountList(listOfAccount);

        list.setAdapter(accountAdapter);
    }

    void createInflowView() {
        ListView list = (ListView) findViewById(R.id.inflowListView);
        list.setClickable(true);

        List<Flow> listOfInflow = new ArrayList<Flow>();

        FlowAdapter inflowAdapter = new FlowAdapter(this, listOfInflow, true);

        Helper.setInflowAdapter(inflowAdapter);
        Helper.setInflowList(listOfInflow);

        list.setAdapter(inflowAdapter);
    }

    void createOutflowView() {
        ListView list = (ListView) findViewById(R.id.outflowListView);
        list.setClickable(true);

        List<Flow> listOfOutflow = new ArrayList<Flow>();

        FlowAdapter outflowAdapter = new FlowAdapter(this, listOfOutflow, false);

        Helper.setOutflowAdapter(outflowAdapter);
        Helper.setOutflowList(listOfOutflow);

        list.setAdapter(outflowAdapter);
    }

    public void updateInfo() {

        long balance = Helper.accountsBalance();
        long month_inflow = Helper.inflowThisMonth();
        long month_outflow = Helper.outflowThisMonth();
        long month_profit = month_inflow - month_outflow;

        TextView tvBalance = (TextView) findViewById(R.id.main_balance);
        tvBalance.setText(Helper.sumToCurrency(balance));

        if (balance < 0)
            tvBalance.setTextColor(getResources().getColor(R.color.red));
        else if (balance == 0)
            tvBalance.setTextColor(Color.GRAY);
        else
            tvBalance.setTextColor(getResources().getColor(R.color.green));

        TextView tvInflow = (TextView) findViewById(R.id.main_month_inflow);
        tvInflow.setText(Helper.sumToCurrency(month_inflow));

        TextView tvOutflow = (TextView) findViewById(R.id.main_month_outflow);
        tvOutflow.setText(Helper.sumToCurrency(month_outflow));


        TextView tvProfit = (TextView) findViewById(R.id.main_month_profit);
        tvProfit.setText(Helper.sumToCurrency(Math.abs(month_profit)));

        TextView tvProfitLabel = (TextView) findViewById(R.id.main_month_profit_label);

        if (month_profit >= 0) {
            tvProfitLabel.setText(getString(R.string.month_profit));
            tvProfit.setTextColor(getResources().getColor(R.color.green));
        } else {
            tvProfitLabel.setText(getString(R.string.month_loss));
            tvProfit.setTextColor(getResources().getColor(R.color.red));
        }

    }

    public void rotate() {

        LinearLayout tab_info = (LinearLayout) findViewById(R.id.tab_info);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            tab_info.setOrientation(LinearLayout.VERTICAL);
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            tab_info.setOrientation(LinearLayout.HORIZONTAL);
        else
            tab_info.setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Helper.startHelper(this);

        createTabs();

        createAccView();
        createInflowView();
        createOutflowView();

        rotate();

        Helper.loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String accName;
        long accBalance;
        int accPosition;

        switch (requestCode) {
            case R.id.REQUEST_NEW_FLOW:
                if (resultCode == RESULT_OK) {
                    Date valDate = new Date(data.getLongExtra("Finance.flow_date", 0));
                    long valAmount = data.getLongExtra("Finance.flow_amount", 0);
                    long valAccount = data.getLongExtra("Finance.flow_account", 0);
                    String strName = data.getStringExtra("Finance.flow_name");
                    boolean flowType = data.getBooleanExtra("Finance.flow_type", false);

                    Helper.addFlow(valDate, valAmount, valAccount, strName, flowType);
                }
                break;
            case R.id.REQUEST_EDIT_FLOW:
                if (resultCode == RESULT_OK) {
                    Date valDate = new Date(data.getLongExtra("Finance.flow_date", 0));
                    long valAmount = data.getLongExtra("Finance.flow_amount", 0);
                    long valAccount = data.getLongExtra("Finance.flow_account", 0);
                    String strName = data.getStringExtra("Finance.flow_name");
                    boolean flowType = data.getBooleanExtra("Finance.flow_type", false);
                    int position = data.getIntExtra("Finance.flow_pos", 0);

                    Helper.updFlow(position, valDate, valAmount, valAccount, strName, flowType);
                }
                break;
            case R.id.REQUEST_NEW_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    accName = data.getStringExtra("Finance.acc_name");
                    accBalance = data.getLongExtra("Finance.acc_balance", 0);

                    Helper.addAccount(accName, accBalance);
                }
                break;
            case R.id.REQUEST_EDIT_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    accName = data.getStringExtra("Finance.acc_name");
                    accBalance = data.getLongExtra("Finance.acc_balance", 0);
                    accPosition = data.getIntExtra("Finance.acc_id", 0);

                    Helper.updAccount(accPosition, accName, accBalance);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Helper.stopHelper();
    }
}
