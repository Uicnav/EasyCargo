package com.vantechinformatics.easycargo

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSString
import platform.Foundation.NSTimeZone
import platform.Foundation.autoupdatingCurrentLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.stringWithFormat
import platform.Foundation.systemTimeZone
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()


actual fun Double.format(digits: Int): String {
    // Apelăm funcția nativă din Objective-C/Swift
    return NSString.stringWithFormat("%.${digits}f", this)
}

actual fun Long.getFormattedDate(): String {
    val date = NSDate.dateWithTimeIntervalSince1970(this.toDouble())
    val formatter = NSDateFormatter()
    formatter.locale = NSLocale.autoupdatingCurrentLocale
    formatter.timeZone = NSTimeZone.systemTimeZone
    formatter.setLocalizedDateFormatFromTemplate("dd.MM.yy HH:mm")
    return formatter.stringFromDate(date)
}