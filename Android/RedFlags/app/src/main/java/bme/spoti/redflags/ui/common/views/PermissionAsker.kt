package bme.spoti.redflags.ui.common.views

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import bme.spoti.redflags.R
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.utils.isPermanantlyDenied
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.security.Permissions


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionAsker(
	permissions: List<String>,
	icon: Int,
	title: String,
	explanation: String
) {
	val permissionsState = rememberMultiplePermissionsState(permissions = permissions)
	AnimatedVisibility(!permissionsState.allPermissionsGranted) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.wrapContentHeight()
				.background(Colors.Sand, MaterialTheme.shapes.large)
				.padding(horizontal = PaddingSizes.looser, vertical = PaddingSizes.loose)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					painter = painterResource(id = icon),
					contentDescription = "permission icon",
					tint = Colors.DarkRed,
					modifier = Modifier
						.background(Colors.SandDarker, MaterialTheme.shapes.medium)
						.padding(PaddingSizes.loose)
				)
				Column(
					modifier = Modifier.padding(start = PaddingSizes.loose),
					verticalArrangement = Arrangement.Center
				) {
					Text(
						text = title,
						style = MaterialTheme.typography.button,
						color = Colors.DarkRed,
					)
					AnimatedVisibility(visible = permissionsState?.isPermanantlyDenied()==true) {
						Text(
							text = "Permanently denied",
							style = MaterialTheme.typography.subtitle2,
							color = Colors.TrueDarker,
							modifier = Modifier.offset(y = -PaddingSizes.default)
						)
					}
				}
			}
			Text(
				text = explanation,
				style = MaterialTheme.typography.h6,
				color = Colors.DarkRed,
				letterSpacing = (-1).sp,
				modifier = Modifier.padding(top = PaddingSizes.default)
			)
			val context = LocalContext.current
			Row(
				Modifier
					.fillMaxWidth()
					.padding(top = PaddingSizes.default), horizontalArrangement = Arrangement.End
			) {
				val textToButtonModifier = Modifier
					.background(Colors.PrimaryRed, MaterialTheme.shapes.large)
					.offset(y = (-1).dp)
					.clip(MaterialTheme.shapes.large)
				if (permissionsState?.isPermanantlyDenied()== true) {
					Text(
						text = "open settings",
						modifier = textToButtonModifier
							.clickable {
								val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								val uri: Uri = Uri.fromParts("package", context.packageName, null)
								intent.data = uri
								startActivity(context, intent, null)
							}
							.padding(
								vertical = PaddingSizes.tight,
								horizontal = PaddingSizes.loose
							),
						color = Colors.DarkRed
					)
				} else {
					Text(
						text = "grant",
						modifier = textToButtonModifier
							.clickable { permissionsState?.launchMultiplePermissionRequest() }
							.padding(
								vertical = PaddingSizes.tight,
								horizontal = PaddingSizes.loose
							),
						color = Colors.DarkRed
					)
				}
			}
		}
	}
}



@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview
fun PermissionAskerPreview(){
	RedFlagsTheme {
		val permissionsState = rememberMultiplePermissionsState(permissions = listOf(
			Manifest.permission.ACCESS_FINE_LOCATION
		))
		Column(
			Modifier
				.fillMaxSize()
				.background(Colors.LightRed)
				.padding(PaddingSizes.loose)
		) {

		}
	}
}