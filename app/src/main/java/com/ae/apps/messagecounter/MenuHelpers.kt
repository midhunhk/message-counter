package com.ae.apps.messagecounter

import android.content.Context
import android.content.Intent


fun getShareIntent(context: Context): Intent {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.play_store_url))
    shareIntent.putExtra(Intent.EXTRA_TITLE, context.getString(R.string.menu_title_share))
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
    return shareIntent
}

fun getFeedbackIntent(context:Context):Intent{
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/html"
    intent.putExtra(Intent.EXTRA_EMAIL, context.getString(R.string.feedback_email_address))
    intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.str_about_share_feedback_subject))
    intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.str_about_share_feedback_body))
    return intent
}