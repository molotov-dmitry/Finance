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
import com.finance.Finance.AddFlow;
import com.finance.Finance.MyActivity;
import com.finance.Finance.R;
import com.finance.data.Account;
import com.finance.data.Flow;
import com.finance.data.Helper;

import java.util.List;

public class FlowAdapter extends BaseAdapter implements View.OnClickListener {
    private final Context context;
    private final List<Flow> flowList;
    private final boolean flowType;   //0 means outflow, 1 means inflow

    public FlowAdapter(Context context, List<Flow> flowList, boolean flowType) {
        this.context = context;
        this.flowList = flowList;
        this.flowType = flowType;
    }

    @Override
    public int getCount() {
        return flowList.size();
    }

    @Override
    public Object getItem(int position) {
        return flowList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Flow entry = flowList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.flow_row, null);
        }
        TextView edName = (TextView) convertView.findViewById(R.id.flowName);
        edName.setText(entry.getName());

        TextView edAmount = (TextView) convertView.findViewById(R.id.flowAmount);
        edAmount.setText(entry.getAmount_c());

        if (flowType)
            edAmount.setTextColor(context.getResources().getColor(R.color.green));
        else
            edAmount.setTextColor(context.getResources().getColor(R.color.red));

        TextView edDate = (TextView) convertView.findViewById(R.id.flowDate);
        edDate.setText(Helper.dateToSimpleString(entry.getDate()));

        TextView edAccount = (TextView) convertView.findViewById(R.id.flowAccount);

        Account account = Helper.getAccount(entry.getAccount());

        if (account != null) {
            edAccount.setText(account.getName());
            edAccount.setTextColor(edName.getCurrentTextColor());
        } else {
            edAccount.setText(context.getString(R.string.error));
            edAccount.setTextColor(context.getResources().getColor(R.color.red));
        }

        ImageButton btnRemove = (ImageButton) convertView.findViewById(R.id.flowRemove);
        btnRemove.setFocusableInTouchMode(false);
        btnRemove.setFocusable(false);
        btnRemove.setOnClickListener(this);
        btnRemove.setTag(R.id.TAG_FLOW_ENTRY, entry);
        btnRemove.setTag(R.id.TAG_FLOW_POSITION, position);
        btnRemove.setTag(R.id.TAG_FLOW_BTNTYPE, R.id.BTN_FLOW_REMOVE);

        ImageButton btnEdit = (ImageButton) convertView.findViewById(R.id.flowEdit);
        btnEdit.setFocusableInTouchMode(false);
        btnEdit.setFocusable(false);
        btnEdit.setOnClickListener(this);
        btnEdit.setTag(R.id.TAG_FLOW_ENTRY, entry);
        btnEdit.setTag(R.id.TAG_FLOW_POSITION, position);
        btnEdit.setTag(R.id.TAG_FLOW_BTNTYPE, R.id.BTN_FLOW_EDIT);

        return convertView;
    }

    public void onClick(View view) {

        final Flow entry = (Flow) view.getTag(R.id.TAG_FLOW_ENTRY);
        final int position = (Integer) view.getTag(R.id.TAG_FLOW_POSITION);
        int btn_type = (Integer) view.getTag(R.id.TAG_FLOW_BTNTYPE);

        switch (btn_type) {
            case R.id.BTN_FLOW_REMOVE:
                new AlertDialog.Builder(this.context)
                        .setTitle("Confirm")
                        .setMessage(R.string.flow_row_confirm_del)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Helper.delFlow(position, flowType);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case R.id.BTN_FLOW_EDIT:
                Intent intent = new Intent(context, AddFlow.class);


                intent.putExtra("Finance.flow_date", entry.getDate().getTime());
                intent.putExtra("Finance.flow_name", entry.getName());
                intent.putExtra("Finance.flow_amount", entry.getAmount());
                intent.putExtra("Finance.flow_account", entry.getAccount());
                intent.putExtra("Finance.flow_type", flowType);
                intent.putExtra("Finance.flow_pos", position);

                ((MyActivity) context).startActivityForResult(intent, R.id.REQUEST_EDIT_FLOW);
                break;
        }

    }
}
