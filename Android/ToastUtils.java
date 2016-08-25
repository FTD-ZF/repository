

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    protected static Toast toast = null;

    public static void showToast(Context context, String s) {
        toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

    
}
