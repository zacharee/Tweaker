package com.zacharee1.systemuituner.data

data class BlacklistBackupInfo(
    val items: HashSet<String>,
    val customItems: HashSet<CustomBlacklistItemInfo>
)