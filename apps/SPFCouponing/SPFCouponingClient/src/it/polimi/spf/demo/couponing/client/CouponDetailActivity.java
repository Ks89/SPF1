package it.polimi.spf.demo.couponing.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class CouponDetailActivity extends Activity {

	private static final String EXTRA_COUPON_ID = "couponId";
	private static final int COUPON_LOADER_ID = 0;
	private static final String TAG = "CouponDetailActivity";

	public static Intent newIntent(Context context, long couponId) {
		Intent i = new Intent(context, CouponDetailActivity.class);
		i.putExtra(EXTRA_COUPON_ID, couponId);
		return i;
	}

	private long mCouponId;
	private Coupon mCoupon;
	
	private ImageView mPhotoView;
	private TextView mTitleView, mTextView, mCategoryView;
	
	private LoaderCallbacks<Coupon> mCouponLoaderCallbacks = new LoaderCallbacks<Coupon>() {
		
		@Override
		public void onLoaderReset(Loader<Coupon> arg0) {
			// Do nothing
		}
		
		@Override
		public void onLoadFinished(Loader<Coupon> arg0, Coupon coupon) {
			Log.d(TAG, "Loaded coupon: " + coupon);
			mCoupon = coupon;
			Bitmap photo = BitmapFactory.decodeByteArray(coupon.getPhoto(), 0, coupon.getPhoto().length);
			
			mPhotoView.setImageBitmap(photo);
			mTitleView.setText(coupon.getTitle());
			mTextView.setText(coupon.getText());
			mCategoryView.setText(coupon.getCategory());
		}
		
		@Override
		public Loader<Coupon> onCreateLoader(int id, Bundle args) {
			return new AsyncTaskLoader<Coupon>(CouponDetailActivity.this) {

				@Override
				public Coupon loadInBackground() {
					return ClientApplication.get().getCouponDatabase().getCouponById(mCouponId);
				}
				
			};
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coupon_detail);
		
		Bundle source = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
		if(source == null || !source.containsKey(EXTRA_COUPON_ID)){
			throw new IllegalStateException("Missing coupon ID");
		}
		
		mCouponId = source.getLong(EXTRA_COUPON_ID);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mPhotoView = (ImageView) findViewById(R.id.coupon_photo);
		mTitleView = (TextView) findViewById(R.id.coupon_title);
		mTextView = (TextView) findViewById(R.id.coupon_text);
		mCategoryView = (TextView) findViewById(R.id.coupon_category);
		
		getLoaderManager().initLoader(COUPON_LOADER_ID, null, mCouponLoaderCallbacks).forceLoad();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_coupon_detail, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_coupon_delete:
			onDeleteRequest();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(EXTRA_COUPON_ID, mCouponId);
	}

	private void onDeleteRequest(){
		new AlertDialog.Builder(this)
			.setTitle(R.string.coupon_deletion_confirm_title)
			.setMessage(R.string.coupon_deletion_confirm_message)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onDelete();
				}
			}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
	}

	private void onDelete(){
		ClientApplication.get().getCouponDatabase().deleteCoupon(mCoupon);
		finish();
	}
}
