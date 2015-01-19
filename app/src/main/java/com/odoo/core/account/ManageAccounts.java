/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 *
 * Created on 19/12/14 2:30 PM
 */
package com.odoo.core.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.OdooActivity;
import com.odoo.crm.R;
import com.odoo.core.auth.OdooAccountManager;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OActionBarUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;

public class ManageAccounts extends ActionBarActivity implements View.OnClickListener {

    private List<OUser> accounts = new ArrayList<OUser>();
    private ListView mList = null;
    private ArrayAdapter<OUser> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_manage_accounts);
        setTitle(R.string.label_accounts);
        OActionBarUtils.setActionBar(this, true);
        setResult(RESULT_CANCELED);
        accounts.clear();
        accounts.addAll(OdooAccountManager.getAllAccounts(this));
        mList = (ListView) findViewById(R.id.accountList);
        mAdapter = new ArrayAdapter<OUser>(this, R.layout.base_account_item, accounts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(ManageAccounts.this).inflate(R.layout.base_account_item, parent, false);
                }
                generateView(convertView, getItem(position));
                return convertView;
            }
        };
        mList.setAdapter(mAdapter);
    }

    private void generateView(View view, OUser user) {
        OControls.setText(view, R.id.accountName, user.getName());
        OControls.setText(view, R.id.accountURL, (user.isOAauthLogin()) ? user.getInstanceUrl() : user.getHost());
        OControls.setImage(view, R.id.profile_image, R.drawable.avatar);
        if (!user.getAvatar().equals("false")) {
            Bitmap bmp = BitmapUtils.getBitmapImage(this, user.getAvatar());
            if (bmp != null)
                OControls.setImage(view, R.id.profile_image, bmp);
        }
        if (user.isIsactive()) {
            OControls.setVisible(view, R.id.btnLogout);
            OControls.setGone(view, R.id.btnLogin);
        } else {
            OControls.setGone(view, R.id.btnLogout);
            OControls.setVisible(view, R.id.btnLogin);
        }
        view.findViewById(R.id.btnLogin).setTag(user);
        view.findViewById(R.id.btnLogout).setTag(user);
        view.findViewById(R.id.btnRemoveAccount).setTag(user);
        view.findViewById(R.id.btnLogout).setOnClickListener(this);
        view.findViewById(R.id.btnLogin).setOnClickListener(this);
        view.findViewById(R.id.btnRemoveAccount).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                OUser user = (OUser) v.getTag();
                OdooAccountManager.login(this, user.getAndroidName());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ManageAccounts.this, OResource.string(ManageAccounts.this,
                                R.string.status_login_success), Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                }, OdooActivity.DRAWER_ITEM_LAUNCH_DELAY);
                break;
            case R.id.btnLogout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_confirm);
                builder.setMessage(R.string.toast_are_you_sure_logout);
                builder.setPositiveButton(R.string.label_logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OUser user = (OUser) v.getTag();
                        OdooAccountManager.logout(ManageAccounts.this, user.getAndroidName());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ManageAccounts.this, OResource.string(ManageAccounts.this,
                                        R.string.status_logout_success), Toast.LENGTH_LONG).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, OdooActivity.DRAWER_ITEM_LAUNCH_DELAY);

                    }
                });
                builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            case R.id.btnRemoveAccount:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_confirm);
                builder.setMessage(R.string.toast_are_you_sure_delete_account);
                builder.setPositiveButton(R.string.label_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OUser user = (OUser) v.getTag();
                        OdooAccountManager.removeAccount(ManageAccounts.this, user.getAndroidName());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ManageAccounts.this, OResource.string(ManageAccounts.this,
                                        R.string.toast_account_removed), Toast.LENGTH_LONG).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, OdooActivity.DRAWER_ITEM_LAUNCH_DELAY);

                    }
                });
                builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }
    }
}