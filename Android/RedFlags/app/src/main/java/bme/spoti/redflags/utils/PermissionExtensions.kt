package bme.spoti.redflags.utils

import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionStatus.isPermanentlyDenied() = !shouldShowRationale && !isGranted


@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.isPermanantlyDenied() = !shouldShowRationale && !allPermissionsGranted