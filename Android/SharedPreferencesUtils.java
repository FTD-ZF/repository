

public class SharedPreferencesUtils {
    public static Boolean doSaveIndexItem(Context context, int item) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "home_item", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("item", item);
        if (editor.commit()) {
            Logger.d("doSaveIndexItem = " + item);
            return true;
        }
        return false;
    }

    public static int doGetIndexItem(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "home_item", Activity.MODE_PRIVATE);
        return sharedPreferences.getInt("item", 0);
    }

    public static void delItemData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "home_item", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
    }
}
