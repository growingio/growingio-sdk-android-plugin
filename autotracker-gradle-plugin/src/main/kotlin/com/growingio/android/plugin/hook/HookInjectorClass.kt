// This class is auto-generated by Inject-Processor, please don't modify it!
package com.growingio.android.plugin.hook

import kotlin.Boolean
import kotlin.String
import kotlin.collections.MutableList

public object HookInjectorClass {
  private val AROUND_HOOK_CLASSES: MutableList<HookData> = mutableListOf()

  private val SUPER_HOOK_CLASSES: MutableList<HookData> = mutableListOf()

  private val TARGET_HOOK_CLASSES: MutableList<HookData> = mutableListOf()

  public fun initAroundClass(): MutableList<HookData> {
    AROUND_HOOK_CLASSES.clear()
    AROUND_HOOK_CLASSES.add(HookData("android/app/AlertDialog","show","()V","com/growingio/android/sdk/autotrack/inject/DialogInjector","alertDialogShow","(Landroid/app/AlertDialog;)V",true))
    AROUND_HOOK_CLASSES.add(HookData("com/uc/webview/export/WebView","loadUrl","(Ljava/lang/String;)V","com/growingio/android/sdk/autotrack/inject/UcWebViewInjector","ucWebViewLoadUrl","(Lcom/uc/webview/export/WebView;Ljava/lang/String;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("com/uc/webview/export/WebView","loadUrl","(Ljava/lang/String;Ljava/util/Map;)V","com/growingio/android/sdk/autotrack/inject/UcWebViewInjector","ucWebViewLoadUrl","(Lcom/uc/webview/export/WebView;Ljava/lang/String;Ljava/util/Map;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("com/uc/webview/export/WebView","loadData","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V","com/growingio/android/sdk/autotrack/inject/UcWebViewInjector","ucWebViewLoadData","(Lcom/uc/webview/export/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("com/uc/webview/export/WebView","loadDataWithBaseURL","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V","com/growingio/android/sdk/autotrack/inject/UcWebViewInjector","ucWebViewLoadDataWithBaseURL","(Lcom/uc/webview/export/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("com/uc/webview/export/WebView","postUrl","(Ljava/lang/String;[B)V","com/growingio/android/sdk/autotrack/inject/UcWebViewInjector","ucWebViewPostUrl","(Lcom/uc/webview/export/WebView;Ljava/lang/String;[B)V",false))
    AROUND_HOOK_CLASSES.add(HookData("android/webkit/WebView","loadUrl","(Ljava/lang/String;)V","com/growingio/android/sdk/autotrack/inject/WebViewInjector","webkitWebViewLoadUrl","(Landroid/webkit/WebView;Ljava/lang/String;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("android/webkit/WebView","loadUrl","(Ljava/lang/String;Ljava/util/Map;)V","com/growingio/android/sdk/autotrack/inject/WebViewInjector","webkitWebViewLoadUrl","(Landroid/webkit/WebView;Ljava/lang/String;Ljava/util/Map;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("android/webkit/WebView","loadData","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V","com/growingio/android/sdk/autotrack/inject/WebViewInjector","webkitWebViewLoadData","(Landroid/webkit/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("android/webkit/WebView","loadDataWithBaseURL","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V","com/growingio/android/sdk/autotrack/inject/WebViewInjector","webkitWebViewLoadDataWithBaseURL","(Landroid/webkit/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("android/webkit/WebView","postUrl","(Ljava/lang/String;[B)V","com/growingio/android/sdk/autotrack/inject/WebViewInjector","webkitWebViewPostUrl","(Landroid/webkit/WebView;Ljava/lang/String;[B)V",false))
    AROUND_HOOK_CLASSES.add(HookData("com/tencent/smtt/sdk/WebView","loadUrl","(Ljava/lang/String;)V","com/growingio/android/sdk/autotrack/inject/X5WebViewInjector","x5WebViewLoadUrl","(Lcom/tencent/smtt/sdk/WebView;Ljava/lang/String;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("com/tencent/smtt/sdk/WebView","loadUrl","(Ljava/lang/String;Ljava/util/Map;)V","com/growingio/android/sdk/autotrack/inject/X5WebViewInjector","x5WebViewLoadUrl","(Lcom/tencent/smtt/sdk/WebView;Ljava/lang/String;Ljava/util/Map;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("com/tencent/smtt/sdk/WebView","loadData","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V","com/growingio/android/sdk/autotrack/inject/X5WebViewInjector","x5WebViewLoadData","(Lcom/tencent/smtt/sdk/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("com/tencent/smtt/sdk/WebView","loadDataWithBaseURL","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V","com/growingio/android/sdk/autotrack/inject/X5WebViewInjector","x5WebViewLoadDataWithBaseURL","(Lcom/tencent/smtt/sdk/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",false))
    AROUND_HOOK_CLASSES.add(HookData("com/tencent/smtt/sdk/WebView","postUrl","(Ljava/lang/String;[B)V","com/growingio/android/sdk/autotrack/inject/X5WebViewInjector","x5WebViewPostUrl","(Lcom/tencent/smtt/sdk/WebView;Ljava/lang/String;[B)V",false))
    return AROUND_HOOK_CLASSES
  }

  public fun initSuperClass(): MutableList<HookData> {
    SUPER_HOOK_CLASSES.clear()
    SUPER_HOOK_CLASSES.add(HookData("android/app/Activity","onNewIntent","(Landroid/content/Intent;)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","onActivityNewIntent","(Landroid/app/Activity;Landroid/content/Intent;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/accounts/AccountAuthenticatorActivity","onNewIntent","(Landroid/content/Intent;)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","onActivityNewIntent","(Landroid/app/Activity;Landroid/content/Intent;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ActivityGroup","onNewIntent","(Landroid/content/Intent;)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","onActivityNewIntent","(Landroid/app/Activity;Landroid/content/Intent;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/AliasActivity","onNewIntent","(Landroid/content/Intent;)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","onActivityNewIntent","(Landroid/app/Activity;Landroid/content/Intent;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ExpandableListActivity","onNewIntent","(Landroid/content/Intent;)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","onActivityNewIntent","(Landroid/app/Activity;Landroid/content/Intent;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/LauncherActivity","onNewIntent","(Landroid/content/Intent;)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","onActivityNewIntent","(Landroid/app/Activity;Landroid/content/Intent;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ListActivity","onNewIntent","(Landroid/content/Intent;)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","onActivityNewIntent","(Landroid/app/Activity;Landroid/content/Intent;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/NativeActivity","onNewIntent","(Landroid/content/Intent;)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","onActivityNewIntent","(Landroid/app/Activity;Landroid/content/Intent;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/TabActivity","onNewIntent","(Landroid/content/Intent;)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","onActivityNewIntent","(Landroid/app/Activity;Landroid/content/Intent;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/preference/PreferenceActivity","onNewIntent","(Landroid/content/Intent;)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","onActivityNewIntent","(Landroid/app/Activity;Landroid/content/Intent;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/Activity","onOptionsItemSelected","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","menuItemOnOptionsItemSelected","(Landroid/app/Activity;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/accounts/AccountAuthenticatorActivity","onOptionsItemSelected","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","menuItemOnOptionsItemSelected","(Landroid/app/Activity;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ActivityGroup","onOptionsItemSelected","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","menuItemOnOptionsItemSelected","(Landroid/app/Activity;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/AliasActivity","onOptionsItemSelected","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","menuItemOnOptionsItemSelected","(Landroid/app/Activity;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ExpandableListActivity","onOptionsItemSelected","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","menuItemOnOptionsItemSelected","(Landroid/app/Activity;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/LauncherActivity","onOptionsItemSelected","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","menuItemOnOptionsItemSelected","(Landroid/app/Activity;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ListActivity","onOptionsItemSelected","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","menuItemOnOptionsItemSelected","(Landroid/app/Activity;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/NativeActivity","onOptionsItemSelected","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","menuItemOnOptionsItemSelected","(Landroid/app/Activity;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/TabActivity","onOptionsItemSelected","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","menuItemOnOptionsItemSelected","(Landroid/app/Activity;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/preference/PreferenceActivity","onOptionsItemSelected","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","menuItemOnOptionsItemSelected","(Landroid/app/Activity;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ExpandableListActivity","onChildClick","(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z","com/growingio/android/sdk/autotrack/inject/ActivityInjector","expandableListActivityOnChildClick","(Landroid/app/ExpandableListActivity;Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ListActivity","onListItemClick","(Landroid/widget/ListView;Landroid/view/View;IJ)V","com/growingio/android/sdk/autotrack/inject/ActivityInjector","listActivityOnListItemClick","(Landroid/app/ListActivity;Landroid/widget/ListView;Landroid/view/View;IJ)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/content/DialogInterface${'$'}OnClickListener","onClick","(Landroid/content/DialogInterface;I)V","com/growingio/android/sdk/autotrack/inject/DialogInjector","dialogOnClick","(Landroid/content/DialogInterface${'$'}OnClickListener;Landroid/content/DialogInterface;I)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/app/Fragment","onResume","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnResume","(Landroid/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/DialogFragment","onResume","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnResume","(Landroid/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ListFragment","onResume","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnResume","(Landroid/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/preference/PreferenceFragment","onResume","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnResume","(Landroid/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/webkit/WebViewFragment","onResume","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnResume","(Landroid/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/Fragment","setUserVisibleHint","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentSetUserVisibleHint","(Landroid/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/DialogFragment","setUserVisibleHint","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentSetUserVisibleHint","(Landroid/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ListFragment","setUserVisibleHint","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentSetUserVisibleHint","(Landroid/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/preference/PreferenceFragment","setUserVisibleHint","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentSetUserVisibleHint","(Landroid/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/webkit/WebViewFragment","setUserVisibleHint","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentSetUserVisibleHint","(Landroid/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/Fragment","onHiddenChanged","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnHiddenChanged","(Landroid/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/DialogFragment","onHiddenChanged","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnHiddenChanged","(Landroid/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ListFragment","onHiddenChanged","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnHiddenChanged","(Landroid/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/preference/PreferenceFragment","onHiddenChanged","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnHiddenChanged","(Landroid/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/webkit/WebViewFragment","onHiddenChanged","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnHiddenChanged","(Landroid/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/Fragment","onDestroyView","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnDestroyView","(Landroid/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/DialogFragment","onDestroyView","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnDestroyView","(Landroid/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/app/ListFragment","onDestroyView","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnDestroyView","(Landroid/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/preference/PreferenceFragment","onDestroyView","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnDestroyView","(Landroid/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/webkit/WebViewFragment","onDestroyView","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","systemFragmentOnDestroyView","(Landroid/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("androidx/fragment/app/Fragment","onResume","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","androidxFragmentOnResume","(Landroidx/fragment/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("androidx/fragment/app/Fragment","setUserVisibleHint","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","androidxFragmentSetUserVisibleHint","(Landroidx/fragment/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("androidx/fragment/app/Fragment","onHiddenChanged","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","androidxFragmentOnHiddenChanged","(Landroidx/fragment/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("androidx/fragment/app/Fragment","onDestroyView","()V","com/growingio/android/sdk/autotrack/inject/FragmentInjector","androidxFragmentOnDestroyView","(Landroidx/fragment/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/support/v4/app/Fragment","onResume","()V","com/growingio/android/sdk/autotrack/inject/FragmentV4Injector","v4FragmentOnResume","(Landroid/support/v4/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/support/v4/app/Fragment","setUserVisibleHint","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentV4Injector","v4FragmentSetUserVisibleHint","(Landroid/support/v4/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/support/v4/app/Fragment","onHiddenChanged","(Z)V","com/growingio/android/sdk/autotrack/inject/FragmentV4Injector","v4FragmentOnHiddenChanged","(Landroid/support/v4/app/Fragment;Z)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/support/v4/app/Fragment","onDestroyView","()V","com/growingio/android/sdk/autotrack/inject/FragmentV4Injector","v4FragmentOnDestroyView","(Landroid/support/v4/app/Fragment;)V",true))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/Toolbar${'$'}OnMenuItemClickListener","onMenuItemClick","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/MenuItemInjector","toolbarOnMenuItemClick","(Landroid/widget/Toolbar${'$'}OnMenuItemClickListener;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/ActionMenuView${'$'}OnMenuItemClickListener","onMenuItemClick","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/MenuItemInjector","actionMenuViewOnMenuItemClick","(Landroid/widget/ActionMenuView${'$'}OnMenuItemClickListener;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/PopupMenu${'$'}OnMenuItemClickListener","onMenuItemClick","(Landroid/view/MenuItem;)Z","com/growingio/android/sdk/autotrack/inject/MenuItemInjector","popupMenuOnMenuItemClick","(Landroid/widget/PopupMenu${'$'}OnMenuItemClickListener;Landroid/view/MenuItem;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/view/View${'$'}OnClickListener","onClick","(Landroid/view/View;)V","com/growingio/android/sdk/autotrack/inject/ViewClickInjector","viewOnClick","(Landroid/view/View${'$'}OnClickListener;Landroid/view/View;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/SeekBar${'$'}OnSeekBarChangeListener","onStopTrackingTouch","(Landroid/widget/SeekBar;)V","com/growingio/android/sdk/autotrack/inject/ViewClickInjector","seekBarOnSeekBarChange","(Landroid/widget/SeekBar${'$'}OnSeekBarChangeListener;Landroid/widget/SeekBar;)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/RadioGroup${'$'}OnCheckedChangeListener","onCheckedChanged","(Landroid/widget/RadioGroup;I)V","com/growingio/android/sdk/autotrack/inject/ViewClickInjector","radioGroupOnChecked","(Landroid/widget/RadioGroup${'$'}OnCheckedChangeListener;Landroid/widget/RadioGroup;I)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/RatingBar${'$'}OnRatingBarChangeListener","onRatingChanged","(Landroid/widget/RatingBar;FZ)V","com/growingio/android/sdk/autotrack/inject/ViewClickInjector","ratingBarOnRatingBarChange","(Landroid/widget/RatingBar${'$'}OnRatingBarChangeListener;Landroid/widget/RatingBar;FZ)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/AdapterView${'$'}OnItemClickListener","onItemClick","(Landroid/widget/AdapterView;Landroid/view/View;IJ)V","com/growingio/android/sdk/autotrack/inject/ViewClickInjector","adapterViewOnItemClick","(Landroid/widget/AdapterView${'$'}OnItemClickListener;Landroid/widget/AdapterView;Landroid/view/View;IJ)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/AdapterView${'$'}OnItemSelectedListener","onItemSelected","(Landroid/widget/AdapterView;Landroid/view/View;IJ)V","com/growingio/android/sdk/autotrack/inject/ViewClickInjector","adapterViewOnItemSelected","(Landroid/widget/AdapterView${'$'}OnItemSelectedListener;Landroid/widget/AdapterView;Landroid/view/View;IJ)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/ExpandableListView${'$'}OnGroupClickListener","onGroupClick","(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z","com/growingio/android/sdk/autotrack/inject/ViewClickInjector","expandableListViewOnGroupClick","(Landroid/widget/ExpandableListView${'$'}OnGroupClickListener;Landroid/widget/ExpandableListView;Landroid/view/View;IJ)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/ExpandableListView${'$'}OnChildClickListener","onChildClick","(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z","com/growingio/android/sdk/autotrack/inject/ViewClickInjector","expandableListViewOnChildClick","(Landroid/widget/ExpandableListView${'$'}OnChildClickListener;Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)V",false))
    SUPER_HOOK_CLASSES.add(HookData("android/widget/CompoundButton${'$'}OnCheckedChangeListener","onCheckedChanged","(Landroid/widget/CompoundButton;Z)V","com/growingio/android/sdk/autotrack/inject/ViewClickInjector","compoundButtonOnChecked","(Landroid/widget/CompoundButton${'$'}OnCheckedChangeListener;Landroid/widget/CompoundButton;Z)V",false))
    return SUPER_HOOK_CLASSES
  }

  public fun initTargetClass(): MutableList<HookData> {
    TARGET_HOOK_CLASSES.clear()
    TARGET_HOOK_CLASSES.add(HookData("com/google/firebase/analytics/FirebaseAnalytics","logEvent","(Ljava/lang/String;Landroid/os/Bundle;)V","com/growingio/android/analytics/firebase/FirebaseAnalyticsInjector","logEvent","(Ljava/lang/String;Landroid/os/Bundle;)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/google/firebase/analytics/FirebaseAnalytics","setDefaultEventParameters","(Landroid/os/Bundle;)V","com/growingio/android/analytics/firebase/FirebaseAnalyticsInjector","setDefaultEventParameters","(Landroid/os/Bundle;)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/google/firebase/analytics/FirebaseAnalytics","setUserId","(Ljava/lang/String;)V","com/growingio/android/analytics/firebase/FirebaseAnalyticsInjector","setUserId","(Ljava/lang/String;)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/google/firebase/analytics/FirebaseAnalytics","setUserProperty","(Ljava/lang/String;Ljava/lang/String;)V","com/growingio/android/analytics/firebase/FirebaseAnalyticsInjector","setUserProperty","(Ljava/lang/String;Ljava/lang/String;)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/google/firebase/analytics/FirebaseAnalytics","setAnalyticsCollectionEnabled","(Z)V","com/growingio/android/analytics/firebase/FirebaseAnalyticsInjector","setAnalyticsCollectionEnabled","(Z)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/google/android/gms/analytics/GoogleAnalytics","newTracker","(I)Lcom/google/android/gms/analytics/Tracker;","com/growingio/android/analytics/google/GoogleAnalyticsInjector","newTracker","(Lcom/google/android/gms/analytics/Tracker;Lcom/google/android/gms/analytics/GoogleAnalytics;I)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/google/android/gms/analytics/GoogleAnalytics","newTracker","(Ljava/lang/String;)Lcom/google/android/gms/analytics/Tracker;","com/growingio/android/analytics/google/GoogleAnalyticsInjector","newTracker","(Lcom/google/android/gms/analytics/Tracker;Lcom/google/android/gms/analytics/GoogleAnalytics;Ljava/lang/String;)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/google/android/gms/analytics/GoogleAnalytics","setAppOptOut","(Z)V","com/growingio/android/analytics/google/GoogleAnalyticsInjector","setAppOptOut","(Lcom/google/android/gms/analytics/GoogleAnalytics;Z)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/google/android/gms/analytics/Tracker","set","(Ljava/lang/String;Ljava/lang/String;)V","com/growingio/android/analytics/google/GoogleAnalyticsInjector","set","(Lcom/google/android/gms/analytics/Tracker;Ljava/lang/String;Ljava/lang/String;)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/google/android/gms/analytics/Tracker","send","(Ljava/util/Map;)V","com/growingio/android/analytics/google/GoogleAnalyticsInjector","send","(Lcom/google/android/gms/analytics/Tracker;Ljava/util/Map;)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/google/android/gms/analytics/Tracker","setClientId","(Ljava/lang/String;)V","com/growingio/android/analytics/google/GoogleAnalyticsInjector","setClientId","(Lcom/google/android/gms/analytics/Tracker;Ljava/lang/String;)V",true))
    TARGET_HOOK_CLASSES.add(HookData("com/sensorsdata/analytics/android/sdk/SensorsDataAPI","disableSDK","()V","com/growingio/android/analytics/sensor/SensorAnalyticsInjector","disableSDK","()V",false))
    TARGET_HOOK_CLASSES.add(HookData("com/sensorsdata/analytics/android/sdk/SensorsDataAPI","enableSDK","()V","com/growingio/android/analytics/sensor/SensorAnalyticsInjector","enableSDK","()V",false))
    TARGET_HOOK_CLASSES.add(HookData("com/sensorsdata/analytics/android/sdk/AbstractSensorsDataAPI","trackEvent","(Lcom/sensorsdata/analytics/android/sdk/internal/beans/EventType;Ljava/lang/String;Lorg/json/JSONObject;Ljava/lang/String;)V","com/growingio/android/analytics/sensor/SensorAnalyticsInjector","trackEvent","(Lcom/sensorsdata/analytics/android/sdk/internal/beans/EventType;Ljava/lang/String;Lorg/json/JSONObject;Ljava/lang/String;)V",false))
    return TARGET_HOOK_CLASSES
  }

  public data class HookData(
    public val targetClassName: String,
    public val targetMethodName: String,
    public val targetMethodDesc: String,
    public val injectClassName: String,
    public val injectMethodName: String,
    public val injectMethodDesc: String,
    public val isAfter: Boolean,
  )
}
