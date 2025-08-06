package com.oneproofverifydummyapp

object VerificationConfig {
    // Verification app configuration
    const val VERIFICATION_APP_SCHEME = "oneproof://verify"
    const val VERIFICATION_APP_PACKAGE = "com.oneproof.verify.intent" // Replace with actual package
    
        // Data constants
    val NAME_SPACES_JSON = """
        {
            "org.iso.18013.5.1": {
                "family_name": false,
                "given_name": false,
                "portrait": false,
                "issuing_country": true,
                "birth_date": false,
                "issuing_authority": true
            },
            "org.iso.18013.5.1.aamva": {
                "sex": false,
                "race_ethnicity": false,
                "domestic_driving_privilege": true,
                "aamva_version": true
            }
        }
    """.trimIndent()
    
    const val ORG_ID = "plfS1eb12zCVVAerNLvRLfqmygzcr7g7"
    const val LICENSE_KEY = "WuSa0hoBiwsrhvVG1A46V9PAcArlbZFH"
    
    // Intent extras keys
    const val EXTRA_NAME_SPACES_JSON = "nameSpacesJson"
    const val EXTRA_ORG_ID = "orgID"
    const val EXTRA_LICENSE_KEY = "licenseKey"
    const val EXTRA_VERIFICATION_RESULT = "verification_result"
    const val EXTRA_VERIFICATION_STATUS = "verification_status"
    
    // Status constants
    const val STATUS_SUCCESS = "SUCCESS"
    const val STATUS_FAILED = "FAILED"
    const val STATUS_ERROR = "ERROR"
} 