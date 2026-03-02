package com.nendo.argosy.data.storage

import android.os.Environment

object StoragePathUtils {

    private val sdcardPattern = Regex("^/storage/([A-F0-9-]+)")

    /**
     * Resolves an absolute file path into a (volumeId, relativePath) pair suitable for
     * DocumentsContract URIs. Returns null for paths that cannot be mapped to a
     * documentable storage volume (e.g. app-private directories).
     */
    fun extractVolumeAndPath(path: String): Pair<String, String>? {
        @Suppress("DEPRECATION")
        val primaryRoot = Environment.getExternalStorageDirectory().absolutePath

        return when {
            path.startsWith(primaryRoot) -> {
                "primary" to path.removePrefix(primaryRoot).trimStart('/')
            }
            path.matches(Regex("^/storage/[A-F0-9-]+.*")) -> {
                val match = sdcardPattern.find(path)
                if (match != null) {
                    val volId = match.groupValues[1]
                    volId to path.removePrefix("/storage/$volId").trimStart('/')
                } else null
            }
            else -> null
        }
    }
}
