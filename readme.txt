
第一部分 介绍换肤原理和过程
1、在Application 初始化SkinManager。

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
2、初始化工作包括读取上一次的皮肤包，从皮肤资源包加载资源，注册Activity生命周期，加载皮肤。

    public SkinManager(Application application) {
        mContext = application;
//        共享首选项 用于记录当前使用的皮肤
        SkinPreference.init(mContext);
//        资源管理类 用于从 app 皮肤中加载资源
        SkinResources.init(mContext);
//        注册Activity生命周期
        skinActivityLifecycle = new SkinActivityLifecycle();
        mContext.registerActivityLifecycleCallbacks(skinActivityLifecycle);
//        加载皮肤
        loadSkin(SkinPreference.getInstance().getSkin());

    }
 3、主界面MainActivity创建成功就更新皮肤。

    public class MainActivity extends AppCompatActivity {



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            ...
            SkinManager.getInstance().updateSkin(this);
         }
    }
 4、SkinManager#updateSkin(this) 调用 SkinActivityLifecycle#updateSkin().

     public void updateSkin(Activity activity) {
         skinActivityLifecycle.updateSkin(activity);
     }
 5、SkinActivityLifecycle#updateSkin通过Factory2的子类来实现更新皮肤

     public void updateSkin(Activity activity) {
     SkinLayoutInflaterFactory skinLayoutInflaterFactory = mLayoutInflaterFactories.get(activity);
     skinLayoutInflaterFactory.update(null,null);
     }

  6、如果需要更新新的皮肤包，从服务器下载皮肤，通过校验MD5成功后通过SkinManager#loadSkin(skin.path)换肤，
    loadSkin(null)可恢复默认皮肤。

    public void loadSkin(String path) {
        //还原默认皮肤包
        if (TextUtils.isEmpty(path)) {
            SkinPreference.getInstance().setSkin("");
            SkinResources.getInstance().reset();
        } else {
            try {
                AssetManager assetManager = AssetManager.class.newInstance();
                // 添加资源进入资源管理器
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String
                        .class);
                addAssetPath.setAccessible(true);
                addAssetPath.invoke(assetManager, path);

                Resources resources = application.getResources();
                // 横竖、语言
                Resources skinResource = new Resources(assetManager, resources.getDisplayMetrics(),
                        resources.getConfiguration());
                //获取外部Apk(皮肤包) 包名
                PackageManager mPm = application.getPackageManager();
                PackageInfo info = mPm.getPackageArchiveInfo(path, PackageManager
                        .GET_ACTIVITIES);
                String packageName = info.packageName;
                SkinResources.getInstance().applySkin(skinResource, packageName);
                //保存当前使用的皮肤包
                SkinPreference.getInstance().setSkin(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //应用皮肤包
        setChanged();
        //通知观察者
        notifyObservers();
    }

7通知观察者之后，就会调用Factory2和Observer的SkinLayoutInflaterFactory实现类的update方法实现换肤。
        @Override
        public void update(Observable o, Object arg) {
            //更新状态栏颜色
            SkinThemeUtils.updateStatusBarColor(activity);
            //更新皮肤样式
            Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);
            //更新字体样式 并应用字体
            skinAttribute.setTypeface(typeface);
            skinAttribute.applySkin();
        }





 8、下面进入SkinAttribute#applySkin() 皮肤属性类分析

  public void applySkin() {
           for (SkinView mSkinView : mSkinViews) {
               mSkinView.applySkin(typeface);
           }
       }
   SkinView是需要换肤的View对象，mSkinViews是所有可换肤的对象列表


9、   SkinView#applySkin(typeface)执行换肤行为
        public void applySkin(Typeface typeface) {
            applyTypeface(typeface);// 文本换肤
            applySkinSupport();// 自定义View
            // 系统控件
            for (SkinPair skinPair : skinPairs){
                Drawable left = null,top = null,right = null,bottom=null;
                Object background;
                Log.d(TAG, "  applySkin: skinPair.attributeName  "+skinPair.attributeName+" for the view "+view);
                switch (skinPair.attributeName){
                    case "background":
                         background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer){

                            view.setBackgroundColor((Integer) background);
                        }else {
                            view.setBackground((Drawable) background);
                        }

                        break;

            ......
        }

        然而效果并没有出来，为么呢？ 查bug 走起！

第二部分 运行时问题

    上一部分代码妥妥的以为靓丽肤色从此随意拥有，结果翻车赤果果，问题找着了，首先时反射接口限制，代码中的反射调用一律无效。

    1、无法通过反射得到 mFactorySet，报错信息
 java.lang.NoSuchFieldException: No field mFactorySet in class Landroid/view/LayoutInflater;
  (declaration of 'android.view.LayoutInflater' appears in /system/framework/framework.jar!classes3.dex)     at java.lang.Class.getDeclaredField(Native Method)
 at java.lang.Class.getDeclaredField(Native Method)

    原因是 从 Android 9（API 级别 28）开始，Android 平台对应用能使用的非 SDK 接口实施了限制。
 只要应用引用非 SDK 接口或尝试使用反射或 JNI 来获取其句柄，这些限制就适用。
 这些限制旨在帮助提升用户体验和开发者体验，为用户降低应用发生崩溃的风险，同时为开发者降低紧急发布的风险。

    对于 Android 10（API 级别 29），以下文件描述了所有非 SDK 接口及其对应的名单：
 https://dl.google.com/developers/android/qt/non-sdk/hiddenapi-flags.csv

从该名单可以找到被限制的接口  Landroid/view/LayoutInflater;->mFactorySet:Z

    访问受限的非 SDK 接口时可能会出现的预期行为：
通过 Class.getDeclaredField() 或 Class.getField() 进行反射，抛出 NoSuchFieldException
在您的应用上运行测试时，如果该应用访问了某些非 SDK 接口，系统就会输出一条日志消息。您可以检查应用的日志消息，查找以下详细信息：
        声明的类、名称和类型（采用 Android 运行时所使用的格式）。
        访问方式：链接、反射或 JNI
        所访问的非 SDK 接口属于哪个名单。


   logcat | grep Access
                                                          <
    05-29 13:12:04.286 19731 19731 W ample.lsn9_ski: Accessing hidden method Landroid/content/res/AssetManager;-><init>()V (greylist, reflection, allowed)
    05-29 13:12:04.286 19731 19731 W ample.lsn9_ski: Accessing hidden method Landroid/content/res/AssetManager;->addAssetPath(Ljava/lang/String;)I (greylist, reflectio
    n, allowed)
    05-29 13:12:04.692 19731 19731 W ample.lsn9_ski: Accessing hidden field Landroid/view/LayoutInflater;->mFactorySet:Z (greylist-max-p, reflection, denied)
    05-29 13:12:05.093 19731 19731 W ample.lsn9_ski: Accessing hidden method Landroid/view/View;->computeFitSystemWindows(Landroid/graphics/Rect;Landroid/graphics/Rect
    ;)Z (greylist, reflection, allowed)
    05-29 13:12:05.094 19731 19731 W ample.lsn9_ski: Accessing hidden method Landroid/view/ViewGroup;->makeOptionalFitsSystemWindows()V (greylist, reflection, allowed)

    05-29 13:12:41.394 19731 19731 W ample.lsn9_ski: Accessing hidden field Landroid/view/LayoutInflater;->mFactorySet:Z (greylist-max-p, reflection, denied)
    05-29 13:22:16.917 19918 19918 W ample.lsn9_ski: Accessing hidden method Landroid/content/res/AssetManager;-><init>()V (greylist, reflection, allowed)
    05-29 13:22:16.919 19918 19918 W ample.lsn9_ski: Accessing hidden method Landroid/content/res/AssetManager;->addAssetPath(Ljava/lang/String;)I (greylist, reflectio
    n, allowed)
    05-29 13:22:17.617 19918 19918 W ample.lsn9_ski: Accessing hidden field Landroid/view/LayoutInflater;->mFactorySet:Z (greylist-max-p, reflection, denied)
    05-29 13:22:18.214 19918 19918 W ample.lsn9_ski: Accessing hidden method Landroid/view/View;->computeFitSystemWindows(Landroid/graphics/Rect;Landroid/graphics/Rect
    ;)Z (greylist, reflection, allowed)
    05-29 13:22:18.217 19918 19918 W ample.lsn9_ski: Accessing hidden method Landroid/view/ViewGroup;->makeOptionalFitsSystemWindows()V (greylist, reflection, allowed)

    05-29 13:25:25.251  2042  2508 I LocationAccessPolicy: Allowing com.google.android.apps.messaging fine because it doesn't target API 29 yet. Please fix this app. C
    alled from TelephonyRegistry push
    05-29 23:21:51.411 19918 19918 W ample.lsn9_ski: Accessing hidden field Landroid/view/LayoutInflater;->mFactorySet:Z (greylist-max-p, reflection, denied)


    一大堆怎么破？全都在 greylist，
起初Android9.0发布时，谷歌为非SDK接口制定了白名单（whitelist）、灰名单（greylist）、黑名单(blacklist）。
    whitelist，白名单内的api可以正常被调用。
    greylist，灰名单的api可以被调用，但未来更高的TargetSDK版本可能会将其列入黑名单。
    blacklist，黑名单的api的调用在运行时会抛出异常，如果没有进行处理，将导致APP的Crash，即使进行了try-catch，这些反射的函数/变量也无成功被调用。
后面Android 10.0发布后，非SDK接口的划分变成了：
    greylist 无限制，可以正常使用。
    blacklist 无论什么版本的手机系统，使用这些api，系统将会抛出错误。
    greylist-max-o 受限制的灰名单。APP运行在 版本<=8.0的系统里 可以正常访问，targetSDK>8.0且运行在>8.0的手机会抛出异常。
    greylist-max-p 受限制的灰名单。APP运行在 版本<=9.0的系统里 可以正常访问，targetSDK>9.0且运行在>9.0的手机会抛出异常。

