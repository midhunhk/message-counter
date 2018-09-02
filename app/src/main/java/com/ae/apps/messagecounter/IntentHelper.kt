package com.ae.apps.messagecounter

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ae.apps.messagecounter.receivers.CounterWidgetReceiver
import com.ae.apps.messagecounter.services.SMSObserverService
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity


fun getShareIntent(context: Context): Intent {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.app_play_store_url))
    shareIntent.putExtra(Intent.EXTRA_TITLE, context.getString(R.string.menu_title_share))
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
    return shareIntent
}

fun getFeedbackIntent(context: Context): Intent {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.type = "text/html"
    intent.data = Uri.parse("mailto:" + context.getString(R.string.app_feedback_email_address))
    intent.putExtra(Intent.EXTRA_EMAIL, context.getString(R.string.app_feedback_email_address))
    intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.str_about_share_feedback_subject))
    intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.str_about_share_feedback_body))
    return intent
}

fun getWidgetUpdateIntent(context: Context): Intent {
    val intent = Intent(context, CounterWidgetReceiver::class.java)
    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    // Get all the widgetIds
    val appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
            ComponentName(context, CounterWidgetReceiver::class.java))
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
    return intent
}

fun getStartActivityIntent(context:Context, requestCode:Int): PendingIntent{
    val resultIntent = Intent(context, MainActivity::class.java)
    return PendingIntent
            .getActivity(context, requestCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun getOpenSourceLicenceDisplayIntent(context: Context): Intent {
    return Intent(context, OssLicensesMenuActivity::class.java)
}

fun getViewSourceIntent(context: Context): Intent {
    return createIntentForURI(context.getString(R.string.app_github_source_url))
}

fun getViewFaqIntent(context: Context): Intent {
    return createIntentForURI(context.getString(R.string.app_faq_url))
}

fun getMessageCounterServiceIntent(context: Context): Intent {
    return Intent(context, SMSObserverService::class.java)
}

private fun createIntentForURI(url: String): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    return intent
}