package dev.dhiren.ultimateTurf.helper.base

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.dhiren.domain.enums.SecurityType

object SecurityFailedComposeScreen {
    
    @Composable
    fun SecurityFailedComposeUI(
        securityType: SecurityType,
        onExitApp: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Security Alert",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = getSecurityMessage(securityType),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onExitApp,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Exit App")
            }
        }
    }
    
    private fun getSecurityMessage(securityType: SecurityType): String {
        return when (securityType) {
            SecurityType.ROOT_DETECTED -> 
                "This device appears to be rooted. For security reasons, the app cannot run on rooted devices."
            SecurityType.EMULATOR_DETECTED -> 
                "This appears to be an emulator. For security reasons, the app cannot run on emulated devices."
            SecurityType.FRIDA_DETECTED -> 
                "Security threat detected. The app cannot run in this environment."
        }
    }
}
