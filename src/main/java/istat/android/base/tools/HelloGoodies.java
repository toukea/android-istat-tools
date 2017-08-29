package istat.android.base.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

public class HelloGoodies {
	public static void applyGoodies(final View view, final int count,
			final int text, GoodiesCallBack callBack) {
		applyGoodies(view, count, view.getContext().getString(text), callBack);
	}

	public static void applyGoodies(final View view, final int count,
			final int text) {
		applyGoodies(view, count, view.getContext().getString(text), null);
	}

	public static void applyGoodies(final View view, final int count,
			final String text) {
		applyGoodies(view, count, text, null);
	}

	public static void applyGoodies(final View view, final int count,
			final String text, final GoodiesCallBack callBack) {
		view.setOnClickListener(new OnClickListener() {
			int clicCount = 0;

			// int lastTrigg=(int) (System.currentTimeMillis());
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clicCount++;
				// if(System.currentTimeMillis()-lastTrigg>=2000) clicCount=0;
				if (clicCount >= count) {
					displayGoodies(text, view, callBack);
					clicCount = 0;
				}

			}
		});
	}

	static void displayGoodies(String text, View view, GoodiesCallBack callBack) {
		Dialogs.displayDialogExclamation(view.getContext(), "Hello Goodies",
				text, "OK");
		if (callBack != null)
			callBack.onGoodiesShow(view);

	}

	public static class Dialogs {
		public static Dialog displayDialogExclamation(Context context,
				String... args) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			// ((Activity)context).getWindowManager().
			// builder.setIcon(R.drawable.icon);
			builder.setTitle(args[0])
					.setMessage(args[1])
					.setCancelable(false)
					.setPositiveButton(args[2],
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									try {
										dialog.cancel();
									} catch (Exception e) {
									}
								}
							});
			AlertDialog alert = builder.create();
			// if(!((Activity)(context)).isDestroyed())
			try {
				alert.show();
			} catch (Exception e) {
			}
			return alert;
		}
	}

	public static interface GoodiesCallBack {
		public abstract void onGoodiesShow(View v);
	}
}
