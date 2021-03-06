/* 
 * Copyright 2014 Jacopo Aliprandi, Dario Archetti
 * 
 * This file is part of SPF.
 * 
 * SPF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * SPF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPF.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package it.polimi.spf.app.fragments.appmanager;

import it.polimi.spf.app.R;
import it.polimi.spf.framework.security.AppAuth;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppManagerListAdapter extends ArrayAdapter<AppAuth> {

	private static final String TAG = "AppManagerListAdapter";

	public AppManagerListAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView != null) {
			view = convertView;
		} else {
			view = LayoutInflater.from(getContext()).inflate(R.layout.app_card, parent, false);
		}

		AppAuth item = getItem(position);
		ViewHolder holder = ViewHolder.from(view);

		holder.appName.setText(item.getAppName());
		holder.appIdentifier.setText(item.getAppIdentifier());

		try {
			Drawable icon = getContext().getPackageManager().getApplicationIcon(item.getAppIdentifier());
			holder.appIcon.setBackground(icon);
		} catch (NameNotFoundException e) {
			Log.d(TAG, "Application identifier is not valid", e);
		}

		return view;
	}

	private static class ViewHolder {
		public ImageView appIcon;
		public TextView appName, appIdentifier;

		public static ViewHolder from(View v) {
			Object o = v.getTag();
			if (o != null && (o instanceof ViewHolder)) {
				return (ViewHolder) o;
			}

			ViewHolder vh = new ViewHolder();
			v.setTag(vh);

			vh.appIcon = (ImageView) v.findViewById(R.id.app_icon_view);
			vh.appName = (TextView) v.findViewById(R.id.app_name_view);
			vh.appIdentifier = (TextView) v.findViewById(R.id.app_identifier_view);

			return vh;
		}
	}
}
