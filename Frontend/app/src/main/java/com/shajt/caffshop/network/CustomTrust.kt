package com.shajt.caffshop.network

import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.decodeCertificatePem

/**
 * Custom trust for self signed server.
 */
object CustomTrust {

    private const val certString =
        "-----BEGIN CERTIFICATE-----\n" +
                "MIIF8TCCA9mgAwIBAgIUHm89Oo3ok88a63W2uwemjQRCiLMwDQYJKoZIhvcNAQEL\n" +
                "BQAwgYcxCzAJBgNVBAYTAkhVMREwDwYDVQQIDAhCdWRhcGVzdDERMA8GA1UEBwwI\n" +
                "QnVkYXBlc3QxDDAKBgNVBAoMA0JNRTEMMAoGA1UECwwDVklLMRAwDgYDVQQDDAdD\n" +
                "b21wU2VjMSQwIgYJKoZIhvcNAQkBFhVleGFtcGxlQGV4YW1wbGVzLnRlc3QwHhcN\n" +
                "MjExMTI3MTMwNjQ4WhcNMjIxMTI3MTMwNjQ4WjCBhzELMAkGA1UEBhMCSFUxETAP\n" +
                "BgNVBAgMCEJ1ZGFwZXN0MREwDwYDVQQHDAhCdWRhcGVzdDEMMAoGA1UECgwDQk1F\n" +
                "MQwwCgYDVQQLDANWSUsxEDAOBgNVBAMMB0NvbXBTZWMxJDAiBgkqhkiG9w0BCQEW\n" +
                "FWV4YW1wbGVAZXhhbXBsZXMudGVzdDCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCC\n" +
                "AgoCggIBALVLdg4sEttOW4bA5AJNrcw2wIopk528RjfnN3zR6GaCKkmYuwTM5ndl\n" +
                "6yGcwwvYp6R2fBLNXJC2Pi6W35j7gsCdn7O1fQWZJ7my0MfQey1cqIONt48BCMdS\n" +
                "/Zhf5Z0/Ks5WlI9DJOxO8t+ucdHaCYar9zEVTDi+9S6ngaIK4qNK2Y9rF5F/Fbo1\n" +
                "hvwrL/ri4iBjDjkzoYeJYR9J2pgEMy/Zd+PeY6a/R7UZv+3iLofG6pxFQvkAq0UQ\n" +
                "PiXQMAwlGkwrJEkc5/EbMbAwA70GWHp9m+562dofpYjUTTdVGhkIe+OnP46YOnAC\n" +
                "ngIcEdiga7C3Bn5qghG05X7ellxl8rXcsDSFWqWUNMB7EAr3t7jTtx7RcgnzX2D9\n" +
                "cV3NR2tMDl2f0G7eWX8Ox6Sn8HzTNm0Q+Roe8UCWAVLVqVwDfhMLnLFXZZvWysy+\n" +
                "g3fnfZeLGTli4VN5HxEBCxXOXBj/CNoVEdBI7ZKGKo6+7fBOZuZc/JrvbB22LKkU\n" +
                "TWL2zVOUhODienuikaVVHDp5qMf5r/mmBchW25tcRB9lghK+K8AuW9SJDYeaQtzA\n" +
                "6psg+9jAeDj2uVB2neYuNdHsxrMSxUgxTaGrHqbsL7fEKhl1qi7oY/knzPERr+qf\n" +
                "1YJEvBNmMnGuv8vhL/zJUaFTYCu+jKXjfTUcFrHjIObdfnqP+hs/AgMBAAGjUzBR\n" +
                "MB0GA1UdDgQWBBSTaKHGtJCZ+abpmlkOt9iBYL7tLjAfBgNVHSMEGDAWgBSTaKHG\n" +
                "tJCZ+abpmlkOt9iBYL7tLjAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBCwUA\n" +
                "A4ICAQCDAwVhTN6tUkc0L85PI/plmiY4gNGPW75s5+AB+kzZ+ksNW/ry0ypD7sJh\n" +
                "rM2esucSYheICRcaAaAPX8bePC1/l+gTFR4V5tkGSz14sCTOJtxrCighp8PEVj6I\n" +
                "GuUIMC4Zu8JxORFR0dbgca+QVXSU0Ziaf3Tgrk8oCxQhsDeCKJHIDu0kBts+MeBQ\n" +
                "ev8zrmSVQgi6blaj7bKk+81dPVp6ATKqajbMY4oAKaI8QqCliRDqWxHriEhR+ykZ\n" +
                "JtMpCtvYIymLfSvJ9z2qRMV13XjU6jgKDUleg3nHkt+og5q5kW0XArz9YHSo5dhb\n" +
                "5LnNhLZKTR20tzNb4mHQk8NWlzJoJRED7KZmw9yFbUIpDu3F3Zvcv2U1uFQfdnuq\n" +
                "nsoexDz+LYlxOSD/7lsWGar7lyJbpOq4wk/rH8zA1P2TtobfR2M8Kh7PN2KbA7Sh\n" +
                "kzXS3XCccxvyot/6Ibg2+QT+lgSYppuCfValb2sQLnwQlCJ+Co2DaihrwDNlSRSF\n" +
                "RPSAfwf8cstCzulvmV3q5b9l7Cc4lq+/yc4YxGu94pljHd1x3boSVR9ELvi7sK0N\n" +
                "SIZEQVE/FY+coED3eVqqnx1y9lmSB3R7impQNpxcOZwleCvuHaV8UvVVMDnOhJkc\n" +
                "h/hLtnQDaISBTZ8QGw0XxaXsR35Zt0Kl+GMNCteGtM9xhBUPlQ==\n" +
                "-----END CERTIFICATE-----\n"

    val certificate = HandshakeCertificates.Builder()
        .addTrustedCertificate(certString.decodeCertificatePem())
        .build()
}