package in.co.eko.ekopay;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EkoPayActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressDialog progressDialog;
    private String secret_key_timestamp, secret_key, developer_key, gateway_url, initiator_id,
            callback_url, user_code, initiator_logo_url, partner_name, language,
            callback_url_custom_headers, callback_url_custom_params, product, environment;

    private static final int MY_PERMISSION_REQUEST_LOCATION = 9003;     // Location Permission Request
    private static final int MY_PERMISSION_REQUEST_CAMERA = 9004;       // Location Permission Request
    private static final int REQUEST_SELECT_FILE = 9005;                // Show File Upload Dialog
    // private static final int REQUEST_SELECT_FILE_CAMERA = 9006;         // File Upload Dialog - Camera Input
    private static final int REQUEST_WRITE_EXT_STORAGE = 9007;

    private static final String TAG = "MainActivity";
    private ValueCallback<Uri[]> mFilePathCallback;
    private PermissionRequest mPermissionRequest;
    private String[] mRequestedResources;

    private String mGeolocationRequestOrigin;
    private GeolocationPermissions.Callback mGeolocationCallback;

    // Map of RdService Providers vs IntentCode.....................................................
    private static final String RD_SERVICE_PACKAGE_SECUGEN = "com.secugen.rdservice";
    private static final String RD_SERVICE_PACKAGE_MORPHO = "com.scl.rdservice";
    private static final String RD_SERVICE_PACKAGE_MANTRA = "com.mantra.rdservice";
    private static final String RD_SERVICE_PACKAGE_STARTEK = "com.acpl.registersdk";        // Startek FM220
    private static final String RD_SERVICE_PACKAGE_3M = "com.rd.gemalto.com.rdserviceapp";  // Gemalto 3M Cogent CSD 200
    //private static final String RD_SERVICE_PACKAGE_TATVIK = "com.tatvik.bio.tmf20";       // Tatvik TMF20

    private static final int RD_SERVICE_CAPTURE = 7000;

    private static final int RD_SERVICE_INFO_SECUGEN = 7001;
    private static final int RD_SERVICE_INFO_MORPHO = 7002;
    private static final int RD_SERVICE_INFO_MANTRA = 7003;
    private static final int RD_SERVICE_INFO_STARTEK = 7004;
    private static final int RD_SERVICE_INFO_3M = 7005;
    //private static final int RD_SERVICE_INFO_TATVIK = 7006;

    private static final Map<String, Integer> RDSERVICE_MAP = new HashMap<String, Integer>() {
        {
            put(RD_SERVICE_PACKAGE_SECUGEN, RD_SERVICE_INFO_SECUGEN);
            put(RD_SERVICE_PACKAGE_MORPHO, RD_SERVICE_INFO_MORPHO);
            put(RD_SERVICE_PACKAGE_MANTRA, RD_SERVICE_INFO_MANTRA);
            put(RD_SERVICE_PACKAGE_STARTEK, RD_SERVICE_INFO_STARTEK);
            put(RD_SERVICE_PACKAGE_3M, RD_SERVICE_INFO_3M);
            //put(RD_SERVICE_PACKAGE_TATVIK, RD_SERVICE_INFO_TATVIK);
        }
    };

    private String result = null;
    private String error = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eko_pay);

        webView = (WebView) findViewById(R.id.web_view);
        setWebView();

        Bundle bundle = getIntent().getExtras();
        secret_key_timestamp = (String)bundle.get("secret_key_timestamp");
        secret_key = (String)bundle.get("secret_key");
        developer_key = (String)bundle.get("developer_key");
        gateway_url = (String)bundle.get("gateway_url");
        initiator_id = (String)bundle.get("initiator_id");
        callback_url = (String)bundle.get("callback_url");
        user_code = (String)bundle.get("user_code");
        initiator_logo_url = (String)bundle.get("initiator_logo_url");
        partner_name = (String)bundle.get("partner_name");
        language = (String)bundle.get("language");
        callback_url_custom_headers = (String) bundle.get("callback_url_custom_headers");
        callback_url_custom_params = (String)bundle.get("callback_url_custom_params");
        product = (String) bundle.get("product");
        environment = (String) bundle.get("environment");

        validateData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

//        String url = "https://stagegateway.eko.in/v2/aeps";
        String url = getUrl(product, environment);
//        String url = "https://gateway.eko.in/v2/aeps";   // Production
//        String url = "http://192.168.61.37:3004/v2/aeps";  // Himanshi
        JSONObject source = new JSONObject();
        try {
            source.put("device", "Android");
            source.put("initiator_id", initiator_id);
            source.put("developer_key", developer_key);
            source.put("secret_key", secret_key);
            source.put("secret_key_timestamp", secret_key_timestamp);
            source.put("user_code", user_code);
            source.put("initiator_logo_url", initiator_logo_url);
            source.put("partner_name", partner_name);
            source.put("language", language);
            source.put("callback_url", callback_url);
            source.put("callback_url_custom_headers", callback_url_custom_headers);
            source.put("callback_url_custom_params", callback_url_custom_params);
//            source.put("callback_url_custom_params", "{\\\"param1\\\":\\\"value1\\\",\\\"param2\\\":\\\"value2\\\"}");
//            source.put("callback_url_custom_headers", "{\\\"header1\\\":\\\"header1val\\\",\\\"header2\\\":\\\"header2val\\\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String data = null;
        try {
            data = URLEncoder.encode(source.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String postData = "data="+data;

        webView.postUrl(url, postData.getBytes());
    }

    private String getUrl(String product, String environment) {
        if (environment==null) {
            environment = "production";
        }
        switch (product) {
            case "aeps":

                if (environment.equals("uat")) {
                    return "https://stagegateway.eko.in/v2/aeps";
                } else if (environment.equals("production")) {
                    return "https://gateway.eko.in/v2/aeps";
                } else {
                    error = "Please enter valid environment";
                    closeSDK();
                    return "";
                }

                default:
                    error = "Enter valid value of product";
                    closeSDK();
                    return "";






//                return "https://stagegateway.eko.in/v2/aeps";
//                return "https://gateway.eko.in/v2/aeps";   // Production
//                return "http://192.168.61.37:3004/v2/aeps";  // Himanshi

        }
    }

    private void validateData() {
        if (secret_key_timestamp == null || secret_key_timestamp.equalsIgnoreCase("")) {
            error = "secret_key_timestamp parameter not found";
            closeSDK();
        } else if(secret_key == null || secret_key.equalsIgnoreCase("")) {
            error = "secret_key parameter not found";
            closeSDK();
        } else if (developer_key == null || developer_key.equalsIgnoreCase("")) {
            error = "developer_key parameter not found";
            closeSDK();
        } else if (initiator_id == null || initiator_id.equalsIgnoreCase("")) {
            error = "initiator_id parameter not found";
            closeSDK();
        } else if (callback_url == null || callback_url.equalsIgnoreCase("")) {
            error = "callback_url parameter not found";
            closeSDK();
        } else if (user_code == null || user_code.equalsIgnoreCase("")) {
            error = "user_code parameter not found";
            closeSDK();
        } else if (initiator_logo_url == null || initiator_logo_url.equalsIgnoreCase("")) {
            error = "initiator_logo_url parameter not found";
            closeSDK();
        } else if (partner_name == null || partner_name.equalsIgnoreCase("")) {
            error = "partner_name parameter not found";
            closeSDK();
        } else if (product == null || product.equalsIgnoreCase("")) {
            error = "product parameter not found";
            closeSDK();
        }
    }

    private void setWebView() {
        WebSettings settings = webView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath(getFilesDir().getPath());
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setUserAgentString(settings.getUserAgentString() + " / ekoconnectandroidwebview");
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.setWebViewClient(new MyWebViewClient());
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebChromeClient(new ConnectWebChromeClient());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Log.d(TAG, "onDownloadStart: " + url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    public void doAction(String action, String data)
    {
        Log.d(TAG, "Android Action From SDK: " + action + "("+data+")");

        switch (action)
        {
            case "close":
                closeSDK();
                break;
            case "transaction_response":
                result = data;
                break;
            case "print_page":
                if (webView != null) {
                    createWebPrintJob(webView, data);
                }
                break;
            case "discover_rdservice":
                discoverRdService();
                break;

            case "capture_rdservice":
                captureRdService(data);
                break;
            /*case "connect_ready":
                Log.d(TAG, "Connect Ready");
                connectReady = true;
                dispatchPendingConnectmessages();
                break;

            case "check_active_login":
                checkActiveGoogleLogin();
                break;

            case "google_login":
                googleSignin();
                break;

            case "google_logout":
                googleSignout();
                break;

            case "fb_login":
                fbSignin();
                break;

            case "fb_logout":
                fbSignout();
                break;*/

//			case "share":
//				share(data);
//				break;



            /*case "save_file_blob":
                saveFileFromBlob(data);
                break;

            case "print_page":
                if (webView != null && connectReady == true) {
                    createWebPrintJob(webView, data);
                }
                break;

            case "razorpay_open":
                startRazorPayPayment(data);
                break;

            case "keep_screen_on":
                keepScreenOn(data);
                break;*/
        }
    }

    private void closeSDK() {
        if (error!=null && !error.equalsIgnoreCase("")) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", error);
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        else if(result == null || result.equalsIgnoreCase("")) {
            setResult(Activity.RESULT_CANCELED);
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", result);
            setResult(Activity.RESULT_OK, returnIntent);
        }
        finish();
    }

    // ===========================================================================================================
    //                                                  PRINT PAGE
    // ===========================================================================================================

    private void createWebPrintJob(final WebView webView, final String document_title) {

        // Get a PrintManager instance
        final PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        final String jobName = (document_title == null || document_title == "") ?
                getString(R.string.app_name) + " Document" : document_title;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Get a print adapter instance
                PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

                // Create a print job with name and adapter instance
                // PrintJob printJob =
                printManager.print(jobName, printAdapter,
                        new PrintAttributes.Builder().build());

                // Save the job object for later status checking
                // mPrintJobs.add(printJob);
            }
        });
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            final String host = Uri.parse(url).getHost();
            final String httphost = "http://" + host;
            final String httpshost = "https://" + host;

            // Log.i(TAG, "[CONNECT URL LOAD] HOST=" + host + ", URL=" + url);

            // if (host.equals("connect.eko.in") || host.equals("ekonnect.app") || host.equals("beta.ekoconnect.in") || host.equals("ekoconnect.in"))
//            if (httpshost.equals(base_url) || httphost.equals(base_url))
//            {
//                // This is my website, so do not override; let my WebView load the page
//                return false;
//            }

            // toast("Opening Link");

            // TODO: Open in Another WebView (and, give option to open in default browser)
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(getCallingActivity()!=null) {
                progressDialog.show();
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private class ConnectWebChromeClient extends WebChromeClient {

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {

            mGeolocationRequestOrigin = null;
            mGeolocationCallback = null;

            // Do we need to ask for permission?
            if (ContextCompat.checkSelfPermission(EkoPayActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Should we show the explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(EkoPayActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    new AlertDialog.Builder(EkoPayActivity.this)
                            .setMessage(R.string.permission_location_rationale)
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mGeolocationRequestOrigin = origin;
                                    mGeolocationCallback = callback;
                                    ActivityCompat.requestPermissions(EkoPayActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                                }
                            })
                            .show();
                } else {

                    // No explanation needed
                    mGeolocationRequestOrigin = origin;
                    mGeolocationCallback = callback;
                    ActivityCompat.requestPermissions(EkoPayActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                }
            } else {

                // Tell the WebView that permission has been granted
                // toast("Geolocation Permission");
                callback.invoke(origin, true, false);
            }
        }


        @Override
        public void onPermissionRequest(final PermissionRequest request) {

            Log.d(TAG, "onPermissionRequest for " + request.getResources());

            // toast(request.getResources().toString());

			/*runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (request.getOrigin().toString().startsWith(base_url)) {
						request.grant(request.getResources());
					} else {
						request.deny();
					}
				}
			});

			if (true) return; */

            mPermissionRequest = request;

            final String[] requestedResources = request.getResources();
            for (String r : requestedResources) {

                switch (r)
                {
//                    case PermissionRequest.RESOURCE_VIDEO_CAPTURE:
//                        requestPermissionHelper(request, Manifest.permission.CAMERA, R.string.permission_camera_rationale, MY_PERMISSION_REQUEST_CAMERA, requestedResources);
//                        break;
                }
            }
        }



        // This method is called when the permission request is canceled by the web content.
        @Override
        public void onPermissionRequestCanceled(PermissionRequest request) {
            Log.i(TAG, "onPermissionRequestCanceled");
            // We dismiss the prompt UI here as the request is no longer valid.
            mPermissionRequest = null;
        }


    }

    @Override
    public void onBackPressed() {
        closeSDK();
    }

    // ===========================================================================================================
    //                                                  RD SERVICE
    // ===========================================================================================================


    private void discoverRdService() {

        Intent intentServiceList = new Intent("in.gov.uidai.rdservice.fp.INFO");

        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intentServiceList, 0);

        String packageNamesStr = "";

        if (resolveInfoList.isEmpty())
        {
            sendWebViewResponse("rdservice_discovery_failed", "");
            return;
        }

        for (ResolveInfo resolveInfo :resolveInfoList)
        {
            try
            {
                Integer packageWhitelistExtra = RDSERVICE_MAP.get(resolveInfo.activityInfo.packageName);

                if (packageWhitelistExtra != null) {

                    packageNamesStr += (packageNamesStr == "" ? "" : ", ") + resolveInfo.activityInfo.packageName;

                    // Get RD Service Info
                    Intent intentInfo = new Intent("in.gov.uidai.rdservice.fp.INFO");
                    intentInfo.setPackage(resolveInfo.activityInfo.packageName);
                    startActivityForResult(intentInfo, packageWhitelistExtra);

                    Log.e(TAG, "RD SERVICE Package Found: " + resolveInfo.activityInfo.packageName);
                }
                else
                {
                    Log.e(TAG, "Connect RD Service: Unlisted Package Found: " + resolveInfo.activityInfo.packageName);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        // toast("RDSERVICES: " + packageNamesStr);

        // Testing Notifications...
        // sendWebViewResponse("fcm_push_msg", "{\"id\":112,\"notification_type\":\"0\",\"cmd\":\"1\",\"metadata\":\"\",\"title\":\"Get Loan 2 Connect notification\",\"desc\":\"Notification description\",\"markdown\":0,\"youtube\":\"\",\"image\":\"https://images6.alphacoders.com/405/405735.jpg\",\"qr_code\":\"\",\"link\":\"\",\"link_label\":\"\",\"priority\":\"3\",\"state\":2,\"notify_time\":\"2018-11-01 17:50:00\",\"expiry_time\":\"2019-10-27 18:54:00\",\"read\":0,\"rating\":0,\"reaction\":0,\"feedback\":\"\"}");
    }


    private void onRDServiceInfoResponse(Intent data, String rd_service_package)
    {
        Bundle b = data.getExtras();

        if (b != null)
        {
            sendWebViewResponse("rdservice_info", b.getString("RD_SERVICE_INFO", "") + "<RD_SERVICE_ANDROID_PACKAGE=\"" + rd_service_package + "\" />");

            Log.i(TAG, "onRDServiceInfoResponse: Device Info: \n\n Device = " + b.getString("DEVICE_INFO", "") + "    \n\nRDService = " + b.getString("RD_SERVICE_INFO", ""));
        }
    }


    private void captureRdService(String data)
    {
        try {
            JSONObject jsonData = new JSONObject(data);

            String _package = jsonData.getString("package");
            String _pidopts = jsonData.getString("pidopts");
            // String _url = jsonData.getString("url");

            Log.d(TAG, "RDSERVICE BEFORE CAPTURE: pid_options: " + _pidopts);

            // toast("PID_OPTS: " + _pidopts, Toast.LENGTH_LONG);

            // Capture fingerprint using RD Service
            Intent intentCap = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");              // _url);
            intentCap.setPackage(_package);

            intentCap.putExtra("PID_OPTIONS", _pidopts);

            startActivityForResult(intentCap, RD_SERVICE_CAPTURE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void onRDServiceCaptureResponse(Intent data)
    {
        Bundle b = data.getExtras();

        if (b != null)
        {
            sendWebViewResponse("rdservice_resp", b.getString("PID_DATA", ""));

            Log.i(TAG, "onRDServiceCaptureResponse: Capture Info: \n\n PID-DATA = " + b.getString("PID_DATA", "") + "    \n\nDeviceNotConnected = " + b.getString("DNC", ""));
        }
    }




    // ===================================================================================================

    protected void sendWebViewResponse(final String action, final String data)
    {
//        if (connectReady)
//        {
        webView.post(new Runnable() {
            @Override
            public void run() {

                String data2 = data.replaceAll("[\\n\\r]+", " ").replaceAll("'", "\'");

                Log.d(TAG, "--------- sendWebViewResponse: " + action + "  ~  " + data + "\n\n ==> " + "callFromAndroid('" + action + "', '" + data2 + "')");

                webView.evaluateJavascript("javascript:callFromAndroid('" + action + "', '" + data2 + "')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        // Value returned by the Javascript function
                    }
                });
            }
        });
//        }
        /*else
        {
            // Connect not yet ready. Cache for later...
            Log.d(TAG, "connectMsgQueue.add: " + action + "  ,  " + data);
            connectMsgQueue.add(new WebViewMessage(action, data));
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult: " + requestCode + ", " + resultCode);

        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RD_SERVICE_CAPTURE:
                    onRDServiceCaptureResponse(data);
                    break;

                case RD_SERVICE_INFO_MORPHO:
                    onRDServiceInfoResponse(data, RD_SERVICE_PACKAGE_MORPHO);
                    break;

                case RD_SERVICE_INFO_SECUGEN:
                    onRDServiceInfoResponse(data, RD_SERVICE_PACKAGE_SECUGEN);
                    break;

                case RD_SERVICE_INFO_MANTRA:
                    onRDServiceInfoResponse(data, RD_SERVICE_PACKAGE_MANTRA);
                    break;

                case RD_SERVICE_INFO_STARTEK:
                    onRDServiceInfoResponse(data, RD_SERVICE_PACKAGE_STARTEK);
                    break;
            }
        } else {
            switch (requestCode) {
                case RD_SERVICE_CAPTURE:
                    Toast.makeText(this, "Fingerprint capture failed!", Toast.LENGTH_SHORT).show();
                    break;

                case RD_SERVICE_INFO_MORPHO:
                    Toast.makeText(this, "Morpho Service Discovery Failed!", Toast.LENGTH_SHORT).show();
                    break;

                case RD_SERVICE_INFO_SECUGEN:
                    Toast.makeText(this, "Secugen Service Discovery Failed!", Toast.LENGTH_SHORT).show();
                    break;

                case RD_SERVICE_INFO_MANTRA:
                    Toast.makeText(this, "Mantra Service Discovery Failed!", Toast.LENGTH_SHORT).show();
                    break;

                case RD_SERVICE_INFO_STARTEK:
                    Toast.makeText(this, "Startek Service Discovery Failed!", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
