package com.jd.apploader;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

/**
 * Created by Administrator on 2016/11/25.
 */

public class App extends Application {

//    public static Application sApplication;
//    private String apkFileName;
//    private String apkFilePath;
//    private String apkLibPath;

    private Object obj;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

//        printAssets();

        String appName = getReinforceApkAppName();

        obj = onAppAttach(this, appName);

        Log.e("AppLoader", "attachBaseContext");
//        try {
//            loadApp(this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        loadResources(apkFileName);

    }

//    private void printAssets() {
//        try {
//            Log.e("AppLoader", "assets files: " + Arrays.asList(getAssets().list("")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onCreate() {
        super.onCreate();

//        String appName = getReinforceApkAppName();

        onAppCreate(this, obj, null);  //"com.snowfish.cn.ganga.offline.helper.SFOfflineApplication"

        Log.e("AppLoader", "onApplicationCreate");
//        try {
//            createApkApp("com.souying.pay.SouYingApplication");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private String getReinforceApkAppName() {
        String appName = null;
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            appName = applicationInfo.metaData.getString("application_name");
            appName = "null".equals(appName) ? null : appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appName;
    }

//    private void changeProvider(Object currentActivityThread, Object app) {
//        ArrayMap mProviderMap = (ArrayMap) RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mProviderMap");
//        Iterator it = mProviderMap.values().iterator();
//        while (it.hasNext()) {
//            Object providerClientRecord = it.next();
//            Object provider = RefInvoke.getFieldOjbect("android.app.ActivityThread$ProviderClientRecord", providerClientRecord, "mLocalProvider");
//            if(provider != null) {
//                RefInvoke.setFieldOjbect("android.content.ContentProvider", "mContext", provider, app);
//            }
//        }
//    }

//    public void loadApp(Application application) throws IOException {
//        File apkPath = application.getDir("apkFile", Context.MODE_PRIVATE);
//        File libPath = application.getDir("apkLib", Context.MODE_PRIVATE);
//        apkFilePath = apkPath.getAbsolutePath();
//        apkLibPath = libPath.getAbsolutePath();
//        apkFileName = apkPath.getAbsolutePath() + "/loader.apk";
//
//        Log.d("AppLoader", "apk lib path: " + apkLibPath);
//
//        copyApkFile(application);
//        Object currentActivityThread = RefInvoke.invokeStaticMethod(
//                "android.app.ActivityThread", "currentActivityThread",
//                new Class[] {}, new Object[] {});
//        Map mPackages = (Map) RefInvoke.getFieldOjbect(
//                "android.app.ActivityThread", currentActivityThread,
//                "mPackages");
//        WeakReference wr = (WeakReference) mPackages.get(application.getPackageName());
//        DexClassLoader dLoader = new DexClassLoader(apkFileName, apkFilePath,
//                apkLibPath, (ClassLoader) RefInvoke.getFieldOjbect(
//                "android.app.LoadedApk", wr.get(), "mClassLoader"));
//        RefInvoke.setFieldOjbect("android.app.LoadedApk", "mClassLoader",
//                wr.get(), dLoader);
//        RefInvoke.setFieldOjbect("android.app.LoadedApk", "mResDir", wr.get(), apkFileName);
//
//    }

//    private void copyApkFile(Application application) throws IOException {
//        InputStream in = application.getAssets().open("Ldal.bin");
//        File apkFile = new File(apkFileName);
//        if(apkFile.exists()) {
//            if(apkFile.length() == in.available()) {
//                in.close();
//                return;
//            } else {
//                apkFile.delete();
//            }
//        }
//
//        apkFile.createNewFile();
//
//        FileOutputStream fout = new FileOutputStream(apkFile);
//        byte[] buffer = new byte[1024];
//        int readLen = 0;
//        while ((readLen = in.read(buffer)) > 0) {
//            for(int i = 0; i < readLen; i++) {
//                buffer[i] = (byte) (buffer[i] ^ 0xa1);
//            }
//
//            fout.write(buffer, 0, readLen);
//        }
//
//        fout.flush();
//        fout.close();
//        in.close();
//
//        copyLibFile();
//    }

//    private void copyLibFile() throws IOException {
//        File apkFile = new File(apkFileName);
//        ZipInputStream localZipInputStream = new ZipInputStream(
//                new BufferedInputStream(new FileInputStream(apkFile)));
//        while (true) {
//            ZipEntry localZipEntry = localZipInputStream.getNextEntry();
//            if (localZipEntry == null) {
//                localZipInputStream.close();
//                break;
//            }
//            String name = localZipEntry.getName();
//            if (name.startsWith("lib/") && name.endsWith(".so")) {
//                File storeFile = new File(apkLibPath + "/"
//                        + name.substring(name.lastIndexOf('/')));
//                storeFile.createNewFile();
//                FileOutputStream fos = new FileOutputStream(storeFile);
//                byte[] arrayOfByte = new byte[1024];
//                while (true) {
//                    int i = localZipInputStream.read(arrayOfByte);
//                    if (i == -1)
//                        break;
//                    fos.write(arrayOfByte, 0, i);
//                }
//                fos.flush();
//                fos.close();
//            }
//            localZipInputStream.closeEntry();
//        }
//        localZipInputStream.close();
//    }

//    private void createApkApp(String appName) throws Exception {
//        if(TextUtils.isEmpty(appName)) {
//            appName = "android.app.Application";
//        }
//        Object currentActivityThread = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread", new Class[0], new Object[0]);
//        Object mBoundApplication = RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mBoundApplication");
//        Object loadedApkInfo = RefInvoke.getFieldOjbect("android.app.ActivityThread$AppBindData", mBoundApplication, "info");
//        RefInvoke.setFieldOjbect("android.app.LoadedApk", "mApplication", loadedApkInfo, null);
//        Object oldApplication = RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mInitialApplication");
//        ArrayList<Application> mAllApplications = (ArrayList<Application>) RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mAllApplications");
//        mAllApplications.remove(oldApplication);
//
//        ApplicationInfo appinfo_In_LoadedApk = (ApplicationInfo) RefInvoke.getFieldOjbect("android.app.LoadedApk", loadedApkInfo, "mApplicationInfo");
//        ApplicationInfo appinfo_In_AppBindData = (ApplicationInfo) RefInvoke.getFieldOjbect("android.app.ActivityThread$AppBindData", mBoundApplication, "appInfo");
//
//        RefInvoke.setFieldOjbect("android.content.pm.ApplicationInfo", "sourceDir", appinfo_In_LoadedApk, apkFileName);
//        RefInvoke.setFieldOjbect("android.app.LoadedApk", "mResDir", loadedApkInfo, apkFileName);
//        RefInvoke.setFieldOjbect("android.app.LoadedApk", "mResources", loadedApkInfo, null);
//
//        appinfo_In_LoadedApk.className = appName;
//        appinfo_In_AppBindData.className = appName;
//
//        Application app = (Application) RefInvoke.invokeMethod("android.app.LoadedApk", "makeApplication", loadedApkInfo, new Class[] {boolean.class, Instrumentation.class}, new Object[] {false, null});
//        ArrayMap mProviderMap = (ArrayMap) RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mProviderMap");
//        Iterator it = mProviderMap.values().iterator();
//        while (it.hasNext()) {
//            Object providerClientRecord = it.next();
//            Object provider = RefInvoke.getFieldOjbect("android.app.ActivityThread$ProviderClientRecord", providerClientRecord, "mLocalProvider");
//            if(provider != null) {
//                RefInvoke.setFieldOjbect("android.content.ContentProvider", "mContext", provider, app);
//            }
//        }
//
//        app.onCreate();
//    }
//
//    private void createApplication() {
//        try {
//            Class clsActThread = Class.forName("android.app.ActivityThread");
//            Method currentActivityThread = clsActThread.getDeclaredMethod("currentActivityThread", new Class[0]);
//            currentActivityThread.setAccessible(true);
//            Object activityThread = currentActivityThread.invoke(null, new Object[0]);
//            Field mPackages = clsActThread.getDeclaredField("mPackages");
//            mPackages.setAccessible(true);
//            Map packages = (Map) mPackages.get(activityThread);
//            WeakReference wr = (WeakReference) packages.get(getPackageName());
//            Class clsLoadedApk = Class.forName("android.app.LoadedApk");
//            Field mClassLoader = clsLoadedApk.getDeclaredField("mClassLoader");
//            mClassLoader.setAccessible(true);
//            ClassLoader classLoader = (ClassLoader) mClassLoader.get(wr.get());
//            Class clsApp = classLoader.loadClass("com.excelliance.open.LBApplication");
//            clsApp.getDeclaredField("appContext").set(null, sApplication);
//            Application application = (Application) clsApp.newInstance();
//            application.onCreate();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
//    }

    private native void onAppCreate(Application app, Object obj, String application);
    private native Object onAppAttach(Application app, String application);

//    以下是加载资源
    protected static AssetManager mAssetManager;//资源管理器
    protected Resources mResources;//资源
    protected Resources.Theme mTheme;//主题

//    protected void loadResources(String dexPath) {
//        try {
//            AssetManager assetManager = AssetManager.class.newInstance();
//            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
//            addAssetPath.invoke(assetManager, dexPath);
//            mAssetManager = assetManager;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Resources superRes = super.getResources();
//        superRes.getDisplayMetrics();
//        superRes.getConfiguration();
//        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),superRes.getConfiguration());
//        mTheme = mResources.newTheme();
//        mTheme.setTo(super.getTheme());
//    }


    @Override
    public AssetManager getAssets() {
        AssetManager assetManager = mAssetManager == null ? super.getAssets() : mAssetManager;
        return assetManager;
    }

    @Override
    public Resources getResources() {
        if(mAssetManager != null && mResources == null) {
            Resources superRes = super.getResources();
            mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        }

        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    public Resources.Theme getTheme() {
        if(mResources != null && mTheme == null) {
            mTheme = mResources.newTheme();
            mTheme.setTo(super.getTheme());
        }

        return mTheme == null ? super.getTheme() : mTheme;
    }
}
