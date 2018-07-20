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