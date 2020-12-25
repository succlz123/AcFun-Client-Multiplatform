package org.succlz123.app.acfun.ui.main.tab.item

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.Manifest
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.vm.HomeAreaViewModel
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.screen.ScreenArgs
import org.succlz123.lib.screen.ScreenNavigator
import org.succlz123.lib.screen.operation.PushOptions

@Composable
fun MainHomeContentTitle(
    screenNavigator: ScreenNavigator, rememberSelectedItem: MutableState<Int>?, id: Int, name: String
) {
    Column {
        Card(
            modifier = Modifier.noRippleClickable {
                if (rememberSelectedItem?.value == 0) {
                    val find = HomeAreaViewModel.CATEGORY.find { it.id == id }
                    if (find != null) {
                        rememberSelectedItem.value = HomeAreaViewModel.CATEGORY.indexOf(find)
                    }
                } else {
                    screenNavigator.push(
                        Manifest.AreaContentScreen,
                        screenKey = id.toString(),
                        arguments = ScreenArgs.putValue("KEY_CATEGORY_NAME", name).putValue("KEY_CATEGORY_ID", id),
                        pushOptions = PushOptions(
                            removePredicate = PushOptions.RemoveAnyPredicate(Manifest.UserSpaceScreen)
                        )
                    )
                }
            }, elevation = 0.dp, backgroundColor = Color.Transparent
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(0.dp, 6.dp),
                    text = name,
                    maxLines = 1,
                    color = ColorResource.text,
                    style = MaterialTheme.typography.h3,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    Icons.Sharp.KeyboardArrowRight,
                    modifier = Modifier.size(16.dp),
                    contentDescription = "Right",
                    tint = ColorResource.text
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}
