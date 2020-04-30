package org.smartregister.hf.util;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.smartregister.CoreLibrary;
import org.smartregister.hf.BuildConfig;
import org.smartregister.hf.R;
import org.smartregister.hf.application.AddoApplication;
import org.smartregister.hf.contract.FamilyCallDialogContract;
import org.smartregister.hf.fragment.CopyToClipboardDialog;
import org.smartregister.util.PermissionUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static com.google.android.gms.common.internal.Preconditions.checkArgument;

/**
 * Author : Isaya Mollel on 2019-10-18.
 */

public class Utils extends org.smartregister.family.util.Utils {

    private static String TAG = Utils.class.getCanonicalName();

    public static final SimpleDateFormat dd_MMM_yyyy = new SimpleDateFormat("dd MMM yyyy");
    public static final SimpleDateFormat yyyy_mm_dd = new SimpleDateFormat("yyyy-mm-dd");
    private static List<String> assets;

    public static String firstCharacterUppercase(String str) {
        if (TextUtils.isEmpty(str)) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String convertToDateFormateString(String timeAsDDMMYYYY, SimpleDateFormat dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");//12-08-2018
        try {
            Date date = sdf.parse(timeAsDDMMYYYY);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean launchDialer(final Activity activity, final FamilyCallDialogContract.View callView, final String phoneNumber) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // set a pending call execution request
            if (callView != null) {
                callView.setPendingCallRequest(new FamilyCallDialogContract.Dialer() {
                    @Override
                    public void callMe() {
                        Utils.launchDialer(activity, callView, phoneNumber);
                    }
                });
            }

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE);

            return false;
        } else {

            if (((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number()
                    == null) {

                Timber.i( "No dial application so we launch copy to clipboard...");

                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(activity.getText(R.string.copied_phone_number), phoneNumber);
                clipboard.setPrimaryClip(clip);

                CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(activity, R.style.copy_clipboard_dialog);
                copyToClipboardDialog.setContent(phoneNumber);
                copyToClipboardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                copyToClipboardDialog.show();
                // no phone
                Toast.makeText(activity, activity.getText(R.string.copied_phone_number), Toast.LENGTH_SHORT).show();

            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                activity.startActivity(intent);
            }
            return true;
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateB != null && stateA.equals(stateB))
                || getBitmap(drawableA).sameAs(getBitmap(drawableB));
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }

    public static int getOverDueProfileImageResourceIDentifier() {
        return R.color.visit_status_over_due;
    }

    public static int getDueProfileImageResourceIDentifier() {
        return R.color.visit_status_ok;
    }

    public static Integer daysBetweenDateAndNow(String date) {
        DateTime duration;
        if (StringUtils.isNotBlank(date)) {
            try {
                duration = new DateTime(new Date(Long.valueOf(date)));
                Days days = Days.daysBetween(duration.withTimeAtStartOfDay(), DateTime.now().withTimeAtStartOfDay());
                return days.getDays();
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
        return null;
    }

    public static String actualDaysBetweenDateAndNow(Context context, String date) {
        Integer days = daysBetweenDateAndNow(date);
        if (days != null) {
            if (days <= 1) {
                return days + getStringSpacePrefix(context, R.string.day);
            } else return days + getStringSpacePrefix(context, R.string.days);
        }
        return "";
    }

    public static String actualDuration(Context context, String duration) {
        List<String> printList = new ArrayList<>();
        String[] splits = duration.split("\\s+");
        for (String s : splits) {
            if (s.contains("d")) {
                printList.add(replaceSingularPlural(s, "d", getStringSpacePrefix(context, R.string.day), getStringSpacePrefix(context, R.string.days)));
            } else if (s.contains("w")) {
                printList.add(replaceSingularPlural(s, "w", getStringSpacePrefix(context, R.string.week), getStringSpacePrefix(context, R.string.weeks)));
            } else if (s.contains("m")) {
                printList.add(replaceSingularPlural(s, "m", getStringSpacePrefix(context, R.string.month), getStringSpacePrefix(context, R.string.months)));
            } else if (s.contains("y")) {
                printList.add(replaceSingularPlural(s, "y", getStringSpacePrefix(context, R.string.year), getStringSpacePrefix(context, R.string.years)));
            }
        }

        return StringUtils.join(printList, " ");
    }

    public static String getBuildDate(Boolean isShortMonth) {
        String simpleDateFormat;
        if (isShortMonth) {
            simpleDateFormat =
                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.BUILD_TIMESTAMP));
        } else {
            simpleDateFormat =
                    new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.BUILD_TIMESTAMP));
        }
        return simpleDateFormat;
    }

    private static String replaceSingularPlural(String string, String dwmyString, String singular, String plural) {
        int dwmy = Integer.valueOf(string.substring(0, string.indexOf(dwmyString)));
        return " " + string.replace(dwmyString, dwmy > 1 ? plural : singular);
    }

    private static String getStringSpacePrefix(Context context, int resId) {
        return " " + context.getString(resId);
    }

    /**
     * Check is the file exists
     *
     * @param form_name
     * @return
     */
    public static String getLocalForm(String form_name) {
        Locale current = AddoApplication.getCurrentLocale();

        String formIdentity = MessageFormat.format("{0}_{1}", form_name, current.getLanguage());
        // validate variant exists
        try {
            if (assets == null) {
                assets = new ArrayList<>();
            }

            if (assets.size() == 0) {
                String[] local_assets = AddoApplication.getInstance().getApplicationContext().getAssets().list("json.form");
                if (local_assets != null && local_assets.length > 0) {
                    for (String s : local_assets) {
                        assets.add(s.substring(0, s.length() - 5));
                    }
                }
            }

            if (assets.contains(formIdentity)) {
                return formIdentity;
            }
        } catch (Exception e) {
            // return default
            return form_name;
        }
        return form_name;
    }

    public static String getLocalForm(String form_name, Locale locale, AssetManager assetManager) {
        return getFileName(form_name, locale, assetManager);
    }


    public static String getFileName(String form_name, Locale current, AssetManager assetManager) {
        String formIdentity = MessageFormat.format("{0}_{1}", form_name, current.getLanguage());
        try {
            if (assets == null) {
                assets = new ArrayList<>();
            }


            if (assets.size() == 0) {
                String[] local_assets = assetManager.list("json.form");
                if (local_assets != null && local_assets.length > 0) {
                    for (String s : local_assets) {
                        assets.add(s.substring(0, s.length() - 5));
                    }
                }
            }

            if (assets.contains(formIdentity)) {
                return formIdentity;
            }
        } catch (Exception e) {
            Timber.v(e);
        }
        return form_name;
    }

    public static String getAncMemberNameAndAge(String firstName, String middleName, String surName, String age) {
        int integerAge = new Period(new DateTime(age), new DateTime()).getYears();
        String first_name = firstName.trim();
        String middle_name = middleName.trim();
        String sur_name = surName != null ? surName.trim() : "";

        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(middleName) && StringUtils.isNotBlank(age)) {
            return (first_name + " " + middle_name + " " + sur_name).trim() + ", " + integerAge;
        }
        return "";
    }

    public static String getDayOfMonthSuffix(String n) {
        return getDayOfMonthSuffix(Integer.parseInt(n));
    }

    public static String getDayOfMonthSuffix(final int n) {
        checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

    public static org.smartregister.Context context() {
        return CoreLibrary.getInstance().context();
    }

}

