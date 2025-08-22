package com.oneproofverifydummyapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    
    private var verificationResult by mutableStateOf("")
    private var verificationStatus by mutableStateOf("")
    private var isLoading by mutableStateOf(false)
    private var hasReceivedResult by mutableStateOf(false)
    private var selectedScannerInfo by mutableStateOf(VerificationConfig.SCANNER_AV3)

    private val verificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isLoading = false
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            verificationResult = data?.getStringExtra(VerificationConfig.EXTRA_VERIFICATION_RESULT)
                ?: "No result received"
            verificationStatus = data?.getStringExtra(VerificationConfig.EXTRA_VERIFICATION_STATUS)
                ?: "No status"
            hasReceivedResult = true
            
            Log.i("MainActivity", "Received verification_result: $verificationResult")
            Log.i("MainActivity", "Received verification_status: $verificationStatus")
        } else {
            verificationResult = "Verification was cancelled or failed"
            verificationStatus = VerificationConfig.STATUS_FAILED
            hasReceivedResult = true
            Log.w("MainActivity", "Verification was cancelled or failed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentResult = intent.getStringExtra(VerificationConfig.EXTRA_VERIFICATION_RESULT)
        val intentStatus = intent.getStringExtra(VerificationConfig.EXTRA_VERIFICATION_STATUS)
        
        if (intentResult != null && intentStatus != null) {
            verificationResult = intentResult
            verificationStatus = intentStatus
            hasReceivedResult = true
            Log.i("MainActivity", "Received verification_result from intent: $verificationResult")
            Log.i("MainActivity", "Received verification_status from intent: $verificationStatus")
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        verificationResult = verificationResult,
                        verificationStatus = verificationStatus,
                        isLoading = isLoading,
                        hasReceivedResult = hasReceivedResult,
                        selectedScannerInfo = selectedScannerInfo,
                        onScannerSelectionChanged = { scannerInfo -> selectedScannerInfo = scannerInfo },
                        onTriggerVerification = { triggerVerification() }
                    )
                }
            }
        }
    }

    private fun triggerVerification() {
        isLoading = true
        try {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                 setPackage(VerificationConfig.VERIFICATION_APP_PACKAGE)

                putExtra(
                    VerificationConfig.EXTRA_NAME_SPACES_JSON,
                    VerificationConfig.NAME_SPACES_JSON
                )
                putExtra(VerificationConfig.EXTRA_ORG_ID, VerificationConfig.ORG_ID)
                putExtra(VerificationConfig.EXTRA_LICENSE_KEY, VerificationConfig.LICENSE_KEY)
                putExtra(VerificationConfig.EXTRA_SCANNER_INFO, selectedScannerInfo)
                //this new variable added for app packageName
                putExtra(VerificationConfig.EXTRA_PACKAGE_NAME, VerificationConfig.packageName)

                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            Log.i(
                "MainActivity",
                "Triggering verification with orgID: ${VerificationConfig.ORG_ID}, scannerInfo: $selectedScannerInfo"
            )
            Log.i("MainActivity", "NameSpaces JSON: ${VerificationConfig.NAME_SPACES_JSON}")

            verificationLauncher.launch(intent)

        } catch (e: Exception) {
            Log.e("MainActivity", "Error triggering verification: ${e.message}", e)
            verificationResult = "Error triggering verification: ${e.message}"
            verificationStatus = VerificationConfig.STATUS_ERROR
        } finally {
            isLoading = false
        }
    }
}
@Preview()
@Composable
fun MainScreen(
    verificationResult: String,
    verificationStatus: String,
    isLoading: Boolean,
    hasReceivedResult: Boolean,
    selectedScannerInfo: String,
    onScannerSelectionChanged: (String) -> Unit,
    onTriggerVerification: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!hasReceivedResult) {
            WelcomeScreen(
                isLoading = isLoading,
                selectedScannerInfo = selectedScannerInfo,
                onScannerSelectionChanged = onScannerSelectionChanged,
                onTriggerVerification = onTriggerVerification
            )
        } else {
            ResultScreen(
                verificationResult = verificationResult,
                verificationStatus = verificationStatus,
                isLoading = isLoading,
                onTriggerVerification = onTriggerVerification
            )
        }
    }
}

@Composable
fun WelcomeScreen(
    isLoading: Boolean,
    selectedScannerInfo: String,
    onScannerSelectionChanged: (String) -> Unit,
    onTriggerVerification: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔐",
                    fontSize = 64.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = stringResource(R.string.welcome_message),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = stringResource(R.string.welcome_description),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = stringResource(R.string.scanner_selection_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onScannerSelectionChanged(VerificationConfig.SCANNER_AV3) },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedScannerInfo == VerificationConfig.SCANNER_AV3) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                width = if (selectedScannerInfo == VerificationConfig.SCANNER_AV3) 2.dp else 1.dp,
                                color = if (selectedScannerInfo == VerificationConfig.SCANNER_AV3)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.scanner_av3),
                                fontSize = 12.sp,
                                color = if (selectedScannerInfo == VerificationConfig.SCANNER_AV3) 
                                    MaterialTheme.colorScheme.onPrimary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Button(
                            onClick = { onScannerSelectionChanged(VerificationConfig.SCANNER_MOBILE) },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedScannerInfo == VerificationConfig.SCANNER_MOBILE) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                width = if (selectedScannerInfo == VerificationConfig.SCANNER_MOBILE) 2.dp else 1.dp,
                                color = if (selectedScannerInfo == VerificationConfig.SCANNER_MOBILE)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.scanner_mobile),
                                fontSize = 12.sp,
                                color = if (selectedScannerInfo == VerificationConfig.SCANNER_MOBILE) 
                                    MaterialTheme.colorScheme.onPrimary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { onScannerSelectionChanged(VerificationConfig.SCANNER_NO_NFC) },
                            modifier = Modifier.width(120.dp).height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedScannerInfo == VerificationConfig.SCANNER_NO_NFC) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                width = if (selectedScannerInfo == VerificationConfig.SCANNER_NO_NFC) 2.dp else 1.dp,
                                color = if (selectedScannerInfo == VerificationConfig.SCANNER_NO_NFC)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.scanner_no_nfc),
                                fontSize = 12.sp,
                                color = if (selectedScannerInfo == VerificationConfig.SCANNER_NO_NFC) 
                                    MaterialTheme.colorScheme.onPrimary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onTriggerVerification,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isLoading) "Starting..." else stringResource(R.string.start_verification),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        
        VerificationResultAwaitedCard()
    }
}

@Composable
fun ResultScreen(
    verificationResult: String,
    verificationStatus: String,
    isLoading: Boolean,
    onTriggerVerification: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        VerificationResultScreen(
            verificationResult = verificationResult,
            verificationStatus = verificationStatus
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onTriggerVerification,
            enabled = !isLoading,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .height(56.dp)
        ) {
            Text(
                text = if (isLoading) "Verifying..." else stringResource(R.string.verify_again),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VerificationResultScreen(
    verificationResult: String,
    verificationStatus: String,
) {
    val isSuccess = verificationStatus == VerificationConfig.STATUS_SUCCESS
    val statusText = if (isSuccess) stringResource(R.string.verification_successful) else stringResource(R.string.verification_failed)
    val statusColor = if (isSuccess) Color(0xFF00C853) else Color.Red

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isSuccess) "✅" else "❌",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = statusText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(vertical = 16.dp)
            )
            
            Text(
                text = stringResource(R.string.result_label),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = verificationResult,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun VerificationResultAwaitedCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⏳",
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = stringResource(R.string.verification_result_awaited),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = stringResource(R.string.verification_result_awaited_status),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = stringResource(R.string.verification_result_awaited_desc),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}