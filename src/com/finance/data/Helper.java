package com.finance.data;

import android.content.Context;
import android.database.Cursor;
import com.finance.Finance.MyActivity;
import com.finance.adapters.AccountAdapter;
import com.finance.adapters.FlowAdapter;
import com.finance.db.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Helper {
    private static Context context;

    private static List<Account> accountList = null;
    private static AccountAdapter accountAdapter = null;

    private static List<Flow> inflowList = null;
    private static FlowAdapter inflowAdapter = null;

    private static List<Flow> outflowList = null;
    private static FlowAdapter outflowAdapter = null;

    private static DBAdapter dbAdapter;

    public static void setAccountList(List<Account> accountList) {
        Helper.accountList = accountList;
    }

    public static void setAccountAdapter(AccountAdapter accountAdapter) {
        Helper.accountAdapter = accountAdapter;
    }

    public static void setInflowList(List<Flow> inflowList) {
        Helper.inflowList = inflowList;
    }

    public static void setInflowAdapter(FlowAdapter inflowAdapter) {
        Helper.inflowAdapter = inflowAdapter;
    }

    public static void setOutflowList(List<Flow> outflowList) {
        Helper.outflowList = outflowList;
    }

    public static void setOutflowAdapter(FlowAdapter outflowAdapter) {
        Helper.outflowAdapter = outflowAdapter;
    }

    public static boolean addAccount(String name, long balance) {

        if (dbAdapter == null)
            return false;

        long accId = dbAdapter.createAccount(name, balance);

        if (accId == -1)
            return false;

        Account account = new Account(accId, name, balance);

        if (accountList != null)
            accountList.add(account);

        if (accountAdapter != null)
            accountAdapter.notifyDataSetChanged();

        if (context != null)
            ((MyActivity) context).updateInfo();

        return true;
    }

    public static boolean updAccount(int position, String name, long balance) {
        if (accountList == null)
            return false;

        if (dbAdapter == null)
            return false;

        Account account = accountList.get(position);

        boolean res = dbAdapter.updateAccount(account.getId(), name, balance);

        if (!res)
            return false;

        account.setName(name);
        account.setBalance(balance);

        if (accountAdapter != null)
            accountAdapter.notifyDataSetChanged();

        if (inflowAdapter != null)
            inflowAdapter.notifyDataSetChanged();

        if (outflowAdapter != null)
            outflowAdapter.notifyDataSetChanged();

        if (context != null)
            ((MyActivity) context).updateInfo();

        return true;
    }

    public static boolean delAccount(int position) {
        if (accountList == null)
            return false;

        if (dbAdapter == null)
            return false;

        Account account = accountList.get(position);

        if (account == null)
            return false;

        if (inflowList != null)
            for (int i = inflowList.size() - 1; i >= 0; i--)
                if (inflowList.get(i).getAccount() == account.getId())
                    delFlow(i, true);

        if (outflowList != null)
            for (int i = outflowList.size() - 1; i >= 0; i--)
                if (outflowList.get(i).getAccount() == account.getId())
                    delFlow(i, false);

        boolean res = dbAdapter.deleteAccount(account.getId());

        if (!res)
            return false;

        accountList.remove(account);

        if (accountAdapter != null)
            accountAdapter.notifyDataSetChanged();

        if (context != null)
            ((MyActivity) context).updateInfo();

        return true;
    }

    public static boolean addInflow(Date date, long amount, long account_id, String name) {
        if (dbAdapter == null)
            return false;

        long flowId = dbAdapter.createFlow(true, date, amount, account_id, name);

        if (flowId == -1)
            return false;

        Flow flow = new Flow(flowId, date, account_id, amount, name);

        if (inflowList != null)
            inflowList.add(flow);

        if (inflowAdapter != null)
            inflowAdapter.notifyDataSetChanged();

        Account account = Helper.getAccount(account_id);

        if (account != null) {
            long account_balance = account.getBalance() + amount;
            updAccount(accountList.indexOf(account), account.getName(), account_balance);
        }

        if (context != null)
            ((MyActivity) context).updateInfo();

        return true;
    }

    public static boolean updInflow(int position, Date date, long amount, long account_id, String name) {
        if (inflowList == null)
            return false;

        if (dbAdapter == null)
            return false;

        Flow flow = inflowList.get(position);

        boolean res = dbAdapter.updateFlow(true, flow.getId(), date, amount, account_id, name);

        if (!res)
            return false;

        Account account = Helper.getAccount(flow.getAccount());

        if (account != null) {
            long account_balance = account.getBalance() - flow.getAmount();
            updAccount(accountList.indexOf(account), account.getName(), account_balance);
        }

        flow.setDate(date);
        flow.setAmount(amount);
        flow.setAccount(account_id);
        flow.setName(name);

        if (inflowAdapter != null)
            inflowAdapter.notifyDataSetChanged();

        account = Helper.getAccount(account_id);

        if (account != null) {
            long account_balance = account.getBalance() + amount;
            updAccount(accountList.indexOf(account), account.getName(), account_balance);
        }

        if (context != null)
            ((MyActivity) context).updateInfo();

        return true;
    }

    public static boolean delInflow(int position) {
        if (inflowList == null)
            return false;

        if (dbAdapter == null)
            return false;

        Flow flow = inflowList.get(position);

        boolean res = dbAdapter.deleteFlow(true, flow.getId());

        if (!res)
            return false;

        Account account = Helper.getAccount(flow.getAccount());

        if (account != null) {
            long account_balance = account.getBalance() - flow.getAmount();
            updAccount(accountList.indexOf(account), account.getName(), account_balance);
        }

        inflowList.remove(flow);

        if (inflowAdapter != null)
            inflowAdapter.notifyDataSetChanged();

        if (context != null)
            ((MyActivity) context).updateInfo();

        return true;
    }

    public static boolean addOutflow(Date date, long amount, long account_id, String name) {
        if (dbAdapter == null)
            return false;

        long flowId = dbAdapter.createFlow(false, date, amount, account_id, name);

        if (flowId == -1)
            return false;

        Flow flow = new Flow(flowId, date, account_id, amount, name);

        if (outflowList != null)
            outflowList.add(flow);

        if (outflowAdapter != null)
            outflowAdapter.notifyDataSetChanged();

        Account account = Helper.getAccount(account_id);

        if (account != null) {
            long account_balance = account.getBalance() - amount;
            updAccount(accountList.indexOf(account), account.getName(), account_balance);
        }

        if (context != null)
            ((MyActivity) context).updateInfo();

        return true;
    }

    public static boolean updOutflow(int position, Date date, long amount, long account_id, String name) {
        if (outflowList == null)
            return false;

        if (dbAdapter == null)
            return false;

        Flow flow = outflowList.get(position);

        boolean res = dbAdapter.updateFlow(false, flow.getId(), date, amount, account_id, name);

        if (!res)
            return false;

        Account account = Helper.getAccount(flow.getAccount());

        if (account != null) {
            long account_balance = account.getBalance() + flow.getAmount();
            updAccount(accountList.indexOf(account), account.getName(), account_balance);
        }

        flow.setDate(date);
        flow.setAmount(amount);
        flow.setAccount(account_id);
        flow.setName(name);

        if (outflowAdapter != null)
            outflowAdapter.notifyDataSetChanged();

        account = Helper.getAccount(account_id);

        if (account != null) {
            long account_balance = account.getBalance() - amount;
            updAccount(accountList.indexOf(account), account.getName(), account_balance);
        }

        if (context != null)
            ((MyActivity) context).updateInfo();

        return true;
    }

    public static boolean delOutflow(int position) {
        if (outflowList == null)
            return false;

        if (dbAdapter == null)
            return false;

        Flow flow = outflowList.get(position);

        boolean res = dbAdapter.deleteFlow(false, flow.getId());

        if (!res)
            return false;

        Account account = Helper.getAccount(flow.getAccount());

        if (account != null) {
            long account_balance = account.getBalance() + flow.getAmount();
            updAccount(accountList.indexOf(account), account.getName(), account_balance);
        }

        outflowList.remove(flow);

        if (outflowAdapter != null)
            outflowAdapter.notifyDataSetChanged();

        if (context != null)
            ((MyActivity) context).updateInfo();

        return true;
    }

    public static boolean addFlow(Date date, long amount, long account_id, String name, boolean flowType) {
        if (flowType)
            return addInflow(date, amount, account_id, name);
        else
            return addOutflow(date, amount, account_id, name);
    }

    public static boolean updFlow(int position, Date date, long amount, long account_id, String name, boolean flowType) {
        if (flowType)
            return updInflow(position, date, amount, account_id, name);
        else
            return updOutflow(position, date, amount, account_id, name);
    }

    public static boolean delFlow(int position, boolean flowType) {
        if (flowType)
            return delInflow(position);
        else
            return delOutflow(position);
    }

    private static boolean loadAccount() {

        if (dbAdapter == null)
            return false;

        Cursor cursor = dbAdapter.fetchAllAccounts();

        int acc_id_index;
        int acc_name_index;
        int acc_balance_index;

        for (boolean cur = cursor.moveToFirst(); cur; cur = cursor.moveToNext()) {
            acc_id_index = cursor.getColumnIndex(DBHelper.KEY_ACC_ROWID);
            if (acc_id_index == -1)
                return false;

            acc_name_index = cursor.getColumnIndex(DBHelper.KEY_ACC_NAME);
            if (acc_name_index == -1)
                return false;

            acc_balance_index = cursor.getColumnIndex(DBHelper.KEY_ACC_BALANCE);
            if (acc_balance_index == -1)
                return false;

            long accId = cursor.getLong(acc_id_index);
            String accName = cursor.getString(acc_name_index);
            long accBalance = cursor.getLong(acc_balance_index);

            Account account = new Account(accId, accName, accBalance);

            if (accountList != null)
                accountList.add(account);

            if (accountAdapter != null)
                accountAdapter.notifyDataSetChanged();
        }


        return true;
    }

    private static boolean loadInflow() {
        if (dbAdapter == null)
            return false;

        Cursor cursor = dbAdapter.fetchAllFlows(true);

        int flow_id_index;
        int flow_date_index;
        int flow_amount_index;
        int flow_account_index;
        int flow_name_index;

        for (boolean cur = cursor.moveToFirst(); cur; cur = cursor.moveToNext()) {
            flow_id_index = cursor.getColumnIndex(DBHelper.KEY_FLOW_ROWID);
            if (flow_id_index == -1)
                return false;

            flow_date_index = cursor.getColumnIndex(DBHelper.KEY_FLOW_DATE);
            if (flow_date_index == -1)
                return false;

            flow_amount_index = cursor.getColumnIndex(DBHelper.KEY_FLOW_AMOUNT);
            if (flow_amount_index == -1)
                return false;

            flow_account_index = cursor.getColumnIndex(DBHelper.KEY_FLOW_ACCOUNT);
            if (flow_account_index == -1)
                return false;

            flow_name_index = cursor.getColumnIndex(DBHelper.KEY_FLOW_NAME);
            if (flow_name_index == -1)
                return false;

            long flowId = cursor.getLong(flow_id_index);
            Date flowDate = new Date(Date.parse(cursor.getString(flow_date_index)));
            long flowAmount = cursor.getLong(flow_amount_index);
            long flowAccount = cursor.getLong(flow_account_index);
            String flowName = cursor.getString(flow_name_index);

            Flow flow = new Flow(flowId, flowDate, flowAccount, flowAmount, flowName);

            if (inflowList != null)
                inflowList.add(flow);

            if (inflowAdapter != null)
                inflowAdapter.notifyDataSetChanged();
        }


        return true;
    }

    private static boolean loadOutflow() {
        if (dbAdapter == null)
            return false;

        Cursor cursor = dbAdapter.fetchAllFlows(false);

        int flow_id_index;
        int flow_date_index;
        int flow_amount_index;
        int flow_account_index;
        int flow_name_index;

        for (boolean cur = cursor.moveToFirst(); cur; cur = cursor.moveToNext()) {
            flow_id_index = cursor.getColumnIndex(DBHelper.KEY_FLOW_ROWID);
            if (flow_id_index == -1)
                return false;

            flow_date_index = cursor.getColumnIndex(DBHelper.KEY_FLOW_DATE);
            if (flow_date_index == -1)
                return false;

            flow_amount_index = cursor.getColumnIndex(DBHelper.KEY_FLOW_AMOUNT);
            if (flow_amount_index == -1)
                return false;

            flow_account_index = cursor.getColumnIndex(DBHelper.KEY_FLOW_ACCOUNT);
            if (flow_account_index == -1)
                return false;

            flow_name_index = cursor.getColumnIndex(DBHelper.KEY_FLOW_NAME);
            if (flow_name_index == -1)
                return false;

            long flowId = cursor.getLong(flow_id_index);
            Date flowDate = new Date(Date.parse(cursor.getString(flow_date_index)));
            long flowAmount = cursor.getLong(flow_amount_index);
            long flowAccount = cursor.getLong(flow_account_index);
            String flowName = cursor.getString(flow_name_index);

            Flow flow = new Flow(flowId, flowDate, flowAccount, flowAmount, flowName);

            if (outflowList != null)
                outflowList.add(flow);

            if (outflowAdapter != null)
                outflowAdapter.notifyDataSetChanged();
        }


        return true;
    }

    public static boolean loadData() {

        if (!loadAccount())
            return false;

        if (!loadInflow())
            return false;

        if (!loadOutflow())
            return false;

        if (context != null)
            ((MyActivity) context).updateInfo();

        return true;
    }

    public static void startHelper(Context context) {
        Helper.context = context;

        dbAdapter = new DBAdapter(context);
        dbAdapter.open();


    }

    public static void stopHelper() {
        dbAdapter.close();
    }

    static public long balance(String balance) {

        long balance_r = 0;
        long balance_k = 0;

        long sign = 1;

        if (!balance.isEmpty()) {
            int i = 0;

            if (balance.charAt(0) == '-') {
                sign = -1;
                i = 1;
            }


            for (; (i < balance.length()) && Character.isDigit(balance.charAt(i)); i++) {
                balance_r *= 10;
                balance_r += Character.digit(balance.charAt(i), 10);
            }

            if ((i < balance.length()) && (balance.charAt(i) == '.'))
                i++;

            int j = 0;
            for (; (i < balance.length()) && Character.isDigit(balance.charAt(i)) && (j < 2); i++) {
                balance_k *= 10;
                balance_k += Character.digit(balance.charAt(i), 10);
                j++;
            }
        }

        return balance(balance_r * sign, balance_k * sign);
    }

    static public long balance(long balance_r, long balance_k) {
        return (balance_r * 100) + balance_k;
    }

    static public long balance_r(long balance) {
        return balance / 100;
    }

    static public long balance_k(long balance) {
        return Math.abs(balance) % 100;
    }

    static public Account getAccount(long id) {
        if (accountList == null)
            return null;

        for (Account account : accountList) {
            if (account.getId() == id)
                return account;
        }

        return null;
    }

    static public int getAccountIndex(long id) {
        if (accountList == null)
            return -1;

        for (int i = 0; i < accountList.size(); i++) {
            if (accountList.get(i).getId() == id)
                return i;
        }

        return -1;
    }

    static public String[] accountNameArray() {
        String[] strings = new String[accountList.size()];

        for (int i = 0; i < accountList.size(); i++)
            strings[i] = accountList.get(i).getName();

        return strings;
    }

    static public boolean canCreateFlow(boolean flowType) {
        return !(accountList == null || outflowList == null) && accountList.size() != 0;

    }    //TODO: indexed flows

    static public String dateToSimpleString(Date date) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return df.format(date);
    }

    static public long getAccountId(int pos) {
        if (accountList == null)
            return -1;

        Account account = accountList.get(pos);

        if (account == null)
            return -1;

        return account.getId();
    }

    static public long accountsBalance() {
        if (accountList == null || accountList.size() == 0)
            return 0;

        int sum = 0;

        for (Account account : accountList)
            sum += account.getBalance();

        return sum;
    }

    static public String sumToCurrency(long sum) {
        if (sum < 0)
            return "-" + String.valueOf(Math.abs(balance_r(sum))) + "." + String.format("%02d", balance_k(sum)) + " руб.";
        else
            return String.valueOf(Math.abs(balance_r(sum))) + "." + String.format("%02d", balance_k(sum)) + " руб.";
    }

    static public long inflowThisMonth() {
        if (inflowList == null || inflowList.size() == 0)
            return 0;

        long sum = 0;

        Date today = new Date();

        int curYear = today.getYear();
        int curMonth = today.getMonth();

        for (Flow flow : inflowList) {
            Date flowDate = flow.getDate();
            int flowYear = flowDate.getYear();
            int flowMonth = flowDate.getMonth();

            if (flowYear == curYear && flowMonth == curMonth)
                sum += flow.getAmount();
        }

        return sum;
    }

    static public long outflowThisMonth() {
        if (outflowList == null || outflowList.size() == 0)
            return 0;

        long sum = 0;

        Date today = new Date();

        int curYear = today.getYear();
        int curMonth = today.getMonth();

        for (Flow flow : outflowList) {
            Date flowDate = flow.getDate();
            int flowYear = flowDate.getYear();
            int flowMonth = flowDate.getMonth();

            if (flowYear == curYear && flowMonth == curMonth)
                sum += flow.getAmount();
        }

        return sum;
    }
}
