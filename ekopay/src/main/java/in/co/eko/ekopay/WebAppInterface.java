package in.co.eko.ekopay;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebAppInterface {

    private Context mContext;


    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

	/* Show a toast from the web page *
	@JavascriptInterface
	public void showToast(String toast) {
		Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	} */

    /** take an action request from the web page */
    @JavascriptInterface
    public void doAction(String action, String data) {
        // Toast.makeText(mContext, "WEB ACTION: " + action /* + " (" + data + ")" */, Toast.LENGTH_SHORT).show();

        ((EkoPayActivity)mContext).doAction(action, data);
        // ((ConnectWebViewActivity)mContext).doAction(action, data);
    }

}
