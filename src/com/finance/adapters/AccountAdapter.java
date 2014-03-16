package com.finance.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.finance.Finance.AddAccount;
import com.finance.Finance.MyActivity;
import com.finance.Finance.R;
import com.finance.data.Account;
import com.finance.data.Helper;

import java.util.List;

public class AccountAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context context;
    private final List<Account> listAccount;

    public AccountAdapter(Context context, List<Account> listAccount) {
        this.context = context;
        this.listAccount = listAccount;
    }

    @Override
    public int getCount() {
        return listAccount.size();
    }

    @Override
    public Object getItem(int position) {
        return listAccount.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Account entry = listAccount.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.acc_row, null);
        }
        TextView tvAccount = (TextView) convertView.findViewById(R.id.accName);
        tvAccount.setText(entry.getName());

        TextView tvBalance = (TextView) convertView.findViewById(R.id.accBalance);
        tvBalance.setText(entry.getBalance_c());

        if (entry.getBalance() < 0)
            tvBalance.setTextColor(context.getResources().getColor(R.color.red));
        else if (entry.getBalance() == 0)
            tvBalance.setTextColor(Color.GRAY);
        else
            tvBalance.setTextColor(context.getResources().getColor(R.color.green));

        ImageButton btnRemove = (ImageButton) convertView.findViewById(R.id.accRemove);
        btnRemove.setFocusableInTouchMode(false);
        btnRemove.setFocusable(false);
        btnRemove.setOnClickListener(this);
        btnRemove.setTag(R.id.TAG_ACC_ENTRY, entry);
        btnRemove.setTag(R.id.TAG_ACC_POSITION, position);
        btnRemove.setTag(R.id.TAG_ACC_BTNTYPE, R.id.BTN_ACC_REMOVE); //0 means btn is for removing

        ImageButton btnEdit = (ImageButton) convertView.findViewById(R.id.accEdit);
        btnEdit.setFocusableInTouchMode(false);
        btnEdit.setFocusable(false);
        btnEdit.setOnClickListener(this);
        btnEdit.setTag(R.id.TAG_ACC_ENTRY, entry);
        btnEdit.setTag(R.id.TAG_ACC_POSITION, position);
        btnEdit.setTag(R.id.TAG_ACC_BTNTYPE, R.id.BTN_ACC_EDIT); //1 means btn is for editing

        return convertView;
    }

    public void onClick(View view) {

        final Account entry = (Account) view.getTag(R.id.TAG_ACC_ENTRY);
        final int position = (Integer) view.getTag(R.id.TAG_ACC_POSITION);
        int btn_type = (Integer) view.getTag(R.id.TAG_ACC_BTNTYPE);

        switch (btn_type) {
            case R.id.BTN_ACC_REMOVE:
                new AlertDialog.Builder(this.context)
                        .setTitle("Confirm")
                        .setMessage(R.string.acc_row_confirm_del)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Helper.delAccount(position);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case R.id.BTN_ACC_EDIT:
                Intent intent = new Intent(context, AddAccount.class);

                intent.putExtra("Finance.acc_name", entry.getName());
                intent.putExtra("Finance.acc_balance", entry.getBalance());
                intent.putExtra("Finance.acc_id", position);

                ((MyActivity) context).startActivityForResult(intent, R.id.REQUEST_EDIT_ACCOUNT);
                break;
        }

    }
}
