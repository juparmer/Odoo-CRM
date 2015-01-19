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
 * Created on 13/1/15 4:59 PM
 */
package com.odoo.addons.phonecall.services;

import android.os.Bundle;

import com.odoo.addons.phonecall.models.CRMPhoneCalls;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

import odoo.ODomain;

public class PhoneCallSyncService extends OSyncService {
    public static final String TAG = PhoneCallSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter() {
        return new OSyncAdapter(getApplicationContext(), CRMPhoneCalls.class, this, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        if (adapter.getModel().getModelName().equals("crm.phonecall")) {
            ODomain domain = new ODomain();
            domain.add("user_id", "=", user.getUser_id());
            adapter.setDomain(domain).syncDataLimit(10);
        }
    }
}