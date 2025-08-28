package com.oneproofverifydummyapp

object VerificationConfig {
    const val VERIFICATION_APP_SCHEME = "oneproof://verify"
    const val VERIFICATION_APP_PACKAGE = "com.oneproof.verify.intent"
    
    const val SCANNER_AV3 = "AV3"
    const val SCANNER_MOBILE = "Mobile"
    const val SCANNER_NO_NFC = "NoNFC"
    
//    val NAME_SPACES_JSON = """
//        {
//            "org.iso.18013.5.1": {
//                "family_name": false,
//                "given_name": false,
//                "portrait": false,
//                "issuing_country": true,
//                "birth_date": false,
//                "issuing_authority": true
//            },
//            "org.iso.18013.5.1.aamva": {
//                "sex": false,
//                "race_ethnicity": false,
//                "domestic_driving_privilege": true,
//                "aamva_version": true
//            }
//        }
//    """.trimIndent()

    val NAME_SPACES_JSON = """
        {
            "org.iso.18013.5.1": {
                "family_name": false,
                "given_name": false,
                "portrait": false,
                "issuing_country": true,
                "birth_date": false,
                "issuing_authority": true
            }
        }
    """.trimIndent()
    
    const val ORG_ID = "palceholder"
    const val LICENSE_KEY = "placeholder"

    const val EXTRA_NAME_SPACES_JSON = "nameSpacesJson"
    const val EXTRA_ORG_ID = "orgID"
    const val EXTRA_LICENSE_KEY = "licenseKey"
    const val EXTRA_SCANNER_INFO = "scannerInfo"
    const val EXTRA_PACKAGE_NAME = "packageName"
    const val EXTRA_DEVICE_ENGAGEMENT = "deviceEngagement"
    const val EXTRA_VERIFICATION_RESULT = "verification_result"
    const val EXTRA_VERIFICATION_STATUS = "verification_status"
    
    const val STATUS_SUCCESS = "SUCCESS"
    const val STATUS_FAILED = "FAILED"
    const val STATUS_ERROR = "ERROR"
} 